/* 
 * Copyright 2016 BananaRama.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bananarama.cache;

import org.bananarama.cache.annotation.Indexed;
import org.bananarama.cache.annotation.BufferedOnIndexedCollection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.index.Index;
import com.googlecode.cqengine.query.Query;
import static com.googlecode.cqengine.query.QueryFactory.*;
import com.googlecode.cqengine.resultset.ResultSet;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.BananaRama;
import org.bananarama.annotation.Banana;
import org.bananarama.concurrency.StripedLock;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import org.bananarama.crud.Adapter;
import org.bananarama.crud.sql.accessor.FieldAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.apache.log4j.Logger;
import org.bananarama.annotation.BananaRamaAdapter;
import org.bananarama.cache.providers.collection.IndexedCollectionProvider;

/**
 * 
 * @author Guglielmo De Concini
 */
@BananaRamaAdapter(requires = BananaRama.class)
public final class IndexedCollectionAdapter implements Adapter<Object> {
    
    private static final String CACHE_NAME = "bananarama:indexed:buffers";
    private final CacheManager cacheManager;
    private final Ehcache cache;
    private final static Logger log = Logger.getLogger(IndexedCollectionAdapter.class);
    private final StripedLock slock = new StripedLock(64);
    private static final int ATTRIBUTE_MODIFIERS =  Modifier.PUBLIC
            | Modifier.FINAL
            | Modifier.STATIC;
    private final BananaRama parent;
    
    public IndexedCollectionAdapter(BananaRama parent){
        //This must be configurable in the future
        final Configuration conf = ConfigurationFactory
                .parseConfiguration();
        
        conf.getDefaultCacheConfiguration()
                .getPersistenceConfiguration()
                .strategy(PersistenceConfiguration.Strategy.NONE);
        
        cacheManager =  CacheManager.newInstance(conf);
        cache = cacheManager.addCacheIfAbsent(CACHE_NAME);
        this.parent = parent;
    }
    
    @SuppressWarnings("unchecked")
    private   <T> Adapter<? super T> getBackingAdapter(Class<T> clazz){
        //Class must be annotated, it must have been
        //checked before, otherwise it's a bug
        Adapter<?> backingAdapter;
        
        if( (  backingAdapter= parent.using(clazz.getAnnotation(Banana.class).adapter())) == null)
            throw new NullPointerException("The backing adapter for class " + clazz.getName() + " cannot be found. This should never happen");

        return (Adapter<? super T>)backingAdapter;
    }
    
    @SuppressWarnings("unchecked")
    private <T> IndexedCollection<T> getCollection(Class<T> clazz){
        Lock lock = slock.getLock(clazz);
        lock.lock();
        Element value = cache.get(clazz);
        try{
            
            if(value == null){
                final BufferedOnIndexedCollection typeAnno = clazz.getAnnotation(BufferedOnIndexedCollection.class);
                log.info("Starting buffering of " + clazz.getName());
                final IndexedCollection<T> tmpColl;
                final IndexedCollectionProvider<?> collectionProvider;
                //Retrieve collection provider from annotation
                try{
                    collectionProvider = typeAnno.provider().newInstance();
                    tmpColl = typeAnno.provider().newInstance().buildCollection();
                }
                catch(IllegalAccessException | InstantiationException ex){
                    throw new IllegalArgumentException(ex);
                }
                
                /*
                Load elements from the backingAdapter
                the operation is blocking, since the
                method which instantiates the Adapter
                does so
                */
                tmpColl.addAll(getBackingAdapter(clazz).read(clazz)
                        .all().collect(Collectors.toList()));
                        
                
                log.debug("Buffer size for " + clazz.getName() + " is " + tmpColl.size() + " after startup");
                
                log.debug("Building indexes for " + clazz.getName());
                
                final Collection<Field> fields;
                final Predicate<Field> fieldFilter = field -> 
                        (field.getModifiers() & ATTRIBUTE_MODIFIERS) == ATTRIBUTE_MODIFIERS
                                && field.isAnnotationPresent(Indexed.class)
                                && Attribute.class.isAssignableFrom(field.getType());
                
                if(typeAnno.inheritFields())
                  fields = FieldAccessor.getAttributeFieldsRecursive(clazz,fieldFilter).values();
                else
                    fields = FieldAccessor.getAttributeFieldsForClass(clazz,fieldFilter).values();
                
                //Find all attributes on which we need to build indexes
                for(Field field :fields){
                    try {
                        //Build the index on the attribute
                        Indexed anno = field.getAnnotation(Indexed.class);
                        Attribute<T,?> attr = (Attribute<T,?>)field.get(null);
                        Index<T> index = anno.value().newInstance().getIndex(attr);
                        log.debug(
                                index.getClass().getName()
                                        + " will be added on attribute " + attr.getAttributeName()
                                        + " for class " +clazz.getName());
                        tmpColl.addIndex(index);
                        
                    } catch (IllegalArgumentException | IllegalAccessException | InstantiationException ex) {
                        log.warn("Can't add index on attribute " + field.getName(), ex);
                    }
                }
                
                final int timeToLive = collectionProvider.timeToLive();
                final int timeToIdle = collectionProvider.timeToIdle();
                
                //Either non-zero values are provided for 
                //TTL and TTI through the annotation 
                //or the element is eternal
                if(timeToLive >= 0 && timeToIdle >= 0)
                    value = new Element(clazz,tmpColl, timeToIdle, timeToLive);
                else
                    value = new Element(clazz, tmpColl, true);
                
                this.cache.put(value);
                
                log.info("Cache initialization for " + clazz.getName() + " completed ");

            }
        }
        finally{
            lock.unlock();
        }
        
        return (IndexedCollection < T >)value.getObjectValue();
    }
    
    
    /**
     * Clear the cache
     */
    public void clear() {
        //Clear single elements
        cache.getAll(cache.getKeys())
                .values()
                .stream()
                .map(elem -> (IndexedCollection<?>)elem.getObjectValue())
                .forEach(IndexedCollection::clear);
        //Dispose the cache
        cache.dispose();
    }
    
    @Override
    public <T> CreateOperation<T> create(Class<T> clazz) {
        return new CreateOperation<T>() {
            
            private IndexedCollection<T> coll = getCollection(clazz);
            
            /**
             * Add elements to internal collection and send
             * them to underlying layer
             * @param data
             * @return
             */
            @Override
            public CreateOperation<T> from(Stream<T> data) {
                List<T> buf = data.collect(Collectors.toList());
                //Add elements to underlying layer
                getBackingAdapter(clazz).create(clazz).from(buf.stream());
                //And then to collection
                coll.addAll(buf);
                return this;
            }
            
            /**
             * Same as {@link #from(java.util.stream.Stream) but it also
             * passes the {@link QueryOptions to the underlying layer}
             * @param data
             * @param options
             * @return
             */
            @Override
            public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
                List<T> buf = data.collect(Collectors.toList());
                //Add elements to underlying layer
                getBackingAdapter(clazz).create(clazz).from(buf.stream(),options);
                //And then to collection
                coll.addAll(buf);
                return this;
            }

            @Override
            public void close() throws Exception {

            }
        };
    }
    
    @Override
    public <T> ReadOperation<T> read(Class<T> clazz) {
        
        return new ReadOperation<T>() {
            
            private IndexedCollection<T> coll = getCollection(clazz);
            /**
             * Returns all the elements that were loaded into
             * the internal collection
             * @return
             */
            @Override
            public Stream<T> all() {
                return (Stream<T>) coll.stream();
            }
            
            /**
             * Returns all the elements directly fromKeys the underlying layer
             * passing the given query options..
             * @param options
             * @return
             */
            @Override
            public Stream<T> all(QueryOptions options) {
                return getBackingAdapter(clazz).read(clazz).all(options);
            }
            
            /**
             * Returns all the elements contained in the internal
             * collection that match the given {@link Query}
             * @param <Q>
             * @param obj
             * @return a {@link Stream} of results
             */
            @Override @SuppressWarnings({ "unchecked", "rawtypes" })
            public <Q> Stream<T> where(Q obj) {
                if(obj instanceof Query){
                    Query<T> query = (Query<T>)obj;
                    ResultSet rs =  coll.retrieve(query);
                    List<T> buf = (List<T>) StreamSupport.stream(rs.spliterator(),false).collect(Collectors.toList());
                    rs.close();
                    return buf.stream();
                }
                
                throw new IllegalArgumentException(getClass().getName() +" does not support " + obj.getClass().getName() + " for querying");
            }
            
            /**
             * Same as {@link #where(java.lang.Object) }, but it
             * also passes the given {@link QueryOptions} to the internal collection
             * when querying it
             * @param <Q>
             * @param obj
             * @param options
             * @return
             */
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public <Q> Stream<T> where(Q obj, QueryOptions options) {
                if(obj instanceof Query){
                    Query<T> query = (Query<T>)obj;
                    ResultSet rs =  coll.retrieve(query);
                    List<T> buf = (List<T>) StreamSupport.stream(rs.spliterator(),false).collect(Collectors.toList());
                    rs.close();
                    return buf.stream();
                }
                throw new IllegalArgumentException(getClass().getName() +" does not support " + obj.getClass().getName() + " for querying");
            }

            @Override
            public Stream<T> fromKeys(List<?> keys) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void close() throws Exception {

            }
        };
    }
    
    @Override
    public <T> UpdateOperation<T> update(Class<T> clazz) {
        return new UpdateOperation<T>() {
            
            private IndexedCollection<T> coll = getCollection(clazz);
            
            /**
             * Replaces all the elements in the {@link Stream} in the
             * internal collection and updates them also
             * on the underlying layer
             * @param data
             * @return
             */
            @Override
            public UpdateOperation<T> from(Stream<T> data) {
                return from(data,noQueryOptions());
            }
            
            /**
             * Same as {@link #from(java.util.stream.Stream) but passes
             * the given {@link QueryOptions} to the underlying layer
             * @param data
             * @param options
             * @return
             */
            @SuppressWarnings("unchecked")
            @Override
            public UpdateOperation<T> from(Stream<T> data, QueryOptions options) {
                List<T> buf = data.collect(Collectors.toList());
                //Update elements in collection
                coll.removeAll(buf);
                coll.update(Collections.EMPTY_LIST, buf, options);
                
                //Update elements on underlying layer
                if(options != null)
                    getBackingAdapter(clazz).update(clazz).from(buf.stream(),options);
                else
                    getBackingAdapter(clazz).update(clazz).from(buf.stream());
                
                return this;
            }

            @Override
            public void close() throws Exception {

            }
        };
    }
    
    @Override
    public <T> DeleteOperation<T> delete(Class<T> clazz) {
        
        return new DeleteOperation<T>() {
            
            private IndexedCollection<T> coll = getCollection(clazz);
            
            /**
             * Removes all elements fromKeys internal collection
             * and underlying layer that match the given predicate.
             * 
             * @param <Q> accepted types are: {@link Query}
             * @param obj
             * @return the {@link DeleteOperation}
             */
            @Override
            public <Q> DeleteOperation<T> where(Q obj) {
                return where(obj, noQueryOptions());
            }
            
            /**
             * Same as {@link #where(java.lang.Object)}, but passes the
             * given {@link QueryOptions} to the underlying layer
             * @param <Q> accepted types are: {@link Query}
             * @param whereClaus
             * @param options
             * @return
             */
            @SuppressWarnings("unchecked")
            @Override
            public <Q> DeleteOperation<T> where(Q obj, QueryOptions options) {
                //Retrieve elements that match query and remove them
                if(obj instanceof Query){
                    Query<T> query = (Query<T>)obj;
                    getBackingAdapter(clazz).delete(clazz)
                            .where(obj,options);
                    
                    Collection<T> complement = StreamSupport
                            .stream(coll.retrieve(not(query)).spliterator(),false).collect(Collectors.toList());

                    coll.clear();
                    coll.addAll(complement);
                    
                    return this;
                }
                
                throw new IllegalArgumentException(getClass().getName() 
                        + " does not support " + obj.getClass().getName() 
                        + " for deleting");
            }
            
            /**
             * Remove all elements in internal collection
             * and underlying layer.
             * @param data
             * @return
             */
            @Override
            public DeleteOperation<T> from(Stream<T> data) {
                return from(data,noQueryOptions());
            }
            
            /**
             * Same as {@link #from(java.util.stream.Stream) but passes
             * the given {@link QueryOptions} to the underlying layer
             * @param data
             * @param options
             * @return
             */
            @Override
            public DeleteOperation<T> from(Stream<T> data, QueryOptions options) {
                List<T> buf = data.collect(Collectors.toList());
                //Remove all elements fromKeys collection
                coll.removeAll(buf);
                
                //Remove all elements fromKeys underlying layer
                    getBackingAdapter(clazz).delete(clazz)
                            .from((Stream<T>) buf.stream());
                
                return this;
            }

            @Override
            public void close() throws Exception {

            }
        };
    }
    
}

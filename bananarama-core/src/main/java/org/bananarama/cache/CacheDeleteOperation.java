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

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import static com.googlecode.cqengine.query.QueryFactory.noQueryOptions;
import static com.googlecode.cqengine.query.QueryFactory.not;
import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.bananarama.BananaRama;
import org.bananarama.crud.DeleteOperation;

/**
 *
 * @author Guglielmo De Concini
 */
public class CacheDeleteOperation<T> extends AbstractCacheOperation<T> implements DeleteOperation<T>{
    
    public CacheDeleteOperation(IndexedCollection<T>coll,Class<T> clazz,BananaRama root){
        super(coll, clazz,root);
    }
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
            
            try(Stream<T> stream = StreamSupport
                    .stream(coll.retrieve(not(query)).spliterator(),false)){
                Collection<T> complement = stream.collect(Collectors.toList());
                
                coll.clear();
                coll.addAll(complement);
                
                return this;
            }
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
                .from(buf.stream());
        
        return this;
    }
    
    @Override
    public void close() throws IOException {
        
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public DeleteOperation<T> all() {

        getBackingAdapter(clazz).delete(clazz).all();
        coll.clear();
        return this;
    }
}

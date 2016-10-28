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
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.bananarama.BananaRama;
import org.bananarama.crud.ReadOperation;

/**
 *
 * @author Guglielmo De Concini
 */
public class CacheReadOperation<T>  extends AbstractCacheOperation<T> implements ReadOperation<T>{
    
    public CacheReadOperation(IndexedCollection<T>coll,Class<T> clazz,BananaRama root){
        super(coll, clazz,root);
    }
    /**
     * Returns all the elements that were loaded into
     * the internal collection
     * @return
     */
    @Override
    public Stream<T> all() {
        return coll.stream();
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
            ResultSet rs =coll.retrieve(query);
            
            try(Stream<T> stream = StreamSupport.stream(rs.spliterator(),false)){
                List<T> buf = stream.collect(Collectors.toList());
                return buf.stream();
            }
            finally{
                rs.close();
            }
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
            
            try(Stream<T> stream = StreamSupport.stream(rs.spliterator(),false)){
                List<T> buf = stream.collect(Collectors.toList());
                return buf.stream();
            }
            finally{
                rs.close();
            }
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
    public void close() throws IOException {
        
    }
}

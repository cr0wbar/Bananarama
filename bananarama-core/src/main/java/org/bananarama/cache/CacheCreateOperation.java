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
import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bananarama.BananaRama;
import org.bananarama.crud.CreateOperation;

/**
 *
 * @author Guglielmo De Concini
 */
public class CacheCreateOperation<T> extends AbstractCacheOperation<T> implements CreateOperation<T>{
             
    public CacheCreateOperation(IndexedCollection<T>coll,Class<T> clazz,BananaRama root){
        super(coll, clazz,root);
    }
    
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
    public void close() throws IOException {
        
    }

    
}

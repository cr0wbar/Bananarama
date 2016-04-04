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
package org.bananarama.crud.util;

import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import org.bananarama.crud.Adapter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
@SuppressWarnings("unchecked")
public abstract class WeldingAdapter<S> implements Adapter<S>{
    
    private final List<Adapter<S>> adapters;
    
    public WeldingAdapter(Adapter<S>...  adapters){
        if(adapters == null || adapters.length < 2)
            throw new IllegalArgumentException("Welding requires at least 2 adapters");
        
        this.adapters = Arrays.asList(adapters);    
    }
    
    @Override
    public <T extends S> CreateOperation<T> create(Class<T> clazz) {
        
        return new CreateOperation<T>() {
            @Override
            public CreateOperation<T> from(Stream<T> data) {
                
                adapters.parallelStream()
                .forEach(adapter -> adapter.create(clazz).from(data));

                return this;
            }
            
            @Override
            public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
                
                adapters.parallelStream()
                .forEach(adapter -> adapter.create(clazz).from(data,options));

                return this;
            }

            @Override
            public void close() throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        
    }
    
    @Override
    public <T extends S> ReadOperation<T> read(Class<T> clazz) {
        return new ReadOperation<T>() {
            @Override
            public Stream<T> all() {
                 return adapters.parallelStream()
                         .flatMap(adapter -> adapter.read(clazz).all());              
            }

            @Override
            public Stream<T> all(QueryOptions options) {
               return adapters.parallelStream()
                       .flatMap(adapter -> adapter.read(clazz).all(options));
            }

            @Override
            public <Q> Stream<T> where(Q whereClause) {
                return adapters.parallelStream()
                        .flatMap(adapter -> adapter.read(clazz).where(whereClause));
            }

            @Override
            public <Q> Stream<T> where(Q whereClause, QueryOptions options) {
                return adapters.parallelStream()
                        .flatMap(adapter -> adapter.read(clazz).where(whereClause,options));
            }

            @Override
            public Stream<T> fromKeys(List<?> keys) {
                return adapters.parallelStream()
                        .flatMap(adapter -> adapter.read(clazz).fromKeys(keys));
            }

            @Override
            public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
                return adapters.parallelStream()
                        .flatMap(adapter -> adapter.read(clazz).fromKeys(keys,options));
            }

            @Override
            public void close() throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
    @Override
    public <T extends S> UpdateOperation<T> update(Class<T> clazz) {
        return new UpdateOperation<T>() {

            @Override
            public UpdateOperation<T> from(Stream<T> data) {
                
                adapters.parallelStream()
                .forEach(adapter -> adapter.update(clazz).from(data));

                return this;   
            }

            @Override
            public UpdateOperation<T> from(Stream<T> data, QueryOptions options) {
                
                adapters.parallelStream()
                .forEach(adapter -> adapter.update(clazz).from(data, options));

                return this;   
            }

            @Override
            public void close() throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
    @Override
    public <T extends S> DeleteOperation<T> delete(Class<T> clazz) {
        return new DeleteOperation<T>() {
            @Override
            public <Q> DeleteOperation<T> where(Q whereClaus) {
                
                adapters.parallelStream()
                        .forEach(adapter -> adapter.delete(clazz).where(whereClaus));
                
                return this;
            }

            @Override
            public <Q> DeleteOperation<T> where(Q whereClaus, QueryOptions options) {

                adapters.parallelStream()
                        .forEach(adapter -> adapter.delete(clazz).where(whereClaus));
                
                return this;
            }

            @Override
            public DeleteOperation<T> from(Stream<T> data) {

                adapters.parallelStream()
                        .forEach(adapter -> adapter.delete(clazz).from(data));
                
                return this;
            }

            @Override
            public DeleteOperation<T> from(Stream<T> data, QueryOptions options) {
                
                adapters.parallelStream()
                        .forEach(adapter -> adapter.delete(clazz).from(data,options));
                
                return this;
            }

            @Override
            public void close() throws Exception {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
}

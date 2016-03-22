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
import org.bananarama.crud.Adapter;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import java.util.List;
import java.util.stream.Stream;

/**
 * NoOp Adapter. Useful for short-circuiting in an adapter chain
 * @author Guglielmo De Concini
 */
public final class NoOpAdapter implements Adapter<Object>{
    
    @Override
    public <T> CreateOperation<T> create(Class<T> clazz) {
        return new CreateOperation<T>() {
            @Override
            public CreateOperation<T> from(Stream<T> data) {
                return this;
            }
            
            @Override
            public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
                return this;
            }
        };
    }
    
    @Override
    public <T> ReadOperation<T> read(Class<T> clazz) {
        return new ReadOperation<T>() {
            @Override
            public Stream<T> all() {
                return Stream.empty();
            }
            
            @Override
            public Stream<T> all(QueryOptions options) {
                return Stream.empty();
            }
            
            @Override
            public <Q> Stream<T> where(Q whereClause) {
                return Stream.empty();
            }
            
            @Override
            public <Q> Stream<T> where(Q whereClause, QueryOptions options) {
                return Stream.empty();
            }
            
            @Override
            public Stream<T> fromKeys(List<?> keys) {
                return Stream.empty();
            }
            
            @Override
            public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
                return Stream.empty();
            }
            
        };
    }
    
    @Override
    public <T> UpdateOperation<T> update(Class<T> clazz) {
        return new UpdateOperation<T>() {
            
            @Override
            public UpdateOperation<T> from(Stream<T> data) {
                return this;
            }
            
            @Override
            public UpdateOperation<T> from(Stream<T> data, QueryOptions options) {
                return this;
            }
        };
    }
    
    @Override
    public <T> DeleteOperation<T> delete(Class<T> clazz) {
        return new DeleteOperation<T>() {
            @Override
            public <Q> DeleteOperation<T> where(Q whereClaus) {
                return this;
            }
            
            @Override
            public <Q> DeleteOperation<T> where(Q whereClaus, QueryOptions options) {
                return this;
            }
            
            @Override
            public DeleteOperation<T> from(Stream<T> data) {
                return this;
            }
            
            @Override
            public DeleteOperation<T> from(Stream<T> data, QueryOptions options) {
                return this;
            }
        };
    }
    
}

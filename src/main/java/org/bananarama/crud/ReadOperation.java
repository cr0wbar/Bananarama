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
package org.bananarama.crud;

import com.googlecode.cqengine.query.option.QueryOptions;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
public interface ReadOperation<T> extends BasicOperation{
    
    /**
     * Return all the data contained in the persisted 
     * layer in the form of a collection
     * @return this
     */
    public Stream<T> all();
    
    /**
     * Return all the data contained in the persisted
     * layer in the form of a collection, passing the given 
     * options to the underlying layer.
     * If no data has been found return an empty stream
     * @return this
     */
    public Stream<T> all(QueryOptions options);
    
    /**
     * Return all the data contained in the persisted
     * layer in the form of a collection.
     * If no data has been found return an empty stream.
     * @param <Q> the query tupe
     * @param whereClause the query object
     * @return this
     */
    public <Q> Stream<T> where(Q whereClause);
    
    /**
     * 
     * @param <Q>
     * @param whereClause
     * @param options
     * @return 
     */
    public <Q> Stream<T> where(Q whereClause,QueryOptions options);
    
    /**
     * 
     * @param keys
     * @return 
     */
    public Stream<T> fromKeys(List<?> keys);
    
    /**
     * 
     * @param keys
     * @param options
     * @return 
     */
    public Stream<T> fromKeys(List<?> keys,QueryOptions options);
}

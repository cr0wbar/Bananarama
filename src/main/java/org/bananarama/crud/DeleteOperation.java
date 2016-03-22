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
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
public interface DeleteOperation <T> {
    /**
     * Delete all objects satisfying the given condition
     * @param <Q> the query type
     * @param whereClaus the query object which defines the condition
     * @return 
     */
    public <Q> DeleteOperation<T> where(Q whereClaus);
    
    /**
     * Same as where(Q whereClause) bus also supports options
     * @param <Q>
     * @param whereClaus
     * @param options
     * @return 
     */
    public <Q> DeleteOperation<T> where(Q whereClaus,QueryOptions options);
    
    /**
     * Delete the given objects from the persisted layer. 
     * @param data The collection of objects to be removed
     * @return 
     */
    public DeleteOperation<T> from(Stream<T> data);

    /**
     * Delete the given objects from the persisted layer.
     * @param data The collection of objects to be removed
     * @return
     */
    public DeleteOperation<T> from(Stream<T> data,QueryOptions options);
}

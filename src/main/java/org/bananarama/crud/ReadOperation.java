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
import org.bananarama.io.DataCollector;

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
    Stream<T> all();
    
    /**
     * Reads all data and saves it in a {@link DataCollector}
     * @param collector
     * @return the {@link ReadOperation}
     */
    default ReadOperation<T> all(DataCollector<T> collector){
        collector.collect(all());
        return this;
    }
    
    /**
     * Return all the data contained in the persisted
     * layer in the form of a collection, passing the given 
     * options to the underlying layer.
     * If no data has been found return an empty stream
     * @return the {@link Stream} of data
     */
    Stream<T> all(QueryOptions options);
    
    /**
     * Reads all data and saves it in a {@link DataCollector}
     * @param collector
     * @param options
     * @return the {@link ReadOperation}
     */
    default ReadOperation<T> all(DataCollector<T> collector, QueryOptions options){
        collector.collect(all(options));
        return this;
    }
    
    /**
     * Return all the data contained in the persisted
     * layer in the form of a collection.
     * If no data has been found return an empty stream.
     * @param <Q> the query tupe
     * @param whereClause the query object
     * @return the {@link Stream} of data
     */
    <Q> Stream<T> where(Q whereClause);
    
    /**
     * Reads all data that match the whereclause and saves it in a {@link DataCollector}
     * @param <Q>
     * @param collector
     * @return the {@link ReadOperation}
     */
    default <Q> ReadOperation<T> where(DataCollector<T> collector, Q whereClause){
        collector.collect(where(whereClause));
        return this;
    }
    
    /**
     * 
     * @param <Q>
     * @param whereClause
     * @param options
     * @return the {@link Stream} of data
     */
    <Q> Stream<T> where(Q whereClause,QueryOptions options);
    
    /**
     * Reads all data that match the where Clause and saves it in a {@link DataCollector}
     * @param <Q>
     * @param collector
     * @param options
     * @return the {@link ReadOperation}
     */
    default <Q> ReadOperation<T> where(DataCollector<T> collector, Q whereClause, QueryOptions options){
        collector.collect(where(whereClause,options));
        return this;
    }
    
    /**
     * 
     * @param keys
     * @return the {@link Stream} of data
     */
    Stream<T> fromKeys(List<?> keys);
    
    /**
     * Reads all data that match the given keys and saves them in a {@link DataCollector}
     * @param <Q>
     * @param collector
     * @param keys
     * @return the {@link ReadOperation}
     */
    default <Q> ReadOperation<T> fromKeys(DataCollector<T> collector, List<?> keys){
        collector.collect(fromKeys(keys));
        return this;
    }
    
    /**
     * 
     * @param keys
     * @param options
     * @return the {@link Stream} of data
     */
    Stream<T> fromKeys(List<?> keys,QueryOptions options);
    
    /**
     * Reads all data that match the given keys and saves them in a {@link DataCollector}
     * @param <Q>
     * @param collector
     * @param keys
     * @param options
     * @return the {@link ReadOperation}
     */
    default <Q> ReadOperation<T> fromKeys(DataCollector<T> collector, List<?> keys,QueryOptions options){
        collector.collect(fromKeys(keys));
        return this;
    }
}

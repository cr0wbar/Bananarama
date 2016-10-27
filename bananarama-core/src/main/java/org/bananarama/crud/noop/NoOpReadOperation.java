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
package org.bananarama.crud.noop;

import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.bananarama.crud.ReadOperation;

/**
 * Read operation that actually does nothing
 * @author Guglielmo De Concini
 */
public class NoOpReadOperation<T> implements ReadOperation<T>{

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

    @Override
    public void close() throws IOException {
       /**
        * Nothing to close
        */
    }
    
}

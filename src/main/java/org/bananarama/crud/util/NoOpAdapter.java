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

import org.bananarama.crud.Adapter;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import org.bananarama.crud.noop.NoOpCreateOperation;
import org.bananarama.crud.noop.NoOpDeleteOperation;
import org.bananarama.crud.noop.NoOpReadOperation;
import org.bananarama.crud.noop.NoOpUpdateOperation;

/**
 * NoOp Adapter. Useful for short-circuiting in an adapter chain
 * @author Guglielmo De Concini
 */
public class NoOpAdapter implements Adapter<Object>{
    
    @Override
    public <T> NoOpCreateOperation<T> create(Class<T> clazz) {
      return new NoOpCreateOperation<>(clazz);
    }
    
    @Override
    public <T> NoOpReadOperation<T> read(Class<T> clazz) {
        return new NoOpReadOperation<>();
    }
    
    @Override
    public <T> NoOpUpdateOperation<T> update(Class<T> clazz) {
      return new NoOpUpdateOperation<>();
    }
    
    @Override
    public <T> NoOpDeleteOperation<T> delete(Class<T> clazz) {
        return new NoOpDeleteOperation<>();
    }
    
}

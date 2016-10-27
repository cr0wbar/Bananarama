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

/**
 *
 * @author Guglielmo De Concini
 */
public interface Adapter<S> {
    
    public <T extends S> CreateOperation<T> create(Class<T> clazz);
    
    public <T extends S> ReadOperation<T> read(Class<T> clazz);
    
    public <T extends S> UpdateOperation<T> update(Class<T> clazz);
    
    public <T extends S> DeleteOperation<T> delete(Class<T> clazz);
    
}

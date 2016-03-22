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
package org.bananarama.crud.magic;

import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import org.bananarama.crud.Adapter;
import org.bananarama.annotation.MapWith;

/**
 * 
 * @author Guglielmo De Concini
 */
@SuppressWarnings("unchecked")
public class MagicAdapter implements Adapter<Object>{
    
    private static <O> ObjToDto<O,?> getMapper(Class<O> clazz){
        if(!clazz.isAnnotationPresent(MapWith.class))
            throw new IllegalArgumentException(clazz.getName() + " is not annotated with " + MapWith.class.getName());
        
        MapWith magic = clazz.getAnnotation(MapWith.class);
        
        try {
            return magic.value().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
           throw new IllegalStateException(ex);
        }
    }
    
    @Override
    public <T> CreateOperation<T> create(Class<T> clazz)throws IllegalArgumentException{
        ObjToDto<T,?> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicCreateOperation<>(mapper);
      
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }

    @Override
    public <T> ReadOperation<T> read(Class<T> clazz) throws IllegalArgumentException {
        ObjToDto<T,?> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicReadOperation(mapper);
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }

    @Override
    public <T> UpdateOperation<T> update(Class<T> clazz) throws IllegalArgumentException {
        ObjToDto<T,?> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicUpdateOperation(mapper);
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }

    @Override
    public <T> DeleteOperation<T> delete(Class<T> clazz) throws IllegalArgumentException {
        ObjToDto<T,?> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicDeleteOperation(mapper);
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }
    
}

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

import org.bananarama.BananaRama;
import org.bananarama.annotation.BananaRamaAdapter;
import org.bananarama.crud.Adapter;
import org.bananarama.annotation.MapWith;

/**
 * 
 * @author Guglielmo De Concini
 */
@SuppressWarnings("unchecked")
@BananaRamaAdapter(requires = BananaRama.class)
public class MagicAdapter implements Adapter<Object>{
    
    private final BananaRama parent;
    
    public MagicAdapter(BananaRama parent){
        this.parent = parent;
    }
    
    private static <O,D> ObjToDto<O,D> getMapper(Class<O> clazz){
        if(!clazz.isAnnotationPresent(MapWith.class))
            throw new IllegalArgumentException(clazz.getName() + " is not annotated with " + MapWith.class.getName());
        
        MapWith magic = clazz.getAnnotation(MapWith.class);
        
        try {
            return magic.value().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Can't instantiate class: " + magic.value().getName(), e);
        }
    }
    
    @Override
    public <T> MagicCreateOperation<T,?,?> create(Class<T> clazz){
        return createInternal(clazz);
    }

    private <O,D> MagicCreateOperation<O,D,?> createInternal(Class<O> clazz){
        ObjToDto<O,D> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicCreateOperation<>(mapper,parent.create(mapper.dtoType()));
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }
    
    @Override
    public <T> MagicReadOperation<T,?,?> read(Class<T> clazz){
        return readInternal(clazz);
    }

    private <O,D> MagicReadOperation<O,D,?> readInternal(Class<O> clazz){
        ObjToDto<O,D> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicReadOperation(mapper,parent.read(mapper.dtoType()));
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public <T> MagicUpdateOperation<T,?,?> update(Class<T> clazz) throws IllegalArgumentException {
        return updateInternal(clazz);
    }

    private <O,D> MagicUpdateOperation<O,D,?> updateInternal(Class<O> clazz){
        ObjToDto<O,D> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicUpdateOperation(mapper,parent.update(mapper.dtoType()));
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public <T> MagicDeleteOperation<T,?,?> delete(Class<T> clazz) throws IllegalArgumentException {
        return deleteInternal(clazz);
    }
    
    private <O,D> MagicDeleteOperation<O,?,?> deleteInternal(Class<O> clazz){
        ObjToDto<O,D> mapper = getMapper(clazz);
        
        if(mapper != null)
            return new MagicDeleteOperation(mapper,parent.delete(mapper.dtoType()));
        
        throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + MapWith.class);
    }
    
}

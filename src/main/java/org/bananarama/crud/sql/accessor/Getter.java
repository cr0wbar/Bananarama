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
package org.bananarama.crud.sql.accessor;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.lang.reflect.Method;
/**
 *
 * @author Guglielmo De Concini
 */
public class Getter extends FieldAccessor implements Function<Object,Object>{
    
    private final Method handle;
    
    public Getter(Field field){
        super(field);
        handle = getterHandleFor(field);
    }
    
    
    private static Method getterHandleFor(Field field){
        Method getter;
        
        if( (field.getType() == Boolean.class || field.getType() == boolean.class) &&
                (getter = getHandle( field.getDeclaringClass(),"is"+getFieldCamelCaseName(field))) != null )
            return getter;
        
        return getHandle(field.getDeclaringClass(), "get" + getFieldCamelCaseName(field));
    }

    @Override
    public Object apply(Object t) {
        try{
            return handle.invoke(t);
        }
        catch(Throwable ex){
            throw new IllegalArgumentException("Can't invoke getter on field " + getName() + " on object " + t, ex);
        }
    }
    
}

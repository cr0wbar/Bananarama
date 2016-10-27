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
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.lang.reflect.Method;
import org.bananarama.crud.sql.annotation.Column;

/**
 *
 * @author Guglielmo De Concini
 */
public class Setter extends SqlFieldAccessor implements BiConsumer<Object, Object>{

    private final Method handle;
    
    public Setter(Field field){
        super(field);
        
        final Column columnAnno = field.getAnnotation(Column.class);
        if(columnAnno != null && !columnAnno.functionWrapper().isEmpty())
            name = String.format(columnAnno.functionWrapper(),super.getName());
        else
            name = super.getName();
        
        handle = setterHandleFor(field);
    }
    
    private static Method setterHandleFor(Field field){
        return getHandle(field.getDeclaringClass(), "set" + getFieldCamelCaseName(field),field.getType());
    }

    @Override
    public void accept(Object obj, Object val) {
        try{
            handle.invoke(obj,val);
        }
        catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException ex){
            throw new IllegalArgumentException("Can't invoke setter on field " + getName() + " with argument " + val, ex);
        }
    }
    
}

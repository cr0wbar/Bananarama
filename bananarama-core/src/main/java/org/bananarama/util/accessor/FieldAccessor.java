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
package org.bananarama.util.accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Guglielmo De Concini
 */
public abstract class FieldAccessor {
    private final Class<?> returnType;
    protected String name;
    
    protected FieldAccessor(Field field){
        returnType = field.getType();
        name = field.getName();
    }
    public String getName(){
        return name;
    }
    
    public Class<?> getType(){
        return returnType;
    }
    
    protected static Method getHandle(Class<?> onClass, String methodName,Class<?>... parameterTypes){
        try{
            return onClass.getDeclaredMethod(methodName,parameterTypes);
        }
        catch(NoSuchMethodException ex){
            throw new IllegalArgumentException("Can't find method " + methodName, ex);
        }
        
    }
    
    protected static String getFieldCamelCaseName(Field field){
        return field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
    }
    
    public static <T> Map<String,Field> getAttributeFieldsRecursive(
            final Class<T> clazz,
            final Predicate<Field> filter){
        
        if( clazz == null )
            return new HashMap<>();
        
        Map<String,Field> fields = getAttributeFieldsRecursive(clazz.getSuperclass(),filter);
                fields.putAll(getAttributeFieldsForClass(clazz,filter));
        
        return fields;
        
    }
    
    public static <T> Map<String,Field>  getAttributeFieldsForClass(
            final Class<T> clazz,
            final Predicate<Field> filter){
        
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(filter)
                .collect(Collectors.toMap(Field::getName, Function.identity()));
    }
}

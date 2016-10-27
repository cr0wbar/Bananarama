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
import org.bananarama.crud.sql.annotation.Column;
import org.bananarama.crud.sql.column.SqlTypeConverter;
import org.bananarama.crud.sql.annotation.ConvertWith;
import org.bananarama.crud.sql.annotation.Id;
import java.lang.reflect.Field;
import org.bananarama.util.accessor.FieldAccessor;

/**
 *
 * @author Guglielmo De Concini
 */
public abstract class SqlFieldAccessor extends FieldAccessor{
    private final boolean isKey;
    private final Class<? extends SqlTypeConverter<?>> sqlTypeConverter;
    
    protected SqlFieldAccessor(Field field){
        super(field);

        Column col;     
        if((col = field.getAnnotation(Column.class)) != null
                &&!col.name().isEmpty()){
            name = col.name();
        }
        
        isKey = field.isAnnotationPresent(Id.class);    
        
        ConvertWith converterAnno = field.getAnnotation(ConvertWith.class);
        sqlTypeConverter = converterAnno != null ? converterAnno.value() : null;
    }
    
    public boolean isKey() {
        return isKey;
    }
    
    public Class<? extends SqlTypeConverter<?>> getSqlTypeConverter(){
        return sqlTypeConverter;
    }
    
}

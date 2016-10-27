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
package org.bananarama.crud.sql;

import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.sql.accessor.SqlFieldAccessor;
import org.bananarama.util.StringUtils;
import java.util.Collection;
import java.util.stream.Stream;
import javax.sql.DataSource;
/**
 *
 * @author Guglielmo De Concini
 */
public class SqlCreateOperation<T> extends AbstractSqlOperation<T>implements CreateOperation<T> {
    
    public SqlCreateOperation(DataSource dataSource, Class<T> clazz){
        super(dataSource, clazz);
    }
    
    public SqlCreateOperation<T> onTable(String tableName) {       
        this.tableName = tableName;
        return this;
    }
    
    @Override
    public SqlCreateOperation<T> from(Stream<T> data) {
        return from(data,null);
    }
    
    @Override
    public SqlCreateOperation<T> from(Stream<T> data, QueryOptions options) {
        String currentTableName = getTableNameForCurrentSession(options);
        String sql  = getInsertQuery(getGetters(),currentTableName);
        
        log.info("Created " + writeOnDb(data,sql, getGetters()) + " record(s) on table " 
                + currentTableName + " (" + clazz.getName() + ")");
        
        return this;
    }

    private static String getInsertQuery(Collection<? extends SqlFieldAccessor> accessors,String table){
        return String.format("INSERT INTO %s (%s) VALUES (%s)", 
                table,
                StringUtils.mkString(accessors.stream().map(SqlFieldAccessor::getName),"", ",", ""),
                StringUtils.getNMarkerSeparatedChars('?', ',', accessors.size()));
    }
    
}

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
import org.bananarama.crud.UpdateOperation;
import org.bananarama.crud.sql.accessor.FieldAccessor;
import org.bananarama.util.StringUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;

/**
 *
 * @author Guglielmo De Concini
 */
public class SqlUpdateOperation<T> extends AbstractSqlOperation<T> implements UpdateOperation<T>{
    
    public SqlUpdateOperation(DataSource dataSource, Class<T> clazz) {
        super(dataSource, clazz);
    }

    public SqlUpdateOperation<T> fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    @Override
    public SqlUpdateOperation<T> from(Stream<T> data) {
        return from(data,null);
    }

    @Override
    public SqlUpdateOperation<T> from(Stream<T> data, QueryOptions options) {
        SqlOperationOptions sqlOpts;
        String currentTableName = getTableNameForCurrentSession(options);
        String sql  = getUpdateQuery(getGetters(),currentTableName,clazz);
        
        log.info("Updated " + writeOnDb(data, sql, getGetters()) + " record(s) on table " 
                + currentTableName + " (" + clazz.getName() + ")");

        return this;
    }

    private static <A extends FieldAccessor> String getUpdateQuery(
            Collection<A> accessors,
            String table,
            Class<?> clazz){
        final Map<Boolean,List<A>> accessorsByIsKey = accessors.stream()
                .collect(Collectors.groupingBy(FieldAccessor::isKey));
        
        if(accessorsByIsKey.get(true).isEmpty())
            throw new IllegalArgumentException(clazz.getName() + " does not have identity fields, UPDATE is not possible");
        
        return String.format(
                "UPDATE %s SET %s WHERE %s ",
                table,
                StringUtils.mkString(accessorsByIsKey.get(false).stream().map(FieldAccessor::getName),""," = ?,"," = ?"),
                StringUtils.mkString(accessorsByIsKey.get(true).stream().map(FieldAccessor::getName),""," = ? AND "," = ?"));
    }

}

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
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.sql.accessor.FieldAccessor;
import org.bananarama.crud.sql.accessor.Getter;
import org.bananarama.util.StringUtils;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import java.util.stream.Stream;
import javax.sql.DataSource;

/**
 *
 * @author Guglielmo De Concini
 */
public class SqlDeleteOperation<T> extends AbstractSqlOperation<T> implements DeleteOperation<T> {
    
    public SqlDeleteOperation(DataSource dataSource, Class<T> clazz) {
        super(dataSource, clazz);
    }

    @Override
    public <Q> SqlDeleteOperation<T> where(Q whereClause) {

        throw new UnsupportedOperationException("SQL DELETE is not supported using a WHERE clause yet");
    }

    @Override
    public SqlDeleteOperation<T> from(Stream<T> data) {
        return from(data, null);
    }

    /**
     * Specify the table where to delete
     * @param tableName
     * @return
     */
    public SqlDeleteOperation<T> onTable(String tableName) {

        this.tableName = tableName;
        return this;
    }

    @Override
    public <Q> DeleteOperation<T> where(Q whereClause, QueryOptions options) {
        return where(whereClause);
    }

    @Override
    public SqlDeleteOperation<T> from(Stream<T> data, QueryOptions options) {
        List<Getter> getters = getGetters().stream()
                .filter(FieldAccessor::isKey)
                .collect(Collectors.toList());
        if(getters.isEmpty())
            throw new IllegalArgumentException(clazz.getName() + " does not have identity fields, DELETE is not possible");
        String currentTableName = getTableNameForCurrentSession(options);     
        String sql  = getDeleteQueryBasedOnIdentity(getters,currentTableName);

        log.info("Deleted " + writeOnDb(data, sql, getters) + " record(s) on table " 
                + currentTableName + " (" + clazz.getName() + ")");

        return this;
    }

    private static String getDeleteQueryBasedOnIdentity(Collection<? extends FieldAccessor> accessors,String table){
        return String.format(
                "DELETE FROM %s WHERE %s ",
                table,
                StringUtils.mkString(accessors
                        .stream()
                        .filter(FieldAccessor::isKey)
                        .map(FieldAccessor::getName),""," = ? AND "," = ?")
        );
    }

}

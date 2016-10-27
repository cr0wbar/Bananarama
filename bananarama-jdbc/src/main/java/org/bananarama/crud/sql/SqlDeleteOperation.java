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
import org.bananarama.crud.sql.accessor.SqlFieldAccessor;
import org.bananarama.crud.sql.accessor.Getter;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import org.bananarama.exception.FailedOperationException;
import org.bananarama.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

        return where(whereClause, null);
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
    public <Q> SqlDeleteOperation<T> where(Q whereClause, QueryOptions options) {
        String currentTableName = getTableNameForCurrentSession(options);
        
        String whereClauseString = "";
        if (whereClause != null) {
            if(whereClause instanceof String)
                whereClauseString = (String) whereClause;
            else if(whereClause instanceof com.googlecode.cqengine.query.Query)
                whereClauseString = CQE2SQL.convertCqQuery((com.googlecode.cqengine.query.Query<?>) whereClause);            
        }
        
        String sql = "DELETE from " + currentTableName + whereClauseString;
        
        try(Connection connection = dataSource.getConnection()){
            try(Statement statement = connection.createStatement()){
                statement.executeUpdate(sql);
                
                log.info("Deleted record(s) on table " + currentTableName + " (" + clazz.getName() + ")");

            }
        }
        catch (Exception ex) {
            final Exception sub;

            if (ex instanceof SQLException)
                sub = findCause((SQLException) ex);
            else
                sub = ex;

            throw new FailedOperationException("Deleting data on DB failed (" + clazz.getName() + ")", sub);
        }
        
        
        return this;
    }

    @Override
    public SqlDeleteOperation<T> from(Stream<T> data, QueryOptions options) {
        List<Getter> getters = getGetters().stream()
                .filter(SqlFieldAccessor::isKey)
                .collect(Collectors.toList());
        if(getters.isEmpty())
            throw new IllegalArgumentException(clazz.getName() + " does not have identity fields, DELETE is not possible");
        String currentTableName = getTableNameForCurrentSession(options);     
        String sql  = getDeleteQueryBasedOnIdentity(getters,currentTableName);

        log.info("Deleted " + writeOnDb(data, sql, getters) + " record(s) on table " 
                + currentTableName + " (" + clazz.getName() + ")");

        return this;
    }

    private static String getDeleteQueryBasedOnIdentity(Collection<? extends SqlFieldAccessor> accessors,String table){
        return String.format("DELETE FROM %s WHERE %s ",
                table,
                StringUtils.mkString(accessors
                        .stream()
                        .filter(SqlFieldAccessor::isKey)
                        .map(SqlFieldAccessor::getName),""," = ? AND "," = ?")
        );
    }

    
    /**
    * {@inheritDoc}
    */
    @Override
    public DeleteOperation<T> all() {

        return where(null);
        
    }

}

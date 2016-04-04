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


import javax.sql.DataSource;

import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.sql.accessor.FieldAccessor;
import org.bananarama.crud.sql.accessor.Setter;
import org.bananarama.crud.sql.column.SqlTypeConverter;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import org.bananarama.util.StringUtils;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 *
 * @author Guglielmo De Concini
 */
public class SqlReadOperation<T>extends AbstractSqlOperation<T> implements ReadOperation<T>{
    
    
    public SqlReadOperation(DataSource dataSource, Class<T> clazz) {
        super(dataSource, clazz);
    }
    
    public SqlReadOperation<T> fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    @Override
    public Stream<T> all() {
        return getDataFromDb(null,null,ps -> {});
    }
    
    @Override
    public Stream<T> all(QueryOptions options) {
        return getDataFromDb(null,options,ps ->{});
    }
    
    @Override
    public <Q> Stream<T> where(Q whereClause) {
        return where(whereClause, null);
    }
    
    
    private Stream<T> getDataFromDb(
            String whereClause, 
            QueryOptions options,
            PreparedStatementPreprocessor preprocessor){
        
        List<Setter> setters = getSetters();
        SqlOperationOptions sqlOpts;
        String currentTableName = getTableNameForCurrentSession(options);
        String sql  = getSelectQuery(setters,currentTableName);
        
        if(whereClause != null)
            sql += whereClause;
        
        return readFromDb(sql, setters,preprocessor);
        
    }
    
    @Override
    public <Q> Stream<T> where(Q whereClause, QueryOptions options) {
        if(whereClause instanceof String)
            return getDataFromDb((String)whereClause,options,ps -> {});
        else if(whereClause instanceof com.googlecode.cqengine.query.Query)
            return getDataFromDb(CQE2SQL.convertCqQuery((com.googlecode.cqengine.query.Query<?>)whereClause,options),options,ps ->{});
        
        throw new UnsupportedOperationException(whereClause.getClass().getCanonicalName() + " not supported for SQL where clause");
    }
    
    private static String getSelectQuery(Collection<? extends FieldAccessor> accessors,String table){
        return String.format(
                " SELECT %s FROM %s ",
                StringUtils.mkString(accessors.stream().map(FieldAccessor::getName),"", ",", ""),
                table);
    }

    @Override
    public Stream<T> fromKeys(List<?> keys) {
        return fromKeys(keys,null);
    }

    @Override
    public Stream<T> fromKeys(List<?> keys,QueryOptions options) {
        List<Setter> setters = getSetters()
                .stream()
                .filter(FieldAccessor::isKey)
                .collect(Collectors.toList());
        
        if(setters.isEmpty())
            throw new IllegalArgumentException(clazz.getName() + " does not have identity fields, SELECT based on keys is not possible");
        
        PreparedStatementPreprocessor builder = ps -> {
            List<SqlTypeConverter> readers = 
                    prepareConverters(setters);
            
            for(int i =0; i < keys.size(); i++)
                readers.get(i%readers.size()).write(ps,i+1 ,keys.get(i));
            
        };
        
        final String singleMarker,whereClause;
        
        if(setters.size() == 1)
            singleMarker = "?";
        else//size > 1 is the only possible case
            singleMarker = '('+StringUtils.getNMarkerSeparatedChars('?', ',', setters.size())+')';
                
        whereClause = String.format(" WHERE %s in (%s) ",
                StringUtils.mkString(setters.stream().map(FieldAccessor::getName),"(",",",")"),
                StringUtils.getNMarkerSeparatedChars(singleMarker, ",", keys.size()/setters.size())
        );
        
        return getDataFromDb(whereClause,options,builder);
    }
    
}

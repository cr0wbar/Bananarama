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

import org.bananarama.crud.sql.column.TransparentTypeConverter;
import org.bananarama.crud.sql.column.SqlTypeConverter;
import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import org.bananarama.crud.sql.accessor.SqlFieldAccessor;
import org.bananarama.crud.sql.accessor.Getter;
import org.bananarama.crud.sql.accessor.Setter;
import org.bananarama.crud.sql.annotation.Column;
import org.bananarama.crud.sql.annotation.Convert;
import org.bananarama.crud.sql.annotation.Table;
import org.bananarama.crud.sql.annotation.Transient;
import org.bananarama.exception.FailedOperationException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.bananarama.exception.BananaRamaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Guglielmo De Concini
 */
public abstract class AbstractSqlOperation <T> implements AutoCloseable{
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Class<T> clazz;
    protected final DataSource dataSource;
    protected String tableName;
    
    private List<Getter> getters;
    private List<Setter> setters;
    
    private final Map<Class<?>,Class<? extends SqlTypeConverter<?>>> converters;
    private static final SqlTypeConverter<Object> NOOP_TYPE_CONVERTER = new TransparentTypeConverter();

    protected AbstractSqlOperation(DataSource dataSource, Class<T> clazz){
        this.clazz = clazz;
        this.dataSource = dataSource;
        
        //lookup = MethodHandles.lookup();
        
        //Set tableName
        tableName = extractTableName(clazz);     

        //Scan for global converters
        converters = Arrays.stream(clazz.getAnnotationsByType(Convert.class))
                .collect(Collectors.toMap(Convert::type,Convert::with));

    }
    
    public String getTableName(){
        return tableName;
    }
    
    protected String getTableNameForCurrentSession(QueryOptions options){
        SqlOperationOptions sqlOpts;
        
        if(options != null && (sqlOpts = options.get(SqlOperationOptions.class)) != null)
            if(sqlOpts.getTableName() != null)
                return sqlOpts.getTableName();
        
        return tableName;
    }
    
    protected List<Getter> getGetters(){
        
        if(getters == null)
            getters = getAccessors(field -> new Getter(field));
        return getters;
        
    }
    
    protected List<Setter>getSetters(){
        
        if(setters == null)
            setters = getAccessors(field -> new Setter(field));
        return setters;
    }
    
    @SuppressWarnings("rawtypes")
    protected List<SqlTypeConverter> prepareConverters(List<? extends SqlFieldAccessor> accessors){
        List<SqlTypeConverter> readers = new ArrayList<>(accessors.size());
        
        for(SqlFieldAccessor acc: accessors){
            Class<? extends SqlTypeConverter<?>> convClass;
            if((convClass = acc.getSqlTypeConverter()) != null//Field converter
                    || (convClass = converters.get(acc.getType())) != null)//overrides global converter
                try {
                    readers.add(convClass.newInstance());
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new IllegalArgumentException("Cannot instantiate "  + convClass.getName(), ex);
                }
            else
                readers.add(NOOP_TYPE_CONVERTER);
        }
        
        return readers;
    }
    
    @SuppressWarnings("rawtypes")
    protected Stream<T> readFromDb(String sql,List<Setter> accessors,PreparedStatementPreprocessor preprocessor){
        final List<SqlTypeConverter> typeConverters = prepareConverters(accessors);
        
        try(Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            preprocessor.process(ps);
            try(ResultSet rs = ps.executeQuery()){
                List<T> records = new ArrayList<>();
                while(rs.next()){
                    
                    T record = clazz.newInstance();
                    for(int i=0;i<accessors.size();i++)
                        accessors.get(i).accept(record,typeConverters.get(i).read(rs, i+1));
                    
                    records.add(record);
                }
                return records.stream();
            }
        }
        catch(Exception ex){
            final Exception sub;
            
            if(ex instanceof SQLException)
                sub = findCause((SQLException)ex);
            else 
                sub = ex;
            
            throw new FailedOperationException("Reading data from database failed (" +clazz.getName()+")" + sub.getMessage(),sub);
        }
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    protected int writeOnDb(Stream<T> data,String sql,List<Getter> accessors){
        final List<SqlTypeConverter> typeConverters = prepareConverters(accessors);
        int affected;

        try(Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
            conn.setAutoCommit(false);
            Iterator<T> iter = data.iterator();
            
            while(iter.hasNext()){
                T record = iter.next();
                
                for(int i=0;i<accessors.size();i++)
                    typeConverters.get(i).write(ps, i+1, accessors.get(i).apply(record));

                ps.addBatch();
            }
            
            affected = IntStream.of(ps.executeBatch()).sum();
            conn.commit();
        }
        catch(Exception ex){
            final Exception sub;
            
            if(ex instanceof SQLException)
                sub = findCause((SQLException)ex);
            else 
                sub = ex;
            
            throw new FailedOperationException("Writing data on DB failed (" +clazz.getName()+")", sub);
        }
        
        return affected;
    }
    
    protected static SQLException findCause(SQLException ex){
        if(ex.getNextException() != null)
            return findCause(ex.getNextException());
        return ex;
    }
    
    private static final int ILLEGAL_FIELD_MODIFIERS = Modifier.STATIC | Modifier.FINAL;
        
    protected <T extends SqlFieldAccessor> List<T> getAccessors(Function<Field,T> generator){
        final Predicate<Field> fieldFilter = field ->(field.getModifiers() & ILLEGAL_FIELD_MODIFIERS) == 0
                        && !field.isAnnotationPresent(Transient.class);

        final Collection<Field> fields;
        final Table anno = clazz.getAnnotation(Table.class);
        
        if(anno != null && anno.inheritFields())
            fields = SqlFieldAccessor.getAttributeFieldsRecursive(clazz, fieldFilter).values();
        else 
            fields = SqlFieldAccessor.getAttributeFieldsForClass(clazz, fieldFilter).values();
        
        final List<T> accessors = fields
                .stream()
                .map(generator)
                .collect(Collectors.toList());
        
        accessors.sort((a,b) -> (a.isKey() ? 1 : 0 ) -  (b.isKey() ? 1 : 0));
        return accessors;
    }
    
    private static String extractTableName(final Class<?> cl){
        final Table table;
        if((table = cl.getAnnotation(Table.class)) != null
                && !table.name().isEmpty())
            return  table.name();
        
        return cl.getSimpleName();
    }
    
    public static String extractColumnName(final Field field){
        final Column column;
        if((column = field.getAnnotation(Column.class)) != null
                &&!column.name().isEmpty())
                   return column.name();
        
        return field.getName();
             
    }

    @Override
    public void close() throws IOException {
    }
    
    /**
     * Utility interface to preprocess a preparedStatement.
     */
    @FunctionalInterface  
    protected interface PreparedStatementPreprocessor {
        void process(PreparedStatement t) throws BananaRamaException;
    }
}

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
package org.bananarama.crud.util.cqlogic;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.logical.And;
import com.googlecode.cqengine.query.logical.LogicalQuery;
import com.googlecode.cqengine.query.logical.Not;
import com.googlecode.cqengine.query.logical.Or;
import com.googlecode.cqengine.query.option.OrderByOption;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.query.simple.Between;
import com.googlecode.cqengine.query.simple.Equal;
import com.googlecode.cqengine.query.simple.GreaterThan;
import com.googlecode.cqengine.query.simple.LessThan;
import com.googlecode.cqengine.query.simple.SimpleQuery;
import org.bananarama.crud.sql.SqlOperationOptions;
import org.bananarama.crud.util.cqlogic.mappers.Mapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.logical.AndMapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.logical.NotMapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.logical.OrMapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.simple.BetweenMapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.simple.GreaterThanMapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.simple.LessThanMapper;
import org.bananarama.crud.util.cqlogic.mappers.impl.simple.EqualMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Guglielmo De Concini
 */
@SuppressWarnings("unchecked")
public class CQE2SQL {
    
    private static final Map<Class<? extends Query>,Mapper> mappers = getMappers();

    public static String convertCqQuery(Query<?> query){
        String sql = "";
        
        if(query != null){
            if( query instanceof SimpleQuery )
                sql =  " WHERE " + getMapper((Class<SimpleQuery<?,?>>)query.getClass()).map((SimpleQuery<?,?>) query);
            else if (query instanceof LogicalQuery)
                sql =  " WHERE " + getMapper((Class<LogicalQuery<?>>)query.getClass()).map((LogicalQuery<?>) query);
            else
                throw new UnsupportedOperationException("Unknown query class " + query.getClass().getName());
        }
          
        return sql;
    }
    
    public static String convertCqQuery(Query<?> query,QueryOptions opts){
        return convertCqQuery(query) + convertCqOptions(opts);
    }
    
    public static String convertCqOptions(QueryOptions opts){
        String sql = "";
        
        if(opts != null){
            OrderByOption<?> orders;
            SqlOperationOptions sqlOpts;
            if((orders = opts.get(OrderByOption.class))!=null){
                sql += " ORDER BY " + orders.getAttributeOrders()
                        .stream()
                        .map(a -> a.getAttribute().getAttributeName() + (a.isDescending() ? " DESC" : ""))
                        .reduce( (o,p) -> o +", "+ p).get();
            }
            if((sqlOpts = opts.get(SqlOperationOptions.class)) != null){
                if(sqlOpts.getLimit() != null)
                    sql += " LIMIT " + sqlOpts.getLimit();
            }
            
        }
        return sql;
    }
    private static Map<Class<? extends Query>, Mapper> getMappers(){
        HashMap<Class<? extends Query>,Mapper> map = new HashMap<>();
        
        //Simple
        map.put(Equal.class,new EqualMapper());
        map.put(GreaterThan.class,new GreaterThanMapper());
        map.put(LessThan.class,new LessThanMapper());
        map.put(Between.class,new BetweenMapper());
        
        //Logical
        map.put(And.class,new AndMapper());
        map.put(Or.class,new OrMapper());
        map.put(Not.class,new NotMapper());
        
        return Collections.unmodifiableMap(map);
    }
    
    public static <T extends Query> Mapper<T> getMapper(Class<T> clazz){
        Mapper <T> mapper;
        
        if( (mapper = mappers.get(clazz)) != null )
            return mapper;
        
        throw new UnsupportedOperationException("Unknown query class " + clazz.getName());
    }
    
}

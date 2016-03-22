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
package org.bananarama.crud.util.cqlogic.mappers.impl.logical;

import com.googlecode.cqengine.query.logical.And;
import com.googlecode.cqengine.query.logical.LogicalQuery;
import com.googlecode.cqengine.query.simple.SimpleQuery;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import org.bananarama.crud.util.cqlogic.mappers.Mapper;

/**
 *
 * @author Guglielmo De Concini
 */
public class AndMapper implements Mapper<And>{
    private static final String AND = " AND ";
    private static final String TC = " ) ";
    private static final String SC = " ( ";
    
    @Override @SuppressWarnings("unchecked")
    public String map(And q) {
        StringBuilder sb = new StringBuilder();
        
        //Open parenthesis
        sb.append(SC);
        
        //Prepare first block from logical child queries, if any
        if(q.hasLogicalQueries())
            sb.append(q.getLogicalQueries()
                    .stream()
                    .map(lq -> CQE2SQL.getMapper((Class<LogicalQuery>) lq.getClass()).map((LogicalQuery) lq))
                    .reduce( (a,b) -> a+AND+b).get());
        
        if(q.hasLogicalQueries() && q.hasSimpleQueries())
            sb.append(AND);//Connect the logical block and simple block of child queries
        
        //Prepare second block from simple child queries, if any
        if(q.hasSimpleQueries())
           sb.append(q.getSimpleQueries()
                    .stream()
                    .map(sq -> CQE2SQL.getMapper((Class<SimpleQuery<?,?>>)sq.getClass()).map((SimpleQuery<?, ?>) sq))
                    .reduce( (a,b) -> a+AND+b).get());
            
        //Close parenthesis
        sb.append(TC);
        
        return sb.toString();
    }
    
}

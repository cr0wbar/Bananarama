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
package org.bananarama.crud.jpa;


/**
 * @author simone.decristofaro
 * Apr 7, 2016
 */
public class JpaOperationOption {

    private final QueryType queryType;
    
    /**
     * @param queryType
     */
    public JpaOperationOption(QueryType queryType) {
        super();
        this.queryType = queryType;
    }

    public static JpaOperationOption queryType(QueryType queryType){
        return new JpaOperationOption(queryType);
    }
    
    
    /**
     * @return the queryType
     */
    public QueryType getQueryType() {
    
        return queryType;
    }
    
    
    public static enum QueryType{
        /** A simple JPA query */
        SIMPLE,
        /** A native query that allow to execute a query without constraints. 
         * The value of the clause should be a complete query (not only the WHERE clause). */
        NATIVE;
    }


}

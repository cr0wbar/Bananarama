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

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.logical.Not;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import org.bananarama.crud.util.cqlogic.mappers.Mapper;

/**
 *
 * @author Guglielmo De Concini
 */
public class NotMapper implements Mapper<Not>{
        private static final String NOT = " NOT ";
        private static final String TC = " ) ";
        private static final String SC = " ( ";

    @Override @SuppressWarnings("unchecked")
    public String map(Not q) {
        Query negated = q.getNegatedQuery();

        return NOT + SC + CQE2SQL.getMapper((Class<Query<?>>)negated.getClass()).map(negated) + TC;
    }
    
}

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
package org.bananarama.crud.util.cqlogic.mappers.impl.simple;

import com.googlecode.cqengine.query.simple.LessThan;
import org.bananarama.crud.util.cqlogic.mappers.Mapper;
import static org.bananarama.crud.util.cqlogic.mappers.SqlTypeStringTranslator.*;
/**
 *
 * @author Guglielmo De Concini
 */
public class LessThanMapper implements Mapper<LessThan>{

    @Override
    public String map(LessThan q) {
        StringBuilder b = new StringBuilder();
        
        b.append(q.getAttributeName());
        b.append(" ");
        
        b.append("<");
        if(q.isValueInclusive())
            b.append("=");
        b.append(" ");
        b.append(adapt(q.getValue()));
        
        return b.toString();
    }
    
}

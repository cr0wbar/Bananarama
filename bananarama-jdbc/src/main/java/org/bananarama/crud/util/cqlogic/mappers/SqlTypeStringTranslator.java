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
package org.bananarama.crud.util.cqlogic.mappers;

import java.time.LocalDateTime;

/**
 *
 * @author Guglielmo De Concini
 */
public class SqlTypeStringTranslator {
    /**
     * Properly fills a query field based on its type,
     * e.g. a String, a timestamp, etc.
     * @param o
     * @return
     */
    public static String adapt(Object o){
        
        if(o instanceof String)
            return adapt((String)o);
        if(o instanceof LocalDateTime)
            return adapt(o.toString());
        
        return o.toString();
    }
    
    public static String adapt(String o){
        return String.format("'%s'",o);
    }
    

}

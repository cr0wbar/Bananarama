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
package org.bananarama.cache.providers.index;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.Index;
import com.googlecode.cqengine.index.disk.DiskIndex;

/**
 *

 * @author Guglielmo De Concini
 */
public class DiskIndexProvider<O> implements IndexProvider<O>{

    @SuppressWarnings("unchecked")
    @Override
    public Index<O> getIndex(Attribute<O, ?> attr) {
        
        if(attr instanceof SimpleAttribute)
            return (Index<O>) DiskIndex.onAttribute((SimpleAttribute)attr);
        throw new IllegalArgumentException(attr.toString() + " must be an instance of SimpleAttribute in order to be persisted on disk");
    }
    
}

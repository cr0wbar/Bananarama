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
package cache.cqengine;

import basic.Entry;
import basic.ListAdapter;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.annotation.Banana;
import org.bananarama.cache.annotation.BufferedOnIndexedCollection;
import org.bananarama.cache.annotation.Indexed;
import org.bananarama.cache.providers.index.NavigableIndexProvider;

/**
 * @author Guglielmo De Concini
 */
@BufferedOnIndexedCollection
@Banana(adapter = ListAdapter.class)
public class CacheEntry extends Entry{
    
    public CacheEntry(String key, String val) {
        super(key, val);
    }
    
    @Indexed
    public static final Attribute<CacheEntry,String> KEY = new SimpleAttribute<CacheEntry, String>("key"){
        @Override
        public String getValue(CacheEntry o, QueryOptions qo) {
            return o.getKey();
        }
    };
    
    @Indexed(NavigableIndexProvider.class)
    public static final Attribute<CacheEntry,String> VAL = new SimpleAttribute<CacheEntry, String>("val"){
        @Override
        public String getValue(CacheEntry o, QueryOptions qo) {
            return o.getVal();
        }
    };
}

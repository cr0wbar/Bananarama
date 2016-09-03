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

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.annotation.Banana;
import org.bananarama.cache.IndexedCollectionAdapter;
import org.bananarama.cache.annotation.BufferedOnIndexedCollection;
import org.bananarama.cache.annotation.Indexed;
import org.bananarama.cache.providers.index.NavigableIndexProvider;
import org.bananarama.crud.util.NoOpAdapter;

/**
 *
 * @author Guglielmo De Concini
 */
@BufferedOnIndexedCollection(backingAdapter = NoOpAdapter.class,inheritFields = true)
@Banana(adapter = IndexedCollectionAdapter.class)
public class CacheInheritedEntry extends CacheParentEntry{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Indexed
     public static final Attribute<CacheInheritedEntry,String> NAME = new SimpleAttribute<CacheInheritedEntry, String>("name") {
         @Override
         public String getValue(CacheInheritedEntry object, QueryOptions queryOptions) {
             return object.name;
         }
     };
    
    @Indexed(NavigableIndexProvider.class)
    public static final Attribute<CacheInheritedEntry,Integer> IDNO_OVERRIDE = new SimpleAttribute<CacheInheritedEntry, Integer>("idno_overridden") {
        @Override
        public Integer getValue(CacheInheritedEntry object, QueryOptions queryOptions) {
            return object.getIdno();
        }
    };
}

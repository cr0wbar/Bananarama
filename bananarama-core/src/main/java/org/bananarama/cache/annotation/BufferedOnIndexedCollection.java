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
package org.bananarama.cache.annotation;

import com.googlecode.cqengine.IndexedCollection;
import org.bananarama.cache.providers.collection.ConcurrentIndexedCollectionProvider;
import org.bananarama.cache.providers.collection.IndexedCollectionProvider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bananarama.crud.Adapter;

/**
 * If present, indicates that the annotated
 * class will be buffered on a {@link IndexedCollection}
 * @author Guglielmo De Concini
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BufferedOnIndexedCollection {
    
    /**
     * Sets the class that will provide the implementation of the
     * {@link IndexedCollection} that will be associated with
     * the annotated entity
     */
    @SuppressWarnings("rawtypes")
    Class<? extends IndexedCollectionProvider> provider() default ConcurrentIndexedCollectionProvider.class;
    
    /**
     * If true it will scan all super classes for attributes, otherwise 
     * it will focus only on the given class instead.
     * Fields in child classes override the ones in parent classes.
     */
    boolean inheritFields() default false; 
    
    Class<? extends Adapter<?>> backingAdapter();
}

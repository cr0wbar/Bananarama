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

import com.googlecode.cqengine.index.Index;
import org.bananarama.cache.providers.index.HashIndexProvider;
import org.bananarama.cache.providers.index.IndexProvider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to automatically identify fields
 * on which the user wants to create an index.
 * @author Guglielmo De Concini
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Indexed {
    /**
     * The type whose instance will be used
     * in order to load the {@link Index} for the annotated field.
     * @return 
     */
    @SuppressWarnings("rawtypes")
    Class<? extends IndexProvider> value() default HashIndexProvider.class;
}

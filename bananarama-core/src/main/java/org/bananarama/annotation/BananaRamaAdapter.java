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
package org.bananarama.annotation;

import org.bananarama.crud.Adapter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bananarama.BananaRama;

/**
 * Utility class to configure additional 
 * parameters
 * @author Guglielmo De Concini
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BananaRamaAdapter {
    /**
     * Indicates adapters on which the annotated adapter depends.
     * {@link BananaRama} will automatically load the instances of 
     * those adapters.
     * @return the sequence of adapter types whose instances will
     * be passed to the annotated adapter.
     */
    Class<? extends Adapter<?>>[] requires();
}

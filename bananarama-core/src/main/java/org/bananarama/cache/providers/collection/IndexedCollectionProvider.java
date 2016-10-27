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
package org.bananarama.cache.providers.collection;

import com.googlecode.cqengine.IndexedCollection;
/**
 * Helper Class used in {@link BufferedOnIndexedCollection} to set 
 * custom properties for the buffer.
 * @author Guglielmo De Concini
 */
public interface IndexedCollectionProvider<O> {
    /**
     * @return the amount of time for the buffer to idle, in seconds. 0 indicates unlimited.
     * Additionally a negative value which will result in the
     * underlying buffer to be flagged as 'eternal'
     */
    default int timeToIdle() {
        return 0;
    }

    /**
     * @return the amount of time for the buffer to live, in seconds. 0 indicates unlimited.
     * Additionally a negative value which will result in the
     * underlying buffer to be flagged as 'eternal'
     */
    default int timeToLive() {
        return 0;
    }
    
    /**
     * Provides the buffer adapter with the custom implementation of 
     * the {@link IndexedCollection}
     * @return A instance of {@link IndexedCollection}
     */
    IndexedCollection<O> buildCollection();
}

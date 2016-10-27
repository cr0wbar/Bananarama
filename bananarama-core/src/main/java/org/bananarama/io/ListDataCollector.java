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
package org.bananarama.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ListDataCollector<T> implements DataCollector<T>{

    private final List<T> data;
    
    public ListDataCollector(){
        data = new ArrayList<>();
    }
    
    public ListDataCollector(final List<T> data){
        this.data = data;
    }
    
    @Override
    public void collect(Stream<T> dataIn) {
        dataIn.forEach(data::add);
    }

    @Override
    public Stream<T> dump() {
        return data.stream();
    }
    
}

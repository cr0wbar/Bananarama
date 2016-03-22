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
package org.bananarama.crud.magic;

import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.BananaRama;
import org.bananarama.crud.CreateOperation;
import java.util.stream.Stream;

/**
 * 
 * @author Guglielmo De Concini
 */
public class MagicCreateOperation<O,D,M extends ObjToDto<O,D>> implements CreateOperation<O>{
    private final M mapper;
    
    public MagicCreateOperation(M mapper){
        this.mapper = mapper;
    }
    
    @Override
    public CreateOperation<O> from(Stream<O> data) {
        Stream<D> dtos = data.map(mapper::toDto);
        BananaRama.create(mapper.dtoType()).from(dtos);
        return this;
    }

    @Override
    public CreateOperation<O> from(Stream<O> data, QueryOptions options) {
        Stream<D> dtos = data.map(mapper::toDto);
        BananaRama.create(mapper.dtoType()).from(dtos,options);
        return this;
    }
    
}

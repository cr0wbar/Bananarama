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
import java.io.IOException;
import org.bananarama.crud.UpdateOperation;
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
public class MagicUpdateOperation<O,D,M extends ObjToDto<O,D>> implements UpdateOperation<O> {

    private final M mapper;
    private final UpdateOperation<D> updateDto;
    
    public MagicUpdateOperation(M mapper,UpdateOperation<D> updateDto) {
        this.mapper = mapper;
        this.updateDto = updateDto;
    }

    @Override
    public UpdateOperation<O> from(Stream<O> data) {
        Stream<D> dtos = data.map(mapper::toDto);
        updateDto.from(dtos);
        return this;
    }

    @Override
    public UpdateOperation<O> from(Stream<O> data, QueryOptions options) {
        Stream<D> dtos = data.map(mapper::toDto);
        updateDto.from(dtos,options);
        return this;
    }

    @Override
    public void close() throws IOException {
     updateDto.close();
    }
    
}

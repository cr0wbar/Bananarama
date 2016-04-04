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
import org.bananarama.crud.ReadOperation;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
public class MagicReadOperation<O,D,M extends ObjToDto<O,D>> implements ReadOperation<O> {
    private final M mapper;
    private final ReadOperation<D> readDto;
    
    public MagicReadOperation(M mapper,ReadOperation<D> readDto){
        this.mapper = mapper;
        this.readDto = readDto;
    }
    
    @Override
    public Stream<O> all() {
        return readDto.all()
                .map(mapper::toObj);
    }

    @Override
    public Stream<O> all(QueryOptions options) {
      return readDto.all(options).
              map(mapper::toObj);
    }
    
    @Override
    public <Q> Stream<O> where(Q whereClause) {
        return readDto.where(whereClause)
                .map(mapper::toObj);
    }

    @Override
    public <Q> Stream<O> where(Q whereClause, QueryOptions options) {
        return readDto.where(whereClause, options)
                .map(mapper::toObj);
    }

    @Override
    public Stream<O> fromKeys(List<?> keys) {
        return readDto.fromKeys(keys)
                .map(mapper::toObj);
    }

    @Override
    public Stream<O> fromKeys(List<?> keys,QueryOptions options) {
        return readDto.fromKeys(keys, options)
                .map(mapper::toObj);
    }

    @Override
    public void close() throws Exception {
        readDto.close();
    }
    
}

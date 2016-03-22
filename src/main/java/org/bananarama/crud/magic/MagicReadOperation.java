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
import org.bananarama.crud.ReadOperation;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
public class MagicReadOperation<O,D,M extends ObjToDto<O,D>> implements ReadOperation<O> {
    private final M mapper;
    
    public MagicReadOperation(M mapper){
        this.mapper = mapper;
    }
    
    @Override
    public Stream<O> all() {
        Stream<D> dtos = BananaRama.read(mapper.dtoType()).all();
        return dtos.map(mapper::toObj);
    }

    @Override
    public Stream<O> all(QueryOptions options) {
        Stream<D> dtos = BananaRama.read(mapper.dtoType()).all(options);
        return dtos.map(mapper::toObj);
    }
    
    @Override
    public <Q> Stream<O> where(Q whereClause) {
        Stream<D> dtos = BananaRama.read(mapper.dtoType()).where(whereClause);
        return dtos.map(mapper::toObj);
    }

    @Override
    public <Q> Stream<O> where(Q whereClause, QueryOptions options) {
        Stream<D> dtos = BananaRama.read(mapper.dtoType()).where(whereClause,options);
        return dtos.map(mapper::toObj);
    }

    @Override
    public Stream<O> fromKeys(List<?> keys) {
        Stream<D> dtos = BananaRama.read(mapper.dtoType()).fromKeys(keys);
        return dtos.map(mapper::toObj);
    }

    @Override
    public Stream<O> fromKeys(List<?> keys,QueryOptions options) {
        Stream<D> dtos = BananaRama.read(mapper.dtoType()).fromKeys(keys,options);
        return dtos.map(mapper::toObj);
    }
    
}

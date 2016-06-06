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

/**
 *
 * @author Guglielmo De Concini
 */
public interface ObjToDto<O,D> {
    /**
     * 
     * @param obj The object to transform in DTO
     * @return the DTO
     */
    D toDto(O obj);
    
    /**
     * 
     * @param dto The dto to Transform in Object
     * @return 
     */
    O toObj(D dto);
    
    /**
     * Returns the .class for the DTO
     * @return the DTO type
     */
    Class<D> dtoType();
    
    /**
     * Returns the .class for the Object
     * @return the Object type
     */
    Class<O> objType();
}

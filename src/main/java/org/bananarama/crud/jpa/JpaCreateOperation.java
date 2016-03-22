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
package org.bananarama.crud.jpa;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.CreateOperation;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

/**
 * 
 * @author Guglielmo De Concini
 */
public class JpaCreateOperation<T> extends AbstractJpaOperation<T> implements CreateOperation<T>{

    public JpaCreateOperation(EntityManager em,Class<T> clazz) {
        super(em,clazz);
    }
    
    @Override
    public CreateOperation<T> from(Stream<T> data) {
        
        em.getTransaction().begin();
        data.forEach(em::persist);
        em.getTransaction().commit();
        
        return this;
    }

    @Override
    public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
        return from(data);
    }


    
}
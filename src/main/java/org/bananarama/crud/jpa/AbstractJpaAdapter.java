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

import org.bananarama.crud.Adapter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * 
 * @author Guglielmo De Concini
 */
public class AbstractJpaAdapter implements Adapter<Object>{

    protected final EntityManagerFactory entityManagerFactory;
    
    public AbstractJpaAdapter(String persistenceUnitName){
        this(Persistence.createEntityManagerFactory(persistenceUnitName));
    }
    
    public AbstractJpaAdapter(EntityManagerFactory factory){
        this.entityManagerFactory = factory;    
    }
    
    @Override
    public <T> JpaCreateOperation<T> create(Class<T> clazz) {
        return new JpaCreateOperation<>(entityManagerFactory.createEntityManager(),clazz);
    }

    @Override
    public <T> JpaReadOperation<T> read(Class<T> clazz) {
        return new JpaReadOperation<>(entityManagerFactory.createEntityManager(),clazz);
    }

    @Override
    public <T> JpaUpdateOperation<T> update(Class<T> clazz) {
        return new JpaUpdateOperation<>(entityManagerFactory.createEntityManager(),clazz);
    }

    @Override
    public <T> JpaDeleteOperation<T> delete(Class<T> clazz) {
        return new JpaDeleteOperation<>(entityManagerFactory.createEntityManager(),clazz);
    }
    
    public EntityManager getEntityManager(){
    	return entityManagerFactory.createEntityManager();
    }
    
    
}

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

import javax.persistence.EntityManager;

/**
 * 
 * @author Guglielmo De Concini
 */
public class AbstractJpaOperation<T> implements AutoCloseable{

    protected final EntityManager em;
    protected final Class<T> clazz;
    
    public AbstractJpaOperation(EntityManager em,Class<T> clazz){
        this.em = em;
        this.clazz = clazz;
    }

   
    @Override
    public void close() throws Exception {
        if(em.getTransaction().isActive()){
            em.getTransaction().rollback();
        }
        if(em.isOpen()){
            em.close();
        }
    }
    
    
    public void beginTransaction(){
        em.getTransaction().begin();
    }
    
    public void commitTransaction(){
        em.getTransaction().commit();
    }
}

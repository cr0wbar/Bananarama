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

import java.io.IOException;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * 
 * @author Guglielmo De Concini
 */
public class AbstractJpaOperation<T> implements AutoCloseable{

    protected final EntityManagerFactory factory;
    protected final Class<T> clazz;
    
    public AbstractJpaOperation(EntityManagerFactory factory,Class<T> clazz){
        this.factory = factory;
        this.clazz = clazz;
    }
   
    protected void close(EntityManager em) {
        if(em.getTransaction().isActive()){
            em.getTransaction().rollback();
        }
        if(em.isOpen()){
            em.close();
        }
    }
    
    
     /**
     * @return the {@link Entity} name value or {@link Class#getSimpleName()} if
     * not annotated with {@link Entity} or the {@link Entity#name()} is not defined
     */
    public static String getEntityName(Class<?> clazz){
        Entity entity = clazz.getAnnotation(Entity.class);
        if(entity != null && !entity.name().isEmpty())
               return entity.name();
        
        return clazz.getSimpleName();
    }

    @Override
    public void close() throws IOException {
        //NO-OP
    }
    
}

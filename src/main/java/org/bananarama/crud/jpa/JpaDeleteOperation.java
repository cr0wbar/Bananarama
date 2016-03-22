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

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

/**
 * 
 * @author Guglielmo De Concini
 */
public class JpaDeleteOperation<T> extends AbstractJpaOperation<T> implements DeleteOperation<T>,AutoCloseable{

    private final String DELETE;
    
    public JpaDeleteOperation(EntityManager em,Class<T> clazz){
        super(em,clazz);
        DELETE = "DELETE from " + clazz.getSimpleName();
    }
    
    @Override
    public <Q> DeleteOperation<T> where(Q obj) {
        
        return where(obj, null);
    }

    @Override @SuppressWarnings("unchecked")
    public <Q> DeleteOperation<T> where(Q obj, QueryOptions options) {
        if(obj instanceof Query){
            Query query = (Query) obj;
            String whereClause = CQE2SQL.convertCqQuery(query, options);
            
            em.getTransaction().begin();
            em.createQuery(DELETE + whereClause).executeUpdate();
            em.getTransaction().commit();
        }
        else if(clazz.equals(obj.getClass()))
            return from(Stream.of((T)obj),options);
        else
            throw new IllegalArgumentException(getClass().getName() + " cannot consume predicate of type " + obj.getClass());
        
        return this;
    }

    @Override
    public DeleteOperation<T> from(Stream<T> data) {
        
        em.getTransaction().begin();
        data.forEach(el-> em.remove(em.contains(el) ? el : em.merge(el)));
        em.getTransaction().commit();
        
        return this;
    }

    @Override
    public DeleteOperation<T> from(Stream<T> data, QueryOptions options) {
        return from(data);
    }
    
}

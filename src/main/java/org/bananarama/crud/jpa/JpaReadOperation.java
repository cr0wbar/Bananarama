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
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.jpa.JpaOperationOption.QueryType;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * 
 * @author Guglielmo De Concini
 */
public class JpaReadOperation<T> extends AbstractJpaOperation<T> implements ReadOperation<T>{

    private static final String FROM = " FROM ";
        
    public JpaReadOperation(EntityManagerFactory factory,Class<T> clazz) {
        super(factory,clazz);
    }

    @Override
    public Stream<T> all() {
        //Simple Select all operation
        final EntityManager em = factory.createEntityManager();
        
        CriteriaQuery<T> cq = em.getCriteriaBuilder()
                .createQuery(clazz);
        Root<T> rootEntry = cq.from(clazz);
        CriteriaQuery<T> all = cq.select(rootEntry);
        
        List<T> results = em.createQuery(all).getResultList();
        close(em);
        
        return results.stream();
    }

    @Override
    public Stream<T> all(QueryOptions options) {
       return all();
    }

    @Override
    public <Q> Stream<T> where(Q obj) {
        return where(obj, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <Q> Stream<T> where(Q obj, QueryOptions options) {
        String clause;
        
        if(obj instanceof Query){
            Query query = (Query)obj;
            clause = CQE2SQL.convertCqQuery(query, options);
        }
        else if(obj instanceof String){
            clause = (String) obj;
        }
        else 
            throw new IllegalArgumentException("Can't use " + obj.getClass().getName() + " in " + getClass().getName());
        
        //For now we will use this simple approach, which transforms the query to a string,
        //JPA will then reparse the string in order to transform it in matching criteria
        final javax.persistence.Query query;
        final JpaOperationOption jpaOperationOption;
        
        final EntityManager em = factory.createEntityManager();
        
        
        if(options != null
                && (jpaOperationOption = options.get(JpaOperationOption.class)) != null
                && jpaOperationOption.getQueryType().equals(QueryType.NATIVE))
            query = em.createNativeQuery(clause,clazz);
        else
            query = em.createQuery(FROM + getEntityName(clazz) + clause,clazz);
        
        List<T> results = query.getResultList();
        
        em.close();
        
        return results.stream();
    }
    

    @Override
    public Stream<T> fromKeys(List<?> keys) {
        final EntityManager em = factory.createEntityManager();
        final List<T> results =  keys.stream()
                .map(key -> em.find(clazz, key))
                .collect(Collectors.toList());        
        close(em);
        return results.stream();
    }

    @Override
    public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
        return fromKeys(keys);
    }
   
    
}

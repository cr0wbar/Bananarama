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
package basic;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import org.bananarama.crud.Adapter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Guglielmo De Concini
 */
public class ListAdapter implements Adapter<Entry>{

    private List<Entry> backend = new ArrayList<>();

    @Override
    public <T extends Entry> CreateOperation<T> create(Class<T> clazz) {
       return new CreateOperation<T>() {
           @Override
           public CreateOperation<T> from(Stream<T> data) {
               data.forEach(backend::add);
               return this;
           }

           @Override
           public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
               return from(data);
           }
       };
    }

    @Override
    public <T extends Entry> ReadOperation<T> read(Class<T> clazz) {
       return new ReadOperation<T>() {
           @Override @SuppressWarnings("unchecked")
           public Stream<T> all() {
              return (Stream<T>) backend.stream();
           }

           @Override
           public Stream<T> all(QueryOptions options) {
              return all();
           }

           @Override
           public <Q> Stream<T> where(Q whereClause) {
               throw new UnsupportedOperationException("Not supported yet.");
           }

           @Override
           public <Q> Stream<T> where(Q whereClause, QueryOptions options) {
               throw new UnsupportedOperationException("Not supported yet."); 
           }

           @Override
           public Stream<T> fromKeys(List<?> keys) {
               throw new UnsupportedOperationException("Not supported yet.");
           }

           @Override
           public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
               throw new UnsupportedOperationException("Not supported yet.");
           }
       };
    }

    @Override
    public <T extends Entry> UpdateOperation<T> update(Class<T> clazz) {
       return new UpdateOperation<T>() {

           @Override
           public UpdateOperation<T> from(Stream<T> data) {
              data.filter(backend::remove)
                      .forEach(backend::add);
                      
              return this;
           }

           @Override
           public UpdateOperation<T> from(Stream<T> data, QueryOptions options) {
              return from(data);
           }
       };
    }

    @Override
    public <T extends Entry> DeleteOperation<T> delete(Class<T> clazz) {
       return new DeleteOperation<T>() {
           @Override
           public <Q> DeleteOperation<T> where(Q whereClaus) {
               throw new UnsupportedOperationException("Not supported yet.");
           }

           @Override
           public <Q> DeleteOperation<T> where(Q whereClaus, QueryOptions options) {
               if(whereClaus instanceof Query){
                   Query<T> query = (Query<T>)whereClaus;
                   backend.removeIf(t -> query.matches((T)t, options));
                   return this;
               }
               throw new UnsupportedOperationException("Not supported yet.");
           }

           @Override
           public DeleteOperation<T> from(Stream<T> data) {
               data.forEach(backend::remove);
               return this;
           }

           @Override
           public DeleteOperation<T> from(Stream<T> data, QueryOptions options) {
               return from(data);
           }
       };
    }
    

    
}

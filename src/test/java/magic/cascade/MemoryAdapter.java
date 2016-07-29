package magic.cascade;

import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.bananarama.crud.Adapter;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;

/**
 * Simple adapter that store data in a public static map
 * @author Tommaso Doninelli <tommaso.doninelli@gmail.com>
 */
public class MemoryAdapter implements Adapter<Object>{
    
    protected static final List<Object> datastore = Collections.synchronizedList(new ArrayList<>());
    
    @Override
    public <T> CreateOperation<T> create(Class<T> clazz) {
        return new CreateOperation<T>() {
            @Override
            public CreateOperation<T> from(Stream<T> data) {
                data.forEach(datastore::add);
                return this;
            }

            @Override
            public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void close() throws IOException {}
        };
    }

    @Override
    public <T> ReadOperation<T> read(Class<T> clazz) {
        return new ReadOperation<T>() {
            @Override
            public Stream<T> all() {
                return (Stream<T>) datastore.stream();
            }

            @Override
            public Stream<T> all(QueryOptions options) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <Q> Stream<T> where(Q whereClause) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <Q> Stream<T> where(Q whereClause, QueryOptions options) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Stream<T> fromKeys(List<?> keys) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void close() throws IOException {}
        };
    }

    @Override
    public <T> UpdateOperation<T> update(Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> DeleteOperation<T> delete(Class<T> clazz) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}

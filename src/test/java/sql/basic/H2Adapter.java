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
package sql.basic;

import org.bananarama.crud.sql.SqlAdapter;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author Guglielmo De Concini
 */
public class H2Adapter extends SqlAdapter implements Closeable{
    public static final DataSource ds = prepareDs();
    public final Connection connection;
    
    public H2Adapter() {
        super(ds);
        try {
            //Must keep alive at least one connection otherwise in-memory H2DB resets
            connection = ds.getConnection();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private static DataSource prepareDs(){
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:db" );
        return ds;
    }
  
    public void doUpdate(String sql){
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.execute();
            connection.commit();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

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

import org.bananarama.crud.sql.column.SqlTypeConverter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.bananarama.exception.BananaRamaException;


/**
 *
 * @author Guglielmo De Concini
 */
public class LocalDateTimeConverter implements SqlTypeConverter<LocalDateTime>{
    
    @Override
    public LocalDateTime read(ResultSet rs, int index) throws BananaRamaException {
        try{
            Timestamp ts;
            return (ts = rs.getTimestamp(index)) == null ? null : ts.toLocalDateTime();
        }
        catch(SQLException ex){
            throw new BananaRamaException("Can't convert LocalDateTime ",ex);
        }
    }
    
    @Override
    public void write(PreparedStatement ps, int index, LocalDateTime obj) throws BananaRamaException {
        try{
            if(obj == null)
                ps.setNull(index, java.sql.Types.TIMESTAMP);
            else
                ps.setTimestamp(index, Timestamp.valueOf(obj));
        }
        catch(SQLException ex){
            throw new BananaRamaException("Can't convert LocalDateTime ",ex);
        }
    }
    
    
}

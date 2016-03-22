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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Guglielmo De Concini
 */
public class MapConverter implements SqlTypeConverter<Map<String,String>>{

    @Override
    public Map<String, String> read(ResultSet rs, int index) throws SQLException {
        String text = rs.getString(index);
        Map<String,String> map = new HashMap<>();
        
        for(int i=0,j;i<text.length();i=j+1){
            j = text.indexOf('|',i);
            if(j < 0) j = text.length();
            String match = text.substring(i, j);
            int s = match.indexOf('/');
            map.put(match.substring(0,s),match.substring(s));         
        }
        
        return map;
    }

    @Override
    public void write(PreparedStatement ps, int index, Map<String, String> obj) throws SQLException {
       String text = obj.entrySet()
               .stream()
               .map(e -> e.getKey() + "/" + e.getValue())
               .reduce((s,t) -> s+"|"+t)
               .orElse("");
              
       ps.setObject(index, text);
    }
    
}

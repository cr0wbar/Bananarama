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

import org.bananarama.crud.sql.annotation.Column;
import org.bananarama.crud.sql.annotation.Convert;
import org.bananarama.crud.sql.annotation.ConvertWith;
import org.bananarama.crud.sql.annotation.Id;
import org.bananarama.crud.sql.annotation.Table;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Guglielmo De Concini
 */
@Table(name = "pojo")
@Convert(type = LocalDateTime.class,with = LocalDateTimeConverter.class)
public class Pojo {
    @Id 
    private int id;

    @Column(name = "name")
    private String laBel;
    
    private Double xyz;
    
    @ConvertWith(MapConverter.class)
    private Map<String,String> attrs;
    
    private LocalDateTime toa;
    
    public int getId() {
        return id;
    }

    public void setId(int idno) {
        this.id = idno;
    }

    public String getLaBel() {
        return laBel;
    }

    public void setLaBel(String laBel) {
        this.laBel = laBel;
    }

    public Double getXyz() {
        return xyz;
    }

    public void setXyz(Double xyz) {
        this.xyz = xyz;
    }
    
    public LocalDateTime getToa() {
        return toa;
    }

    public void setToa(LocalDateTime toa) {
        this.toa = toa;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
    
    public static Pojo newInstance(int id){
        Pojo pojo = new Pojo();
        pojo.setId(id);
        pojo.setLaBel("POJO:id="+id);
        pojo.setXyz(id+0.5);
        pojo.setToa(LocalDateTime.now());
        HashMap<String,String> map = new HashMap<>();
        map.put("label",pojo.getLaBel());
        map.put("time", pojo.getToa().toString());
        pojo.setAttrs(map);
        return pojo;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pojo){
            Pojo other = (Pojo) obj;
            return id == other.id &&  laBel.equals(other.laBel);
        }
        return false;
    }
    
}


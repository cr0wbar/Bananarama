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
package sql.jpa;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.annotation.Banana;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * 
 * @author Guglielmo De Concini
 */
@Entity
@Banana(adapter = H2Adapter.class)
public class Simpleton {
    private String description;
    
    @Id
    private int id;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name="utls",
            joinColumns={@JoinColumn(name="simpletonId", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="itemId", referencedColumnName="id")}
    )
    private List<Item> items;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
     
    private static final Attribute<Simpleton,Integer> ID = new SimpleAttribute<Simpleton, Integer>("id") {
        @Override
        public Integer getValue(Simpleton object, QueryOptions queryOptions) {
            return object.id;
        }
    };
    
    public List<Item> getItems(){
        if(items == null)
            items = new ArrayList<>();
        return items;
    }
    
    private static final Attribute<Simpleton,String> DESCRIPTION = new SimpleNullableAttribute<Simpleton, String>("description") {
        @Override
        public String getValue(Simpleton object, QueryOptions queryOptions) {
            return object.description;
        }
    };

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Simpleton){
            Simpleton other = (Simpleton)obj;
            return !(description != null ^ other.description != null)
                    && description.equals(other.description)
                    && id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.description);
        hash = 23 * hash + this.id;
        return hash;
    }
    
}

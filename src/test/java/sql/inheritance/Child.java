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
package sql.inheritance;

import org.bananarama.annotation.Banana;
import org.bananarama.crud.sql.annotation.Table;
import sql.basic.H2Adapter;

/**
 *
 * @author Guglielmo De Concini
 */
@Banana(adapter = H2Adapter.class)
@Table(name = "child",inheritFields = true)
public class Child extends Parent{
    
    private long time;

    public Child(){
        
    }
    
    public Child(int id,String name,long time){
        super(id,name);
        this.time = time;
    }
    
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
    
    public static Child newInstance(int id){
        return new Child(id, String.valueOf(id), id + 100);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Child){
            Child other = (Child)obj;
            return getId() == other.getId()
                    && getName().equals(other.getName())
                    && getTime() == other.getTime();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId();
    }
    
    
}

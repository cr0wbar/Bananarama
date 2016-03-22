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
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.annotation.Banana;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * @author Guglielmo De Concini
 */
@Entity
@Banana(adapter = H2Adapter.class)
public class Someone {
    @Id
    private String name;
    
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public static final Attribute<Someone,Integer> AGE = new SimpleAttribute<Someone, Integer>("age") {
        @Override
        public Integer getValue(Someone object, QueryOptions queryOptions) {
            return object.age;
        }
    };
    
    public static final Attribute<Someone,String> NAME = new SimpleAttribute<Someone, String>("name") {
        @Override
        public String getValue(Someone object, QueryOptions queryOptions) {
            return object.name;
        }
    };
}

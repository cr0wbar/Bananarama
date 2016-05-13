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

import org.bananarama.annotation.Banana;

/**
 * 
 * @author Guglielmo De Concini
 */
@Banana(adapter = ListAdapter.class)
public class Entry {
    private final String key;
    private String val;
    
    public Entry(String key,String val){
        this.val = val;
        this.key = key;
    }
    
    public String getValue(){
        return val;
    }

    public String getKey(){
        return key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Entry)
            return  key.equals(((Entry)obj).key);
       return false;
    }

    @Override
    public int hashCode() {
       return val.hashCode();
    }
    
    
}

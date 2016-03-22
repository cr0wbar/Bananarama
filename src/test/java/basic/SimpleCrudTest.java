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

import org.bananarama.BananaRama;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Guglielmo De Concini
 */
public class SimpleCrudTest {
    
    
    @Test
    public void crudTest() {

        Entry entry = new Entry("testKey","testVal");
        
        ReadOperation<Entry> read = BananaRama.read(Entry.class);
        assertNotNull(read);
        
        assertEquals(0, read.all().count());
        
        CreateOperation<Entry> create = BananaRama.create(Entry.class);
        assertNotNull(create);
        
        create.from(Stream.of(entry));
        
        assertEquals(1, read.all().count());
        
        assertEquals(entry.getValue(),read.all().findFirst().get().getValue());
        
        Entry entryUpdated = new Entry("testKey","testValUpdated");
        
        BananaRama.update(Entry.class).from(Stream.of(entryUpdated));
        assertEquals(1, read.all().count());

        assertEquals(entryUpdated.getValue(),read.all().findFirst().get().getValue());
        
        DeleteOperation<Entry> delete = BananaRama.delete(Entry.class);
        assertNotNull(delete);
        
        delete.from(Stream.of(entryUpdated));
        assertEquals(0, read.all().count());
        
    }
}

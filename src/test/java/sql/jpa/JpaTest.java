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

import static com.googlecode.cqengine.query.QueryFactory.*;
import org.bananarama.BananaRama;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Guglielmo De Concini
 */
public class JpaTest {
    private final BananaRama bananarama;
    
    public JpaTest(){
        this.bananarama = new BananaRama();
    }
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);//JPA is very verbose...
    }
    
    @AfterClass
    public static void tearDownClass() {
        BasicConfigurator.resetConfiguration();
    }

    public void checkCount(int n,ReadOperation<Simpleton> read){
        assertEquals(n,read.all().count());
        assertEquals(n,bananarama.read(Simpleton.class).all().count());
        assertEquals(n,bananarama.using(H2Adapter.class).read(Simpleton.class).all().count());
    }
    
    @Test
    public void jpaTest() {
        Simpleton simp = new Simpleton();
        simp.setId(1);
        simp.setDescription("immalive");
        
        CreateOperation<Simpleton> create = bananarama.create(Simpleton.class);
        ReadOperation<Simpleton> read = bananarama.read(Simpleton.class);
        UpdateOperation<Simpleton> update = bananarama.update(Simpleton.class);
        DeleteOperation<Simpleton> delete = bananarama.delete(Simpleton.class);
        
        assertNotNull(create);
        assertNotNull(read);
        assertNotNull(update);
        assertNotNull(delete);
        
        checkCount(0, read);
        
        create.from(Stream.of(simp));
        
        checkCount(1, read);
        Simpleton saved = read.all().findFirst().get();
        
        assertEquals(simp, saved);
        
        saved.setDescription("immaupdated");
        
        update.from(Stream.of(saved));
        
        checkCount(1, read);
        
        Simpleton updated = read.all().findFirst().get();
        
        assertNotSame(simp, updated);
        assertEquals(saved, updated);
        
        //Check that items that are not 
        //in the persistence layer are not affected
        simp.setId(2);
        delete.from(Stream.of(simp));
        checkCount(1, read);
                
        delete.from(Stream.of(updated));
        
        checkCount(0, read);
    }
    
    @Test 
    public void testManyToMany(){
        
        Simpleton simp = new Simpleton();
        simp.setId(2);
        simp.setDescription("goofy");
        
        Item hammer = new Item();
        hammer.setId(1);
        hammer.setDescription("A hammer");
        
        simp.getItems().add(hammer);
        bananarama.create(Simpleton.class)
                .from(Stream.of(simp));
        
        List<Item> items  = bananarama.read(Item.class)
                .all()
                .collect(Collectors.toList());
        
        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getUsers().size());
        assertEquals(hammer.getDescription(), items.get(0).getDescription());

        simp = bananarama.read(Simpleton.class).all().findFirst().get();
        assertEquals(1, simp.getItems().size());
        simp.getItems().clear();
        bananarama.update(Simpleton.class).from(Stream.of(simp));    
        simp = bananarama.read(Simpleton.class).all().findFirst().get();
        assertEquals(0, simp.getItems().size());
        
        items  = bananarama.read(Item.class)
                .all()
                .collect(Collectors.toList());
        
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getUsers().size());

    }
    
    
    @Test
    public void testCqQueriesOnJpa(){
        Someone someone = new Someone();
        
        someone.setAge(27);
        someone.setName("Gordon");
        
        bananarama.create(Someone.class)
                .from(Stream.of(someone));
        
        List<Someone> people = bananarama.read(Someone.class).all().collect(Collectors.toList());
        assertEquals(1, people.size());
        
        //Filter On Age
        assertEquals(1, bananarama.read(Someone.class).where(equal(Someone.AGE, 27)).count());
        assertEquals(0, bananarama.read(Someone.class).where(greaterThan(Someone.AGE, 27)).count());
        assertEquals(1, bananarama.read(Someone.class).where(greaterThanOrEqualTo(Someone.AGE, 27)).count());
        assertEquals(1, bananarama.read(Someone.class).where(lessThanOrEqualTo(Someone.AGE, 27)).count());
        assertEquals(0, bananarama.read(Someone.class).where(lessThan(Someone.AGE, 27)).count());
        
        //Filter on Name
        assertEquals(1, bananarama.read(Someone.class).where(equal(Someone.NAME, "Gordon")).count());
        assertEquals(0, bananarama.read(Someone.class).where(equal(Someone.NAME, "Victor")).count());
        
        //Delete on attribute
        bananarama.delete(Someone.class)
                .where(greaterThan(Someone.AGE,15));
        
        //Verify that it was deleted
        assertEquals(0, bananarama.read(Someone.class).where(equal(Someone.NAME, "Gordon")).count());
    }
   
}

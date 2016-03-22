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
package cache.cqengine;

import basic.ListAdapter;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.Index;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import static com.googlecode.cqengine.query.QueryFactory.*;
import org.bananarama.BananaRama;
import org.bananarama.cache.IndexedCollectionAdapter;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Guglielmo De Concini
 */
@SuppressWarnings("unchecked")
public class CacheTest {
    Logger log = Logger.getLogger(CacheTest.class);
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }
    
    @AfterClass
    public static void tearDownClass() {
        BasicConfigurator.resetConfiguration();
    }
    
    public void checkCount(int n,ReadOperation<?> read){
        assertEquals(n, read.all().count());
        assertEquals(n, BananaRama.read(CacheEntry.class).all().count());
        assertEquals(n, BananaRama.using(ListAdapter.class).read(CacheEntry.class).all().count());
        assertEquals(n, BananaRama.using(IndexedCollectionAdapter.class).read(CacheEntry.class).all().count());
    }
    
    @Test
    public void testCaching()  {
        CacheEntry entry = new CacheEntry("testKey", "testVal");
        
        CreateOperation<CacheEntry> create = BananaRama.create(CacheEntry.class);
        assertNotNull(create);
        
        ReadOperation<CacheEntry> read = BananaRama.read(CacheEntry.class);
        assertNotNull(read);
        
        UpdateOperation<CacheEntry> update = BananaRama.update(CacheEntry.class);
        assertNotNull(update);
        
        DeleteOperation<CacheEntry> delete = BananaRama.delete(CacheEntry.class);
        assertNotNull(delete);
        
        checkCount(0, read);
        
        create.from(Stream.of(entry));
        
        checkCount(1, read);
        
        assertEquals(entry.getKey(), read.all().findFirst().get().getKey());
        assertEquals(entry.getValue(), BananaRama.read(CacheEntry.class).all().findFirst().get().getValue());
        assertEquals(entry.getValue(), BananaRama.using(IndexedCollectionAdapter.class).read(CacheEntry.class).all().findFirst().get().getValue());
        assertEquals(entry.getValue(), BananaRama.using(ListAdapter.class).read(CacheEntry.class).all().findFirst().get().getValue());
        
        CacheEntry entryUpdated = new CacheEntry("testKey", "testValUpdated");
        
        update.from(Stream.of(entryUpdated));
        checkCount(1, read);
        
        assertEquals(entryUpdated.getKey(), read.all().findFirst().get().getKey());
        assertEquals(entryUpdated.getValue(), BananaRama.read(CacheEntry.class).all().findFirst().get().getValue());
        assertEquals(entryUpdated.getValue(), BananaRama.using(IndexedCollectionAdapter.class).read(CacheEntry.class).all().findFirst().get().getValue());
        assertEquals(entryUpdated.getValue(), BananaRama.using(ListAdapter.class).read(CacheEntry.class).all().findFirst().get().getValue());
        
        delete.from(Stream.of(entryUpdated));
        checkCount(0, read);
        
        //Check selection and deletion with CQQuery
        create.from(Stream.of(entryUpdated));
        checkCount(1, read);
        
        assertEquals(1, BananaRama.read(CacheEntry.class).where(equal(CacheEntry.VAL, entryUpdated.getVal())).count());
        
        BananaRama.delete(CacheEntry.class).where(equal(CacheEntry.KEY,entryUpdated.getKey()+"a"));
        checkCount(1, read);
        
        BananaRama.delete(CacheEntry.class).where(equal(CacheEntry.KEY,entryUpdated.getKey()));
        checkCount(0, read);
        
        //Check indexes creation
        try{
            IndexedCollectionAdapter adap = BananaRama.using(IndexedCollectionAdapter.class);
            
            Method m = IndexedCollectionAdapter.class.getDeclaredMethod("getCollection", Class.class);
            m.setAccessible(true);//Is a private, method, this is kind of ugly..
            IndexedCollection<CacheEntry> coll = (IndexedCollection<CacheEntry>) m.invoke(adap, CacheEntry.class);
            
            HashIndex hashIndex = null;
            NavigableIndex navigableIndex = null;
            
            for(Index index : coll.getIndexes()){
                if(index instanceof HashIndex){
                    hashIndex = (HashIndex) index;
                }
                else if(index instanceof NavigableIndex){
                    navigableIndex = (NavigableIndex) index;
                }
            }
            assertNotNull(hashIndex);
            assertNotNull(navigableIndex);
            assertEquals(CacheEntry.KEY,hashIndex.getAttribute() );
            assertEquals(CacheEntry.VAL, navigableIndex.getAttribute());
            
        }
        catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException |  InvocationTargetException ex){
            throw new RuntimeException("Index checking failed",ex);
        }
    }
    
    @Test
    public void testFieldInheritance(){
        CacheInheritedEntry entry = new CacheInheritedEntry();
        entry.setName("gordon");
        entry.setIdno(1);
        
        BananaRama.create(CacheInheritedEntry.class)
                .from(Stream.of(entry));
        
        CacheInheritedEntry read = BananaRama.read(CacheInheritedEntry.class)
                .all()
                .findFirst()
                .orElse(null);
        
        assertNotNull(read);
        assertTrue(read == entry);
        try{
            IndexedCollectionAdapter adap = BananaRama.using(IndexedCollectionAdapter.class);
            
            Method m = IndexedCollectionAdapter.class.getDeclaredMethod("getCollection", Class.class);
            m.setAccessible(true);//Is a private, method, this is kind of ugly..
            IndexedCollection<CacheEntry> coll = (IndexedCollection<CacheEntry>) m.invoke(adap, CacheInheritedEntry.class);
            
            List<Index<CacheInheritedEntry>> indexes  = new ArrayList<>();
            
            for(Index index : coll.getIndexes())
                indexes.add(index);
            
            assertEquals(3,indexes.size());
            
        }
        catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException |  InvocationTargetException ex){
            throw new RuntimeException("Index checking failed",ex);
        }
    }
}

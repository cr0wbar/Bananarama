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

import org.bananarama.BananaRama;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sql.inheritance.Child;

/**
 *
 * @author Guglielmo De Concini
 */
public class SqlTest {
    private static H2Adapter adap;
    
    @BeforeClass
    public static void prepare(){
        BasicConfigurator.configure();
        adap = BananaRama.using(H2Adapter.class);
    }
    
    @AfterClass
    public static void destroy() throws IOException{
        BasicConfigurator.resetConfiguration();
        adap.close();
    }
    
    @Test
    public void testPojoCrud(){
        testBatch(100);     
    }
    
    @Test
    public void testPojoMultiIdCrud(){
        testBatchMultiId(100);     
    }
    
    @Test
    public void testInheritance(){
        //Create table;;
        
        String sql = "CREATE TABLE child (" +
                "id integer not null, " +
                "name char(64)," +
                "time bigint);";
        
        adap.doUpdate(sql);
        
        final int n = 5;
        List<Child> children = IntStream
                .range(0, n)
                .mapToObj(Child::newInstance)
                .collect(Collectors.toList());
        
        BananaRama.create(Child.class)
                .from(children.stream());
        
        List<Child> read = BananaRama.read(Child.class)
                .all()
                .sorted((c1,c2) -> c1.getId() - c2.getId())
                .collect(Collectors.toList());
        
        for(int i=0;i<5;i++)
            Assert.assertEquals(children.get(i),read.get(i));
        
        adap.doUpdate("DROP TABLE child;");
        
    }
    
    private void testBatch(int n){
        List<Pojo> created = IntStream.range(0,n)
                .mapToObj(Pojo::newInstance)
                .collect(Collectors.toList());
        
        String sql = "CREATE TABLE pojo (" +
                "id integer not null, " +
                "xyz double," +
                "toa timestamp," +
                "attrs char(128)," +
                "name char(128)," +
                "primary key (id));";
        
        adap.doUpdate(sql);
        //Create records on DB
        CreateOperation<Pojo> creator = adap.create(Pojo.class);
        creator.from(created.stream());
        
        //Read Records
        ReadOperation<Pojo> reader = adap.read(Pojo.class);
        List<Pojo> read = reader
                .all()
                .collect(Collectors.toList());
        
        //Update records
        Assert.assertEquals(created.size(),read.size());  
        List<Pojo> pojos = read.subList(n/4, n/2);
        for(Pojo pojo : pojos)
            pojo.setLaBel("UPDATE|"+pojo.getId());
        
        List<Integer> indexes = IntStream.range(n/4,n/2)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
        
        UpdateOperation<Pojo> updater = adap.update(Pojo.class);
        updater.from(pojos.stream());
        
        List<Pojo> updated = reader.fromKeys(indexes)
                .collect(Collectors.toList());
        Assert.assertEquals(pojos.size(),updated.size());
        
        for(Pojo pojo : updated)
            Assert.assertEquals(pojo.getLaBel(),"UPDATE|"+pojo.getId());
        
        //Delete Records
        DeleteOperation<Pojo> deleter = adap.delete(Pojo.class);
        deleter.from(read.stream());      
        
        //Check that records were deleted
        Assert.assertEquals(0,reader.all().count());
        
    }
    
    private void testBatchMultiId(int n){
        List<MultipleIdColPojo> created = IntStream.range(0,n)
                .mapToObj(MultipleIdColPojo::newInstance)
                .collect(Collectors.toList());
        
        String sql = "CREATE TABLE mpojo (" +
                "id integer not null, " +
                "textid char(64) not null, " +
                "xyz double," +
                "toa timestamp," +
                "attrs char(128)," +
                "name char(128)," +
                "primary key (id,textid));";
        
        adap.doUpdate(sql);
        //Create records on DB
        CreateOperation<MultipleIdColPojo> creator = adap.create(MultipleIdColPojo.class);
        creator.from(created.stream());
        
        //Read Records
        ReadOperation<MultipleIdColPojo> reader = adap.read(MultipleIdColPojo.class);
        List<MultipleIdColPojo> read = reader
                .all()
                .collect(Collectors.toList());
        
        //Update records
        Assert.assertEquals(created.size(),read.size());  
        List<MultipleIdColPojo> pojos = read.subList(n/4, n/2);
        for(MultipleIdColPojo pojo : pojos)
            pojo.setLaBel("UPDATE|"+pojo.getId());
        
        List<Object> keys = new ArrayList<>(n/2);
                
       IntStream.range(n/4,n/2).forEach( i -> {
            keys.add(i);
            keys.add(String.valueOf(i));
        });
        
        UpdateOperation<MultipleIdColPojo> updater = adap.update(MultipleIdColPojo.class);
        updater.from(pojos.stream());
        
        List<MultipleIdColPojo> updated = reader.fromKeys(keys)
                .collect(Collectors.toList());
        Assert.assertEquals(pojos.size(),updated.size());
        
        for(MultipleIdColPojo pojo : updated)
            Assert.assertEquals(pojo.getLaBel(),"UPDATE|"+pojo.getId());
        
        //Delete Records
        DeleteOperation<MultipleIdColPojo> deleter = adap.delete(MultipleIdColPojo.class);
        deleter.from(read.stream());      
        
        //Just check if NoOp deletion works
        deleter.from(Stream.of(MultipleIdColPojo.newInstance(1)));
        
        //Check that records were deleted
        Assert.assertEquals(0,reader.all().count());
        
    }
}

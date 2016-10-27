/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package magic;

import basic.ListAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bananarama.BananaRama;
import org.bananarama.crud.magic.MagicAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assert;

/**
 *
 * @author Guglielmo De Concini
 */
public class TestMagic {
    final BananaRama bananarama;
    public TestMagic() {
        bananarama = new BananaRama();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    public boolean isDtoEqualToObj(SimpleObj obj, SimpleDto dto){
        boolean res = (obj.getId() == dto.getId());
        res |= dto.getDate().equals(obj.getDate().format(DateTimeFormatter.ISO_DATE));
        return res;
    }
    
    
    @Test
    public void testMagicConversion() {
        List<SimpleObj> objs = IntStream.range(0, 10)
                .mapToObj(SimpleObj::new)
                .collect(Collectors.toList());
        objs.forEach(obj -> obj.setDate(LocalDate.now()));
        
        MagicAdapter magic = new MagicAdapter(bananarama);
        magic.create(SimpleObj.class).from(objs.stream()).andReturn();
        
        //Check in underlying layer
        ListAdapter listAd = bananarama.using(ListAdapter.class);
        
        List<SimpleDto> dtos = listAd.read(SimpleDto.class).all().collect(Collectors.toList());
        assertEquals("Magic Create failed",objs.size(), dtos.size());
        
        IntStream.range(0, objs.size())
                .forEach(i -> isDtoEqualToObj(objs.get(i), dtos.get(i)));
                
        //TestRead
        List<SimpleObj> objsRead = magic.read(SimpleObj.class)
                .all()
                .sorted((i,j) -> i.getId() - j.getId())
                .collect(Collectors.toList());
        
        assertEquals("Magic read failed",objs.size(), objsRead.size());
                
        IntStream.range(0, objs.size())
                .forEach(i-> assertEquals(objs.get(i), objsRead.get(i)));
        
        //TestUpdate
        List<SimpleObj> updatedObjs = IntStream.range(0, objs.size())
                .mapToObj(SimpleObj::new)
                .collect(Collectors.toList());
        
        magic.update(SimpleObj.class)
                .from(updatedObjs.stream());
        
        List<SimpleObj> objsReadAgain = magic.read(SimpleObj.class)
                .all()
                .sorted((i,j) -> i.getId() - j.getId())
                .collect(Collectors.toList());
        
        assertEquals("Magic Update failed", objs.size(),objsReadAgain.size());
       
        updatedObjs.stream()
                .map(SimpleObj::getDate)
                .forEach(Assert::assertNull);
        
        //TestDelete
        magic.delete(SimpleObj.class).from(objs.stream());
        
        assertEquals("Magic Delete did now work",0,listAd.read(SimpleObj.class).all().count());
        
        
        magic.create(SimpleObj.class).from(objs.stream()).andReturn();

        magic.delete(SimpleObj.class).all();
        
        assertEquals("Magic Delete did now work",0,listAd.read(SimpleObj.class).all().count());
    
    }
}

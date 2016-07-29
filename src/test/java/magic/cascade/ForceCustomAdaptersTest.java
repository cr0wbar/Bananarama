/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magic.cascade;

import basic.ListAdapter;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import magic.SimpleDto;
import magic.SimpleMapper;
import magic.SimpleObj;
import org.bananarama.BananaRama;
import org.bananarama.annotation.Banana;
import org.bananarama.annotation.MapWith;
import org.bananarama.crud.magic.MagicAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Tommaso Doninelli <tommaso.doninelli@gmail.com>
 */
public class ForceCustomAdaptersTest {
    
    @Test
    public void useAnotherAdapter() throws NoSuchMethodException {
     
        Random rnd = new Random();
        
        /*
            SimpleObj has a MagicAdapter, that convert to SimpleDto;
            SimpleDto has a ListAdapter. 
            SimpleDto is representing a class declared final that comes from a third-party library,
            a class for which is not possible to declare the adapter at compile time.        
        */
        
        // Verify all the assumptions
        
        // SimpleObj is babanaed with a MAgicAdapter that map using SimpleMapper
        Assert.assertEquals(
               SimpleMapper.class, 
               Optional.ofNullable(SimpleObj.class.getDeclaredAnnotation(MapWith.class))
                    .map(MapWith::value)
                    .get());

        // SimpleMapper map to SimpleDto
        Assert.assertNotNull(SimpleMapper.class.getDeclaredMethod("toObj", SimpleDto.class));
       
        // SimpleDto is bananaed using a ListAdapter
        Assert.assertEquals(
               ListAdapter.class, 
               Optional.ofNullable(SimpleDto.class.getDeclaredAnnotation(Banana.class))
                    .map(Banana::adapter)
                    .get());
       
        Assert.assertEquals(0, MemoryAdapter.datastore.size());
     
        // Override the declared adapter (only for this run)        
        new BananaRama()
                .registerAdapter(MemoryAdapter.class, SimpleObj.class)
                .create(SimpleObj.class)
                .from(Stream.of(new SimpleObj(rnd.nextInt()), new SimpleObj(rnd.nextInt())));
                
        Assert.assertEquals(2, MemoryAdapter.datastore.size());
        Assert.assertEquals(0, new BananaRama().read(SimpleObj.class).all().count());
        
        MemoryAdapter.datastore.clear();
        BananaRama banana = new BananaRama();
        
        banana
                .create(SimpleObj.class)
                .from(Stream.of(new SimpleObj(rnd.nextInt()), new SimpleObj(rnd.nextInt())));
        
        Assert.assertEquals(0, MemoryAdapter.datastore.size()); 
        Assert.assertEquals(2, banana.read(SimpleObj.class).all().count());
    }
    
}

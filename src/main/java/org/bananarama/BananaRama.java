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
package org.bananarama;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bananarama.annotation.Banana;
import org.bananarama.annotation.BananaRamaAdapter;
import org.bananarama.cache.IndexedCollectionAdapter;
import org.bananarama.cache.annotation.BufferedOnIndexedCollection;
import org.bananarama.concurrency.StripedLock;
import org.bananarama.crud.Adapter;
import org.bananarama.crud.CreateOperation;
import org.bananarama.crud.DeleteOperation;
import org.bananarama.crud.ReadOperation;
import org.bananarama.crud.UpdateOperation;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author Guglielmo De Concini
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BananaRama implements Adapter<Object>{
    
    private final StripedLock slock;
    private final Map<Class<?>, Adapter<?>> adapters;
    
    public BananaRama(){
        this(32);
    }
    
    public BananaRama(int concurrencyLevel){
        slock = new StripedLock(concurrencyLevel);
        adapters = new HashMap<>();
        init();//Avoids leaking this in constructor
    }
   
    private void init(){
        adapters.put(BananaRama.class, this);
    }
 
    private  Adapter getAdapterForClass(Class<?> clazz){
        if(!clazz.isAnnotationPresent(Banana.class))    
            throw new IllegalArgumentException(clazz.getName() + " is not annotated with " + Banana.class.getName());
        
        // If type was annotated for buffering, we retrieve the IndexedCollectionAdapter
        if(clazz.isAnnotationPresent(BufferedOnIndexedCollection.class))
            return getAdapter(IndexedCollectionAdapter.class);
        
        Banana banana = clazz.getAnnotation(Banana.class);
        
        return getAdapter(banana.adapter());
    }
    
    private <T extends Adapter> T getAdapter(Class<T> adapterClass){
        //Acquire lock on class, this will block arriving calls for a specific
        //adapter, while the adapter is loading
        Lock lock = slock.getLock(adapterClass);
        lock.lock();
        
        try{
    
            T adapter = (T) adapters.get(adapterClass);
            
            if(adapter == null){
                //First time that we need the adapter
                if(adapterClass.isAnnotationPresent(BananaRamaAdapter.class)){
                    //Adapter has some dependencies
                    BananaRamaAdapter adapterAnnotation = adapterClass.getAnnotation(BananaRamaAdapter.class);
                    Constructor<T> constructor;
                    Object args[] =  new Object[adapterAnnotation.requires().length];
                    
                    try {//Get constructor that matches dependencies
                        constructor = adapterClass.getConstructor(adapterAnnotation.requires());
                        
                        //Fill in the dependencies
                        for( int i=0; i< args.length;i++)
                            args[i] = getAdapter(adapterAnnotation.requires()[i]);
                        
                        adapter = constructor.newInstance(args);
                        
                    } catch (NoSuchMethodException |
                            SecurityException  |
                            InstantiationException  |
                            IllegalAccessException |
                            InvocationTargetException ex) {
                        throw new IllegalStateException("Can't instantiate " + adapterClass.getName(),ex);
                    }
                }
                else{
                    try{
                        //Adapter has no dependencies
                        adapter = adapterClass.newInstance();
                    }
                    catch(InstantiationException | IllegalAccessException ex){
                        throw new IllegalStateException("Can't instantiate " + adapterClass.getName(),ex);
                    }
                }
                //Register adapter
                adapters.put(adapterClass, adapter);
            }
            
            return adapter;
        }
        finally{
            //Release lock anyway
            lock.unlock();
        }
    }
    
    public <T extends Adapter> T using(Class<T> clazz) {
        return getAdapter(clazz);
    }
    
    @Override
    public <T> CreateOperation <T> create(Class<T> clazz){
        return getAdapterForClass(clazz).create(clazz);
    }
    
    @Override
    public <T> ReadOperation <T> read(Class<T> clazz){
        return getAdapterForClass(clazz).read(clazz);
    }
    
    @Override
    public <T> UpdateOperation<T> update(Class<T> clazz){
        return getAdapterForClass(clazz).update(clazz);
    }
    
    @Override
    public <T> DeleteOperation<T> delete(Class<T> clazz){
        return getAdapterForClass(clazz).delete(clazz);
    }
    
}

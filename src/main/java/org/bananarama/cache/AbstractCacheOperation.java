/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bananarama.cache;

import com.googlecode.cqengine.IndexedCollection;
import org.bananarama.BananaRama;
import org.bananarama.annotation.Banana;
import org.bananarama.cache.annotation.BufferedOnIndexedCollection;
import org.bananarama.crud.Adapter;

/**
 *
 * @author Guglielmo De Concini
 */
public class AbstractCacheOperation<T> {
    
    protected final IndexedCollection<T> coll;
    protected final Class<T> clazz;
    protected final BananaRama root;
    
    
    public AbstractCacheOperation(IndexedCollection<T> coll,Class<T> clazz,BananaRama root){
        this.coll = coll;
        this.clazz = clazz;
        this.root = root;
    }
    
    @SuppressWarnings("unchecked")
    protected  static <T> Adapter<? super T> getBackingAdapter(Class<T> clazz,BananaRama root){
        //Class must be annotated, it must have been
        //checked before, otherwise it's a bug
        final BufferedOnIndexedCollection anno = clazz.getAnnotation(BufferedOnIndexedCollection.class);
        final Adapter<?> backingAdapter = root.using(anno.backingAdapter());
        
        if(  backingAdapter == null)
            throw new NullPointerException("The backing adapter for class " + clazz.getName() + " cannot be found. This should never happen");
        
        return (Adapter<? super T>)backingAdapter;
    }
    
    protected <T> Adapter<? super T> getBackingAdapter(Class<T> clazz){
        return getBackingAdapter(clazz, root);
    }
}

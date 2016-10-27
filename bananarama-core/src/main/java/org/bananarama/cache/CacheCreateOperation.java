/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bananarama.cache;

import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bananarama.BananaRama;
import org.bananarama.crud.CreateOperation;

/**
 *
 * @author Guglielmo De Concini
 */
public class CacheCreateOperation<T> extends AbstractCacheOperation<T> implements CreateOperation<T>{
             
    public CacheCreateOperation(IndexedCollection<T>coll,Class<T> clazz,BananaRama root){
        super(coll, clazz,root);
    }
    
    /**
     * Add elements to internal collection and send
     * them to underlying layer
     * @param data
     * @return
     */
    @Override
    public CreateOperation<T> from(Stream<T> data) {
        List<T> buf = data.collect(Collectors.toList());
        //Add elements to underlying layer
        getBackingAdapter(clazz).create(clazz).from(buf.stream());
        //And then to collection
        coll.addAll(buf);
        return this;
    }
    
    /**
     * Same as {@link #from(java.util.stream.Stream) but it also
     * passes the {@link QueryOptions to the underlying layer}
     * @param data
     * @param options
     * @return
     */
    @Override
    public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
        List<T> buf = data.collect(Collectors.toList());
        //Add elements to underlying layer
        getBackingAdapter(clazz).create(clazz).from(buf.stream(),options);
        //And then to collection
        coll.addAll(buf);
        return this;
    }
    
    @Override
    public void close() throws IOException {
        
    }

    
}

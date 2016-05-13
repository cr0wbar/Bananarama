/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package org.bananarama.cache;

import com.googlecode.cqengine.IndexedCollection;
import static com.googlecode.cqengine.query.QueryFactory.noQueryOptions;
import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bananarama.BananaRama;
import org.bananarama.crud.UpdateOperation;

/**
 *
 * @author Guglielmo De Concini
 */
public class CacheUpdateOperation<T> extends AbstractCacheOperation<T> implements UpdateOperation<T>{
    
    public CacheUpdateOperation(IndexedCollection<T>coll,Class<T> clazz,BananaRama root){
        super(coll, clazz,root);
    }
    /**
     * Replaces all the elements in the {@link Stream} in the
     * internal collection and updates them also
     * on the underlying layer
     * @param data
     * @return
     */
    @Override
    public UpdateOperation<T> from(Stream<T> data) {
        return from(data,noQueryOptions());
    }
    
    /**
     * Same as {@link #from(java.util.stream.Stream) but passes
     * the given {@link QueryOptions} to the underlying layer
     * @param data
     * @param options
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public UpdateOperation<T> from(Stream<T> data, QueryOptions options) {
        List<T> buf = data.collect(Collectors.toList());
        //Update elements in collection
        coll.removeAll(buf);
        coll.update(Collections.EMPTY_LIST, buf, options);
        
        //Update elements on underlying layer
        if(options != null)
            getBackingAdapter(clazz).update(clazz).from(buf.stream(),options);
        else
            getBackingAdapter(clazz).update(clazz).from(buf.stream());
        
        return this;
    }
    
    @Override
    public void close() throws IOException {
        
    }
}

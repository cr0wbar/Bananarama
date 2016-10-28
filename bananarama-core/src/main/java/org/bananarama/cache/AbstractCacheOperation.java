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
package org.bananarama.cache;

import com.googlecode.cqengine.IndexedCollection;
import org.bananarama.BananaRama;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bananarama.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author gdc
 */
public class ListDataCollector<T> implements DataCollector<T>{

    private final List<T> data;
    
    public ListDataCollector(){
        data = new ArrayList<>();
    }
    
    public ListDataCollector(final List<T> data){
        this.data = data;
    }
    
    @Override
    public void collect(Stream<T> dataIn) {
        dataIn.forEach(data::add);
    }

    @Override
    public Stream<T> dump() {
        return data.stream();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bananarama.io;

import java.util.stream.Stream;

/**
 *
 * @author gdc
 */
public interface DataCollector<T> {
    
    /**
     * Collect incoming data
     * @param dataIn
     * @return 
     */
    void collect(Stream<T> dataIn);
    
    /**
     * Dump collected data 
     * @return 
     */
    Stream<T> dump();
    
}

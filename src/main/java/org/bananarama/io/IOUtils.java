/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bananarama.io;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author gdc
 */
public class IOUtils {
    public static <T,S extends Collection<T>> DataCollector<T> andSaveInList(final List<T> output){
        return new ListDataCollector<>(output);
    }
}

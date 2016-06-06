/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bananarama.crud.noop;

import com.googlecode.cqengine.query.option.QueryOptions;
import java.io.IOException;
import java.util.stream.Stream;
import org.bananarama.crud.UpdateOperation;

/**
 * Update operation that actually does nothing
 * @author Guglielmo De Concini
 */
public class NoOpUpdateOperation<T> implements UpdateOperation<T>{

    @Override
    public NoOpUpdateOperation<T> from(Stream<T> data) {
        return this;
    }

    @Override
    public NoOpUpdateOperation<T> from(Stream<T> data, QueryOptions options) {
        return this;
    }

    @Override
    public void close() throws IOException {
       /**
        * Nothing to close
        */
    }
    
}

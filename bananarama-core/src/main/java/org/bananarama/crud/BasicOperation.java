/**
 * BasicOperation.java
 */
package org.bananarama.crud;

import java.io.IOException;


/**
 * @author simone.decristofaro
 * Apr 13, 2016
 */
public interface BasicOperation extends AutoCloseable{

    default void andReturn(){
        try {
            close();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void close() throws IOException;
    
}

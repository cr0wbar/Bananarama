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
package org.bananarama.concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Guglielmo De Concini
 */
public class StripedLock{
    
    //Provides simple, somewhat
    //fine-grained control
    //on concurrent access to objects
    private final ReentrantLock locks[];
    private final int concurrencyLevel;
    
    public StripedLock(int concurrencyLevel) {
        this.locks = new ReentrantLock[concurrencyLevel];
        this.concurrencyLevel = concurrencyLevel;
        
        for(int i=0;i<concurrencyLevel;i++)
            locks[i] = new ReentrantLock();
        
    }
    
   public ReentrantLock getLock(Object obj){
       return locks[obj.hashCode() % concurrencyLevel];
   }
    
    public void lockAll(){
        for(ReentrantLock lock: locks)
            lock.lock();
    }
    
    public void unlockAll(){
        for(ReentrantLock lock: locks)
            lock.unlock();
    }
}

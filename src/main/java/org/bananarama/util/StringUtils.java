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
package org.bananarama.util;

import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 *

 * @author Guglielmo De Concini
 */
public class StringUtils {
    
    public static String getNMarkerSeparatedChars(char c,char del,int n){
        char buf[] = new char[2*n-1];
        char pattern[] = {c,del};
        
        for(int i=0;i<buf.length;i++)
            buf[i] = pattern[i%2];
        
        return new String(buf);
    }

    public static String getNMarkerSeparatedChars(String c,String del,int n){
        char buf[] = new char[n*c.length() + (n-1)*del.length()];
        final int patternLen = c.length() + del.length();
        char pattern[] = new char[patternLen];
        
        System.arraycopy(c.toCharArray(), 0, pattern, 0, c.length());
        System.arraycopy(del.toCharArray(), 0, pattern, c.length(), del.length());
        
        for(int i=0;i<buf.length;i++)
            buf[i] = pattern[i%patternLen];
        
        return new String(buf);
    }
    
    public static String mkString(Stream<String> items,String prefix,String delimiter,String suffix){
        StringJoiner joiner = new StringJoiner(delimiter);
        items.forEach(joiner::add);
        return prefix + joiner.toString() + suffix;
    }
    
}

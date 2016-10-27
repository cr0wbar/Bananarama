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
package org.bananarama.crud.sql;

/**
 *
 * @author Guglielmo De Concini
 */
public class SqlOperationOptions {
    
    private final String tableName;
    private final boolean batchModeActive;
    private final Integer limit;
    
    private static final String DEFAULT_TABLENAME = null;
    private static final boolean  DEFAULT_BATCHMODEACTIVE = true;
    private static final Integer DEFAULT_LIMIT = null;
    
    public SqlOperationOptions(String tableName,boolean batchmodeactive,Integer limit){
        this.tableName = tableName;
        this.batchModeActive = batchmodeactive;
        this.limit = limit;
    }
    
    public String getTableName() {
        return tableName;
    }

    public boolean isBatchModeActive() {
        return batchModeActive;
    }

    public Integer getLimit() {
        return limit;
    }

    //Util methods
    public static final SqlOperationOptions BATCHMODE_DISABLE = new SqlOperationOptions(DEFAULT_TABLENAME, false, DEFAULT_LIMIT);
    public static  SqlOperationOptions limit(int limit){
        return new SqlOperationOptions(DEFAULT_TABLENAME, DEFAULT_BATCHMODEACTIVE, limit);
    }
    public static  SqlOperationOptions tableName(String tableName){
        return new SqlOperationOptions(tableName, DEFAULT_BATCHMODEACTIVE, DEFAULT_LIMIT);
    }    
}

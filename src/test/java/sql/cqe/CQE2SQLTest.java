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
package sql.cqe;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.Query;
import static com.googlecode.cqengine.query.QueryFactory.*;
import com.googlecode.cqengine.query.option.QueryOptions;
import org.bananarama.crud.sql.SqlOperationOptions;
import org.bananarama.crud.util.cqlogic.CQE2SQL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Guglielmo De Concini
 */
public class CQE2SQLTest {
    
    public CQE2SQLTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private static class UmpaLumpa{
        private final int id;
        private final String label;
        
        protected UmpaLumpa(int id,String label){
            this.id = id;
            this.label = label;
        }
        
        public static final Attribute<UmpaLumpa, Integer> ID = new SimpleAttribute<UmpaLumpa, Integer>("id") {
            
            @Override
            public Integer getValue(UmpaLumpa obj, QueryOptions queryOptions) {
                
                return obj.id;
            }
        };    
        
        public static final Attribute<UmpaLumpa, String> LABEL = new SimpleAttribute<UmpaLumpa, String>("label") {
            
            @Override
            public String getValue(UmpaLumpa obj, QueryOptions queryOptions) {
                
                return obj.label;
            }
        };
        
    }
    
    @Test
    public void testOrderBy() {
        Query<UmpaLumpa> query = equal(UmpaLumpa.LABEL, "uno");
        String whereClause = CQE2SQL.convertCqQuery(query,queryOptions(orderBy(ascending(UmpaLumpa.ID))));
        
        assertEquals(" WHERE label = 'uno' ORDER BY id", whereClause);
        
        whereClause = CQE2SQL.convertCqQuery(query,queryOptions(orderBy(descending(UmpaLumpa.ID))));
        assertEquals(" WHERE label = 'uno' ORDER BY id DESC",whereClause);
        
        whereClause = CQE2SQL.convertCqQuery(null,queryOptions(orderBy(descending(UmpaLumpa.ID))));
        assertEquals(whereClause, " ORDER BY id DESC",whereClause);
    }
    
    @Test
    public void testLimit() {
        Query<UmpaLumpa> query = equal(UmpaLumpa.LABEL, "uno");
        
        
        String whereClause = CQE2SQL.convertCqQuery(query,queryOptions(SqlOperationOptions.limit(100)));
        assertEquals(whereClause, " WHERE label = 'uno' LIMIT 100");
        
        whereClause = CQE2SQL.convertCqQuery(query,queryOptions(SqlOperationOptions.limit(100),orderBy(descending(UmpaLumpa.ID))));
        assertEquals(whereClause, " WHERE label = 'uno' ORDER BY id DESC LIMIT 100");
        
        whereClause = CQE2SQL.convertCqQuery(null,queryOptions(SqlOperationOptions.limit(100),orderBy(descending(UmpaLumpa.ID))));
        assertEquals(whereClause, " ORDER BY id DESC LIMIT 100");
        
    }
}

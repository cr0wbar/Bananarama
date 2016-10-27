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

import org.bananarama.crud.Adapter;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Guglielmo De Concini
 */
public abstract class SqlAdapter implements Adapter<Object> {

    private static final Logger log = LoggerFactory.getLogger(SqlAdapter.class);
    protected final DataSource dataSource;

    public SqlAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> SqlCreateOperation<T> create(Class<T> clazz) {

        log.debug("SQL Create for class: " + clazz.getName());
        return new SqlCreateOperation<>(dataSource, clazz);
    }

    @Override
    public <T> SqlReadOperation<T> read(Class<T> clazz) {

        log.debug("SQL Read for class: " + clazz.getName());
        return new SqlReadOperation<>(dataSource, clazz);
    }

    @Override
    public <T> SqlUpdateOperation<T> update(Class<T> clazz) {

        log.debug("SQL Update for class: " + clazz.getName());
        return new SqlUpdateOperation<>(dataSource, clazz);
    }

    @Override
    public <T> SqlDeleteOperation<T> delete(Class<T> clazz) {

        log.debug("SQL Delete for class: " + clazz.getName());
        return new SqlDeleteOperation<>(dataSource, clazz);
    }

}

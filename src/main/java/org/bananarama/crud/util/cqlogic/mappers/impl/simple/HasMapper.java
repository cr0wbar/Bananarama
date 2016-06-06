/**
 * HasMapper.java
 */
package org.bananarama.crud.util.cqlogic.mappers.impl.simple;

import org.bananarama.crud.util.cqlogic.mappers.Mapper;

import com.googlecode.cqengine.query.simple.Has;


/**
 * @author simone.decristofaro
 * Apr 28, 2016
 */
@SuppressWarnings("rawtypes")
public class HasMapper implements Mapper<Has<?,?>> {

    /**
    * {@inheritDoc}
    */
    @Override
    public String map(Has q) {
        return String.format("%s is not null",q.getAttributeName());
    }

}

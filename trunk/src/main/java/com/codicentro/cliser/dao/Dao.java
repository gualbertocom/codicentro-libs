/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codicentro.cliser.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;

/**
 *
 * @author avillalobos
 */
public interface Dao {

    /**
     *
     * @param <T>
     * @param entity
     * @return
     */
    public <T> T persist(T entity);

    /**
     *
     * @param <T>
     * @param entity
     * @return
     */
    public <T> void delete(T entity);

    /**
     *
     * @param entities
     */
    public void persist(Object[] entities);

    /**
     * 
     * @param <T>
     * @param entityClass
     * @return
     */
    public <T> List<T> find(Class<T> entityClass);

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     */
    public <T> T load(Class<T> entityClass, Serializable id);

    /**
     *
     * @param <T>
     * @param entityClass
     * @param id
     * @return
     */
    public <T> T get(Class<T> entityClass, Serializable id);

    /**
     *
     * @param <T>
     * @param hql
     * @return
     */
    public <T> List<T> find(String hql);

    /**
     *
     * @param <T>
     * @param criteria
     * @return
     */
    public <T> List<T> find(DetachedCriteria criteria);

    /**
     *
     * @param <T>
     * @param criteria
     * @param start
     * @param limit
     * @return
     */
    public <T> List<T> find(DetachedCriteria criteria, Integer start, Integer limit);
}

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
public interface CliserDao {

    /**
     *
     * @param <TEntity>
     * @param entity
     * @return
     */
    public <TEntity> TEntity persist(TEntity entity);

    /**
     *
     * @param <TEntity>
     * @param entity
     * @return
     */
    public <TEntity> void delete(TEntity entity);

    /**
     *
     * @param entities
     */
    public void persist(Object[] entities);

    /**
     * 
     * @param <TEntity>
     * @param entityClass
     * @return
     */
    public <TEntity> List<TEntity> find(Class<TEntity> entityClass);

    /**
     *
     * @param <TEntity>
     * @param entityClass
     * @param id
     * @return
     */
    public <TEntity> TEntity load(Class<TEntity> entityClass, Serializable id);

    /**
     *
     * @param <TEntity>
     * @param entityClass
     * @param id
     * @return
     */
    public <TEntity> TEntity get(Class<TEntity> entityClass, Serializable id);

    /**
     *
     * @param <TEntity>
     * @param hql
     * @return
     */
    public <TEntity> List<TEntity> find(String hql);

    public <TEntity> List<TEntity> find(StringBuilder sql);

    /**
     *
     * @param <TEntity>
     * @param criteria
     * @return
     */
    public <TEntity> List<TEntity> find(DetachedCriteria criteria);

    /**
     *
     * @param <TEntity>
     * @param criteria
     * @param start
     * @param limit
     * @return
     */
    public <TEntity> List<TEntity> find(DetachedCriteria criteria, Integer start, Integer limit);

    /**
     * 
     * @param <TEntity>
     * @param entity
     * @param start
     * @param limit
     * @return
     */
    public <TEntity> List<TEntity> find(TEntity entity, Integer start, Integer limit);
}

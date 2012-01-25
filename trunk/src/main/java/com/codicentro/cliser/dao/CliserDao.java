/**
 * Author: Alexander Villalobos Yadró E-Mail: avyadro@yahoo.com.mx Created on
 * 03/08/2010, 10:52:52 AM Place: Toluca, Estado de Mexico, Mexico. Company:
 * Codicentro Web: http://www.codicentro.com Class Name: CliserDao.java Purpose:
 * Revisions: Ver Date Author Description --------- ---------------
 * ----------------------------------- ------------------------------------
 *
 */
package com.codicentro.cliser.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.DetachedCriteria;

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
    public <TEntity> void delete(final TEntity entity);

    /**
     *
     * @param <TEntity>
     * @param entity
     */
    public <TEntity> void delete(final TEntity[] entity);

    /**
     *
     * @param entities
     */
    public <TEntity> void persist(TEntity[] entities);

    /**
     *
     * @param <TEntity>
     * @param entityClass
     * @return
     */
    public <TEntity> List<TEntity> find(final Class<TEntity> entityClass);

    /**
     *
     * @param <TEntity>
     * @param entityClass
     * @param id
     * @return
     */
    public <TEntity> TEntity load(final Class<TEntity> entityClass, final Serializable id);

    /**
     *
     * @param <TEntity>
     * @param entityClass
     * @param id
     * @return
     */
    public <TEntity> TEntity get(final Class<TEntity> entityClass, final Serializable id);

    /**
     *
     * @param <TEntity>
     * @param hql
     * @return
     */
    public <TEntity> List<TEntity> find(final String hql);

    /**
     *
     * @param <TEntity>
     * @param hql
     * @param values
     * @return
     */
    public <TEntity> List<TEntity> find(final String hql, final Object... values);

    /**
     * 
     */
    public List<?> find(final StringBuilder sql);

    /**
     *
     * @param <TEntity>
     * @param eClazz
     * @param sql
     * @return
     */
    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final String sql);

    /**
     *
     */
    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final String sql, final Object[] params);

    /**
     *
     * @param <TEntity>
     * @param criteria
     * @return
     */
    public <TEntity> List<TEntity> find(final DetachedCriteria criteria);

    /**
     *
     * @param <TEntity>
     * @param criteria
     * @param start
     * @param limit
     * @return
     */
    public <TEntity> List<TEntity> find(final DetachedCriteria criteria, final Integer start, final Integer limit);

    /**
     *
     * @param <TEntity>
     * @param entity
     * @param start
     * @param limit
     * @return
     */
    public <TEntity> List<TEntity> find(final TEntity entity, final Integer start, final Integer limit);

    /**
     *
     * @param <TEntity>
     * @param queryName
     * @param values
     * @return
     */
    public <TEntity> List<TEntity> findByQueryName(final String queryName, final Map<String, Object> values);

    /**
     *
     * @return
     */
    public org.hibernate.Session getHBSession();

    /**
     *
     */
    public int execute(final StringBuilder sql);

    public int execute(final StringBuilder sql, Object value);

    public int execute(final StringBuilder sql, Object... values);
}

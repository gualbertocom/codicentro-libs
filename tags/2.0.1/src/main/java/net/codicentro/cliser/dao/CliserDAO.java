/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 03/08/2010, 10:52:52 AM
 * Place: Toluca, Estado de Mexico, Mexico.
 * Company: Codicentro©
 * Web: http://www.codicentro.net
 * Class Name: CliserDAO.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.cliser.dao;

import net.codicentro.utils.Scalar;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.DetachedCriteria;

public interface CliserDAO {

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

    public <TEntity> void persist(Collection<TEntity> entities);

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
     * @param sql
     * @return
     */
    public List<?> find(final StringBuilder sql);

    public List<?> find(final StringBuilder sql, final Scalar[] scalars);

    /**
     *
     * @param sql
     * @param params
     * @return
     */
    public List<?> find(final StringBuilder sql, final Object[] params);

    public List<?> find(final StringBuilder sql, final Object[] params, final Scalar[] scalars);

    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final StringBuilder sql);

    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final StringBuilder sql, final Object... params);

    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final StringBuilder sql, final Object[] params, final Scalar[] scalars);

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

    public <TEntity> List<TEntity> find(final TEntity entity);

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
     * @param hql
     * @return
     */
    public int execute(final String hql);

    /**
     *
     * @param hql
     * @param value
     * @return
     */
    public int execute(final String hql, final Object value);

    /**
     *
     * @param hql
     * @param values
     * @return
     */
    public int execute(final String hql, final Object... values);

    /**
     *
     * @param sql
     * @return
     */
    public int execute(final StringBuilder sql);

    /**
     *
     * @param sql
     * @param param
     * @return
     */
    public int execute(final StringBuilder sql, final Object param);

    /**
     *
     * @param sql
     * @param params
     * @return
     */
    public int execute(final StringBuilder sql, final Object... params);
}

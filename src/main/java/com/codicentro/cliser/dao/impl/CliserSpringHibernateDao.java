/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 03/08/2010, 10:52:52 AM
 * Place: Toluca, Estado de Mexico, Mexico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: CliserSpringHibernateDao.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.cliser.dao.impl;

import com.codicentro.cliser.dao.CliserDao;
import com.codicentro.utils.Scalar;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public abstract class CliserSpringHibernateDao extends HibernateDaoSupport implements CliserDao {

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> void delete(final TEntity entity) {
        getHibernateTemplate().delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> void delete(final TEntity[] entities) {
        for (TEntity entity : entities) {
            delete(entity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> TEntity persist(TEntity entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> void persist(TEntity[] entities) {
        TEntity entity = null;
        try {
            for (int idx = 0; idx < entities.length; idx++) {
                entity = entities[idx];
                persist(entity);
            }
        } catch (Exception ex) {
            throw new RuntimeException(entity.toString(), ex);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(final Class<TEntity> entityClass) {
        return getHibernateTemplate().loadAll(entityClass);
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> TEntity load(final Class<TEntity> entityClass, final Serializable id) {
        return getHibernateTemplate().load(entityClass, id);
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> TEntity get(final Class<TEntity> entityClass, final Serializable id) {
        return getHibernateTemplate().get(entityClass, id);
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(final String hql) {
        final List<TEntity> entities = getHibernateTemplate().find(hql);
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(final DetachedCriteria criteria, final Integer start, final Integer limit) {
        return getHibernateTemplate().findByCriteria(criteria, start, limit);
    }

    @Override
    public <TEntity> List<TEntity> find(final DetachedCriteria criteria) {
        return find(criteria, -1, -1);
    }

    @Override
    public <TEntity> List<TEntity> find(final TEntity entity, final Integer start, final Integer limit) {
        return getHibernateTemplate().findByExample(entity, start, limit);
    }

    @Override
    public <TEntity> List<TEntity> find(final TEntity entity) {
        return getHibernateTemplate().findByExample(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(final String hql, final Object... values) {
        return getHibernateTemplate().find(hql, values);
    }

    @Override
    public List<?> find(final StringBuilder sql) {
        return find(null, sql.toString(), null);
    }

    @Override
    public List<?> find(final StringBuilder sql, final Scalar[] scalars) {
        return find(null, sql.toString(), null, scalars);
    }

    @Override
    public List<?> find(final StringBuilder sql, final Object[] params) {
        return find(null, sql.toString(), params, null);
    }

    @Override
    public List<?> find(final StringBuilder sql, final Object[] params, final Scalar[] scalars) {
        return find(null, sql.toString(), params, scalars);
    }

    @Override
    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final String sql) {
        return find(eClazz, sql, null);
    }

    @Override
    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final String sql, final Object[] params) {
        return find(eClazz, sql, params, null);
    }

    @Override
    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final String sql, final Object[] params, final Scalar[] scalars) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<TEntity>>() {

            @Override
            public List<TEntity> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                if (eClazz != null) {
                    query.addEntity(eClazz);
                }
                if (params != null && params.length > 0) {
                    for (int idx = 0; idx < params.length; idx++) {
                        query.setParameter(idx, params[idx]);
                    }
                }
                if (scalars != null) {
                    for (Scalar scalar : scalars) {
                        if (scalar.getType() != null) {
                            query.addScalar(scalar.getAlias(), scalar.getType());
                        } else {
                            query.addScalar(scalar.getAlias());
                        }
                    }
                }
                return query.list();
            }
        });
    }

    @Override
    public <TEntity> List<TEntity> findByQueryName(final String queryName, final Map<String, Object> values) {
        return getHibernateTemplate().execute(new HibernateCallback<List<TEntity>>() {

            @Override
            public List<TEntity> doInHibernate(final Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(queryName);
                for (String key : values.keySet()) {
                    query.setParameter(key, values.get(key));
                }
                return query.list();
            }
        });
    }

    @Override
    public int execute(String hql) {
        return getHibernateTemplate().bulkUpdate(hql);
    }

    @Override
    public int execute(String hql, Object value) {
        return getHibernateTemplate().bulkUpdate(hql, value);
    }

    @Override
    public int execute(String hql, Object... values) {
        return getHibernateTemplate().bulkUpdate(hql, values);
    }

    @Override
    public int execute(final StringBuilder sql) {
        return execute(sql, new Object[]{});
    }

    @Override
    public int execute(final StringBuilder sql, final Object param) {
        return execute(sql, new Object[]{param});
    }

    @Override
    public int execute(final StringBuilder sql, final Object... params) {

        return getHibernateTemplate().execute(new HibernateCallback<Integer>() {

            @Override
            public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql.toString());
                if (params != null && params.length > 0) {
                    for (int idx = 0; idx < params.length; idx++) {
                        query.setParameter(idx, params[idx]);
                    }
                }
                return query.executeUpdate();
            }
        });

    }

    /**
     *
     * @return @deprecated Este método se debe de utilizar con mucho cuidado por
     * el tema de las conexiones.
     */
    @Override
    public Session getHBSession() {
        return getSession();
    }
}
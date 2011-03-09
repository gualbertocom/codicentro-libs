/**
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
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.Resource;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CliserSpringHibernateDao extends HibernateDaoSupport implements CliserDao {

    //  private Logger logger = LoggerFactory.getLogger(CliserSpringHibernateDao.class);
    @Resource //(name = "hibernateTemplate")
    public void setTemplate(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> void delete(final TEntity entity) {
        getHibernateTemplate().delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> TEntity persist(final TEntity entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void persist(final Object[] entities) {
        for (int i = 0; i < entities.length; i++) {
            persist(entities[i]);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(final Class<TEntity> entityClass) {
        final List<TEntity> entities = getHibernateTemplate().loadAll(entityClass);
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> TEntity load(final Class<TEntity> entityClass, final Serializable id) {
        final TEntity entity = (TEntity) getHibernateTemplate().load(entityClass, id);
        return entity;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> TEntity get(final Class<TEntity> entityClass, final Serializable id) {
        final TEntity entity = (TEntity) getHibernateTemplate().get(entityClass, id);
        return entity;
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
        final List<TEntity> entities = getHibernateTemplate().findByCriteria(criteria, start, limit);
        return entities;
    }

    @Override
    public <TEntity> List<TEntity> find(final DetachedCriteria criteria) {
        return find(criteria, -1, -1);
    }

    @Override
    public <TEntity> List<TEntity> find(final TEntity entity, final Integer start, final Integer limit) {
        final List<TEntity> entities = getHibernateTemplate().findByExample(entity, start, limit);
        return entities;
    }

    @Override
    public <TEntity> List<TEntity> find(final StringBuilder sql) {
        Session session = getSessionFactory().openSession();
        SQLQuery query = session.createSQLQuery(sql.toString());
        final List<TEntity> entities = query.list();
        session.close();
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(final String hql, final Object... values) {
        final List<TEntity> entities = getHibernateTemplate().find(hql, values);
        return entities;
    }

    @Override
    public <TEntity> List<TEntity> find(final Class<TEntity> eClazz, final String sql) {
       return getHibernateTemplate().executeFind(new HibernateCallback<TEntity>() {

            @Override
            public TEntity doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.addEntity(eClazz);
                return (TEntity) query.list();
            }
        });
    }
}

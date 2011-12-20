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
package com.codicentro.cliser.dao;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CliserSpringHibernateDao extends HibernateDaoSupport implements CliserDao {

    private Logger log = org.slf4j.LoggerFactory.getLogger(CliserSpringHibernateDao.class);

    @Resource(name = "hibernateTemplate")
    public void setTemplate(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> void delete(TEntity entity) {
        getHibernateTemplate().delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <TEntity> TEntity persist(TEntity entity) {
        getHibernateTemplate().saveOrUpdate(entity);
        return entity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void persist(Object[] entities) {
        for (int i = 0; i < entities.length; i++) {
            persist(entities[i]);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(Class<TEntity> entityClass) {
        final List<TEntity> entities = getHibernateTemplate().loadAll(entityClass);
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> TEntity load(Class<TEntity> entityClass, Serializable id) {
        final TEntity entity = (TEntity) getHibernateTemplate().load(entityClass, id);
        return entity;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> TEntity get(Class<TEntity> entityClass, Serializable id) {
        final TEntity entity = (TEntity) getHibernateTemplate().get(entityClass, id);
        return entity;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(String hql) {
        final List<TEntity> entities = getHibernateTemplate().find(hql);
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <TEntity> List<TEntity> find(DetachedCriteria criteria, Integer start, Integer limit) {
        final List<TEntity> entities = getHibernateTemplate().findByCriteria(criteria, start, limit);
        return entities;
    }

    @Override
    public <TEntity> List<TEntity> find(DetachedCriteria criteria) {
        return find(criteria, -1, -1);
    }

    @Override
    public <TEntity> List<TEntity> find(TEntity entity, Integer start, Integer limit) {
        final List<TEntity> entities = getHibernateTemplate().findByExample(entity, start, limit);
        return entities;
    }

    @Override
    public <TEntity> List<TEntity> find(StringBuilder sql) {
        Session session = getSessionFactory().openSession();
        SQLQuery query = session.createSQLQuery(sql.toString());
        final List<TEntity> entities = query.list();
        session.close();
        return entities;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codicentro.cliser.dao;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author avillalobos
 */
@Repository
public class SpringHibernateDao extends HibernateDaoSupport implements Dao {

    private Logger log = org.slf4j.LoggerFactory.getLogger(SpringHibernateDao.class);

    @Resource(name = "hibernateTemplate")
    public void setTemplate(HibernateTemplate hibernateTemplate) {
        setHibernateTemplate(hibernateTemplate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <T> void delete(T entity) {
        getHibernateTemplate().delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public <T> T persist(T entity) {
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
    public <T> List<T> find(Class<T> entityClass) {
        final List<T> entities = getHibernateTemplate().loadAll(entityClass);
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <T> T load(Class<T> entityClass, Serializable id) {
        final T entity = (T) getHibernateTemplate().load(entityClass, id);
        return entity;
    }

    @Transactional(readOnly = true)
    @Override
    public <T> T get(Class<T> entityClass, Serializable id) {
        final T entity = (T) getHibernateTemplate().get(entityClass, id);
        return entity;
    }

    @Transactional(readOnly = true)
    @Override
    public <T> List<T> find(String hql) {
        final List<T> entities = getHibernateTemplate().find(hql);
        return entities;
    }

    @Transactional(readOnly = true)
    @Override
    public <T> List<T> find(DetachedCriteria criteria, Integer start, Integer limit) {
        final List<T> entities = getHibernateTemplate().findByCriteria(criteria, start, limit);
        return entities;
    }

    @Override
    public <T> List<T> find(DetachedCriteria criteria) {
        return find(criteria, -1, -1);
    }

    @Override
    public <T> List<T> find(T entity, Integer start, Integer limit) {
        final List<T> entities = getHibernateTemplate().findByExample(entity, start, limit);
        return entities;
    }
}

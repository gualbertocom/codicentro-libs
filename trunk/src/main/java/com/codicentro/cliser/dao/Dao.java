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

    public <T> T persist(T entity);

    public void persist(Object[] entities);

    public <T> List<T> find(Class<T> entityClass);

    public <T> T load(Class<T> entityClass, Serializable id);

    public <T> T get(Class<T> entityClass, Serializable id);

    public <T> List<T> find(String hql);

    public <T> List<T> find(DetachedCriteria criteria);

    public <T> List<T> find(DetachedCriteria criteria, Integer start, Integer limit);
}

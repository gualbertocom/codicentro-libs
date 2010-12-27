/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 20, 2009, 05:37:24 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: BL.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.cliser;

import com.codicentro.cliser.dao.CliserDao;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import com.codicentro.utils.Types.DBProtocolType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.util.SerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.WebApplicationContext;

@Service
public class BL implements Serializable {

    private Logger log = LoggerFactory.getLogger(BL.class);
    private RequestWrapper requestWrapper = null;
    private ResponseWrapper responseWrapper = null;
    private DBProtocolType dbProtocol = null;
    private String dbVersion = null;
    private DetachedCriteria criteria = null;
    private ProjectionList projections = null;
    private String rowCountUniqueProperty = null;
    /*** Identifier IU ***/
    private String nameIU = null;
    private Object IU = null;
    //private String
    //private Object sessionEntity = null;
    /*** SERVICE ***/
    private Class srvClazz = null;
    private String srvName = null;
    private String srvMethodName = null;
    private List<Object> srvParams = null;
    private List<Class> srvParamTypes = null;
    private String sessionName = null;
    private WebApplicationContext wac = null;
    /*** ENTITY ***/
    private Class eClazz = null;
    private String eClazzAlia = null;
    private Object oEntity = null;
    private String dateFormat = null;
    @Resource
    private CliserDao dao;

    /**
     *
     * @throws CDCException
     */
    public <TSessionEntity> void checkSession() throws CDCException {
        if ((requestWrapper.getSession() == null) || (requestWrapper.getSession().getAttribute(sessionName) == null)) {
            throw new CDCException("lng.msg.error.sessionexpired");
        }
        IU = invoke(session(), nameIU, null);
    }

    public <TSessionEntity> TSessionEntity session() {
        return (TSessionEntity) requestWrapper.getSession().getAttribute(sessionName);
    }

    public Object session(String propertyName) throws CDCException {
        return invoke(requestWrapper.getSession().getAttribute(sessionName), propertyName);
    }

    /**
     *
     * @return
     */
    public Object getIU() {
        return IU;
    }

    /**
     *
     * @param <TEntity>
     * @param eClazz
     */
    public <TEntity> void entity(Class<TEntity> eClazz) {
        this.eClazz = eClazz;
        criteria = null;
    }

    /**
     * 
     * @param <TEntity>
     * @param eClazz
     * @param eClazzAlia
     */
    public <TEntity> void entity(Class<TEntity> eClazz, String eClazzAlia) {
        entity(eClazz);
        this.eClazzAlia = eClazzAlia;
    }

    /**
     * 
     * @param <TBean>
     * @param srvClazz
     */
    public <TBean> void service(Class<TBean> srvClazz) {
        this.srvClazz = srvClazz;
    }

    public <TBean> void service(String srvMethodName, Class<TBean> srvClazz) {
        this.srvClazz = srvClazz;
        this.srvMethodName = srvMethodName;
    }

    public <TBean> void service(Class<TBean> srvClazz, String srvName) {
        this.srvClazz = srvClazz;
        this.srvName = srvName;
    }

    public <TBean> void service(String srvName) {
        this.srvName = srvName;
    }

    /**
     * 
     * @param <TBean>
     * @param <TEntity>
     * @param srvClazz
     * @param eClazz
     * @param eClazzAlia
     */
    public <TBean, TEntity> void service(Class<TBean> srvClazz, Class<TEntity> eClazz, String eClazzAlia) {
        this.srvClazz = srvClazz;
        entity(eClazz, eClazzAlia);
    }

    /**
     * 
     * @param <TEntity>
     * @param eClazz
     * @param idClass
     * @param id
     * @return
     * @throws CDCException
     */
    public <TEntity> TEntity instance(Class<TEntity> eClazz, Class idClass, Serializable id) throws CDCException {
        TEntity entity = (id == null) ? null : dao.get(eClazz, id);
        if (entity != null) {
            return entity;
        } else {
            try {
                return eClazz.getConstructor(idClass).newInstance(id);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new CDCException(ex);
            }
        }
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param paramName
     * @throws CDCException
     * @deprecated
     */
    public void EQ(String propertyName, String paramName) throws CDCException {
        EQ(propertyName, paramName, false);
    }

    /**
     * Apply case-insensitive an "equal" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param ignoreCase
     */
    public void EQ(String propertyName, String paramName, boolean ignoreCase) throws CDCException {
        if (!TypeCast.isNullOrEmpy(paramName)) {
            EQ(ignoreCase, propertyName, form(paramName));
        }
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param value
     * @throws CDCException     
     */
    public void EQ(String propertyName, Object value) throws CDCException {
        EQ(false, propertyName, value);
    }

    /**
     * Apply case-insensitive an "equal" constraint to the named property when ignoreCase is true
     * @param ignoreCase
     * @param propertyName
     * @param value
     * @throws CDCException
     */
    public void EQ(boolean ignoreCase, String propertyName, Object value) throws CDCException {
        if (value != null) {
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            try {
                if (ignoreCase) {
                    criteria.add(Restrictions.eq(propertyName, value).ignoreCase());
                } else {
                    criteria.add(Restrictions.eq(propertyName, value));
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new CDCException(ex);
            }
        }
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param ignoreCase
     * @param propertyName
     * @param otherPropertyName
     * @throws CDCException
     */
    public void EQ(boolean ignoreCase, String propertyName, String otherPropertyName) throws CDCException {

        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        try {
            if (ignoreCase) {
                criteria.add(Restrictions.eq(propertyName, otherPropertyName).ignoreCase());
            } else {
                criteria.add(Restrictions.eq(propertyName, otherPropertyName));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }

    }

    public void SQL(String sql) throws CDCException {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        try {
            criteria.add(Restrictions.sqlRestriction(sql));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }

    }

    /**
     * Apply a "not equal" constraint to the named property
     * @param ignoreCase
     * @param propertyName
     * @param value
     * @throws CDCException
     */
    public void NE(boolean ignoreCase, String propertyName, Object value) throws CDCException {
        if (value != null) {
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            try {
                if (ignoreCase) {
                    criteria.add(Restrictions.ne(propertyName, value).ignoreCase());
                } else {
                    criteria.add(Restrictions.ne(propertyName, value));
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new CDCException(ex);
            }
        }
    }

    /**
     * Apply case-insensitive an "not equal" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param ignoreCase
     */
    public void NE(String propertyName, String paramName, boolean ignoreCase) throws CDCException {
        if (!TypeCast.isNullOrEmpy(paramName)) {
            NE(ignoreCase, propertyName, form(paramName));
        }
    }

    /**
     * Apply an "like" constraint to the named property
     * @param propertyName
     * @param paramName
     * @param define, define like conditions, Values[?%||%?||%?%]
     */
    public void LK(String propertyName, String paramName, String define) throws CDCException {
        LK(propertyName, paramName, define, false);
    }

    /**
     * Apply case-insensitive an "like" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param define, define like conditions, Values[?%||%?||%?%]
     * @param ignoreCase
     */
    public void LK(String propertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
        if (paramName != null) {
            String param = paramString(paramName);
            if (TypeCast.isNullOrEmpy(param)) {
                return;
            }
            if (TypeCast.isNullOrEmpy(define)
                    || ((define.indexOf("?%") == -1)
                    && (define.indexOf("%?") == -1)
                    && (define.indexOf("%?%") == -1))) {
                throw new CDCException("cliser.msg.error.criteria.like.baddefined");
            } else {
                param = define.replaceAll("\\?", param);
            }
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            if (ignoreCase) {
                criteria.add(Restrictions.like(propertyName, param).ignoreCase());
            } else {
                criteria.add(Restrictions.like(propertyName, param));
            }
        }
    }

    /**
     * Apply case-insensitive an "like or" constraint to the named property when ignoreCase is true
     * @param lhsPropertyName
     * @param rhsPropertyName
     * @param paramName
     * @param define
     * @param ignoreCase
     * @throws CDCException
     */
    public void LKo(String lhsPropertyName, String rhsPropertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
        if (paramName != null) {
            String param = paramString(paramName);
            if (TypeCast.isNullOrEmpy(param)) {
                return;
            }
            if (TypeCast.isNullOrEmpy(define)
                    || ((define.indexOf("?%") == -1)
                    && (define.indexOf("%?") == -1)
                    && (define.indexOf("%?%") == -1))) {
                throw new CDCException("cliser.msg.error.criteria.like.baddefined");
            } else {
                param = define.replaceAll("\\?", param);
            }
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(eClazz);
            }
            if (ignoreCase) {
                criteria.add(Restrictions.or(Restrictions.like(lhsPropertyName, param).ignoreCase(), Restrictions.like(rhsPropertyName, param).ignoreCase()));
            } else {
                criteria.add(Restrictions.or(Restrictions.like(lhsPropertyName, param), Restrictions.like(rhsPropertyName, param)));
            }
        }
    }

    public void LK(String propertyNameJoin, String propertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
        if (paramName != null) {
            String param = paramString(paramName);
            if (TypeCast.isNullOrEmpy(param)) {
                return;
            }
            if (TypeCast.isNullOrEmpy(define)
                    || ((define.indexOf("?%") == -1)
                    && (define.indexOf("%?") == -1)
                    && (define.indexOf("%?%") == -1))) {
                throw new CDCException("cliser.msg.error.criteria.like.baddefined");
            } else {
                param = define.replaceAll("\\?", param);
            }
            if (criteria == null) {
                criteria = DetachedCriteria.forClass(this.eClazz);
            }
            if (ignoreCase) {
                criteria.createCriteria(propertyNameJoin).add(Restrictions.like(propertyName, param).ignoreCase());
            } else {
                criteria.createCriteria(propertyNameJoin).add(Restrictions.like(propertyName, param));
            }
        }
    }

    /**
     * Specifies joining to an entity based on a left outer join.
     * @param associationPath
     * @param alias
     */
    public <TEntity> void LJN(Class<TEntity> eClazz, String propertyName) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(this.eClazz);
        }



    }

    /**
     * Specifies joining to an entity based on a full join.
     * @param associationPath
     * @param alias
     */
    public void FJN(String associationPath, String alias) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(this.eClazz);
        }
        criteria.createCriteria(associationPath, alias, DetachedCriteria.FULL_JOIN);
    }

    /**
     * Specifies joining to an entity based on an inner join.
     * @param associationPath
     * @param alias
     */
    public void IJN(String associationPath, String alias) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(this.eClazz);
        }
        criteria.createCriteria(associationPath, alias, DetachedCriteria.INNER_JOIN);
    }

    public void IN(String propertyName, Object[] values) throws CDCException {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        try {
            criteria.add(Restrictions.in(propertyName, values));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }

    }

    public void IN(String propertyName, Collection values) throws CDCException {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        try {
            if ((values == null) || (values.isEmpty())) {
                criteria.add(Restrictions.isNull(propertyName));
            } else {
                criteria.add(Restrictions.in(propertyName, values));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }
    }

    /**
     * A grouping property value
     * @param propertyName
     * @throws CDCException
     */
    public void GBy(String propertyName) throws CDCException {
        if (projections == null) {
            projections = Projections.projectionList();
        }
        projections.add(Projections.groupProperty(propertyName));
    }

    /**
     * A projected property value
     * @param propertyName
     * @throws CDCException
     */
    public void PV(String propertyName) throws CDCException {
        if (projections == null) {
            projections = Projections.projectionList();
        }
        projections.add(Projections.property(propertyName));

    }

    /**
     * A property value count
     * @param propertyName
     * @throws CDCException
     */
    public void PVC(String propertyName) throws CDCException {
        if (projections == null) {
            projections = Projections.projectionList();
        }
        projections.add(Projections.count(propertyName));

    }

    /**
     * A property value sum
     * @param propertyName
     * @throws CDCException
     */
    public void PVS(String propertyName) throws CDCException {
        if (projections == null) {
            projections = Projections.projectionList();
        }
        projections.add(Projections.sum(propertyName));

    }

    /**
     * Ascending order
     * @param propertyName
     */
    public void OByAsc(String propertyName) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        criteria.addOrder(Order.asc(propertyName));
    }

    /**
     * Descending order
     * @param propertyName
     */
    public void OByDesc(String propertyName) {
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        criteria.addOrder(Order.desc(propertyName));
    }

    /**
     * Property for rowCount 
     * @param propertyName
     * @throws CDCException
     */
    public void RCD(String propertyName) throws CDCException {
        rowCountUniqueProperty = propertyName;
    }

    /**
     * 
     * @throws CDCException
     */
    public void find() throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, findByCriteria(true));
    }

    public void find(String hql) throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, getDao().find(hql));
    }

    public <TEntity> List<TEntity> rsFind() throws CDCException {
        return findByCriteria(false);
    }

    public <TEntity> List<TEntity> rsFind(String hql) throws CDCException {
        return getDao().find(hql);
    }

    public WebApplicationContext getWac() {
        return wac;
    }

    /**
     * Add params for service call
     * @param o
     */
    public void param(Object o) {
        if (o == null) {
            return;
        }
        if (srvParams == null) {
            srvParams = new ArrayList<Object>();
            srvParamTypes = new ArrayList<Class>();
        }
        srvParams.add(o);
        srvParamTypes.add(o.getClass());
    }

    public void param(Class type, Object o) {
        if (o == null) {
            return;
        }
        if (srvParams == null) {
            srvParams = new ArrayList<Object>();
            srvParamTypes = new ArrayList<Class>();
        }
        srvParams.add(o);
        srvParamTypes.add(type);
    }

    /**
     * Clear params for service
     * @param o
     */
    public void clear() {
        srvParams = null;
    }

    /**
     * 
     * @param <TEntity>
     * @param o
     * @param m
     * @param args
     * @return
     * @throws CDCException
     */
    private Object invoke(Object o, String m, Object[] values) throws CDCException {
        try {
            if ((values != null) && (values.length > 0)) {
                Class[] valuesTypes = new Class[values.length];
                for (int i = 0; i < values.length; i++) {
                    valuesTypes[i] = values[i].getClass();
                }
                return o.getClass().getMethod(m, valuesTypes).invoke(o, values);
            } else if (criteria != null) {
                return o.getClass().getMethod(m, DetachedCriteria.class).invoke(o, criteria);
            } else {
                return o.getClass().getMethod(m).invoke(o);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param o
     * @param m
     * @param types
     * @param values
     * @return
     * @throws CDCException
     */
    private Object invoke(Object o, String m, Class[] types, Object[] values) throws CDCException {
        try {
            if ((values != null) && (values.length > 0)) {
                return o.getClass().getMethod(m, types).invoke(o, values);
            } else if (criteria != null) {
                return o.getClass().getMethod(m, DetachedCriteria.class).invoke(o, criteria);
            } else {
                return o.getClass().getMethod(m).invoke(o);
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param o
     * @param m
     * @return
     * @throws CDCException
     */
    private Object invoke(Object o, String m) throws CDCException {
        try {
            return o.getClass().getMethod(m).invoke(o);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @param <TEntity>
     * @param m
     * @return
     * @throws CDCException
     */
    public <TObject> TObject call(String m) throws CDCException {
        if ((srvParams != null) && (!srvParams.isEmpty())) {
            return (TObject) invoke(bean(), m, srvParamTypes.toArray(new Class[0]), srvParams.toArray());
        } else {
            return (TObject) invoke(bean(), m, null);
        }

    }

    /**
     * 
     * @param <TEntity>
     * @param m, Method name, params
     * @param params
     * @throws CDCException
     */
    public <TEntity> void write(String m, Object... params) throws CDCException {
        write((List<TEntity>) invoke(bean(), m, params));
    }

    /**
     *
     * @param <TEntity>
     * @param pojos
     * @throws CDCException
     */
    public <TEntity> void write(List<TEntity> pojos) throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, pojos);
    }

    /**
     *
     * @param <TBean>
     * @return
     * @throws CDCException
     */
    private <TBean> TBean bean() throws CDCException {
        if ((srvClazz != null) && (srvName != null)) {
            return (TBean) wac.getBean(srvName, srvClazz);
        } else if ((srvClazz == null) && (srvName != null)) {
            return (TBean) wac.getBean(srvName);
        } else {
            return (TBean) wac.getBean(srvClazz);
        }
    }

    public <TBean> TBean bean(String name, Class<TBean> bClazz) throws CDCException {
        return (TBean) wac.getBean(name, bClazz);
    }

    /**
     * 
     * @param rowCount
     * @param minValue
     * @throws CDCException
     */
    private void tPagin(int rowCount, int minValue) throws CDCException {
        int start = ((integerValue("start") == null) || (integerValue("start").intValue() == 0)) ? minValue : integerValue("start").intValue();
        int limit = ((integerValue("limit") == null) || (integerValue("limit").intValue() == 0)) ? minValue : integerValue("limit").intValue();
        responseWrapper.setPage(start);
        responseWrapper.setPageSize(limit);
        responseWrapper.setRowCount(rowCount);
    }

    /**
     * 
     * @param <TEntity>
     * @param paginator
     * @return
     * @throws CDCException
     */
    private <TEntity> List<TEntity> findByCriteria(boolean paginator) throws CDCException {
        if (getDao() == null) {
            throw new CDCException("cliser.msg.error.dao.notinitialized");
        }
        if (criteria == null) {
            criteria = DetachedCriteria.forClass(eClazz);
        }
        if (paginator) {
            if (projections != null) {
                criteria.setProjection(projections);
            }
            extra();
        }
        return getDao().find(criteria, responseWrapper.getPage(), responseWrapper.getPageSize());
    }

    /**
     * 
     * @param <TEntity>
     * @param projection
     * @param query
     * @throws CDCException
     */
    public <TEntity> void find(StringBuilder projection, StringBuilder query) throws CDCException {
        if (getDao() == null) {
            throw new CDCException("cliser.msg.error.dao.notinitialized");
        }
        /*** ***/
        List<TEntity> md = getDao().find(new StringBuilder("SELECT COUNT(*) ").append(query));
        tPagin(TypeCast.toInt(md.get(0)), 0);
        query.insert(0, projection);
        query.insert(0, "SELECT row_.*,rownum rownum_ FROM (");
        query.append(") row_ WHERE ROWNUM<=").append(responseWrapper.getPage() + responseWrapper.getPageSize());
        query.insert(0, "SELECT * FROM (");
        query.append(") WHERE rownum_>").append(responseWrapper.getPage());
        responseWrapper.setDataJSON(eClazz, eClazzAlia, getDao().find(query));
    }

    /**
     * 
     * @param <TEntity>
     * @throws CDCException
     */
    private <TEntity> void extra() throws CDCException {
        DetachedCriteria criteriaEx = (DetachedCriteria) SerializationHelper.clone(criteria);
        criteriaEx.setProjection(((rowCountUniqueProperty == null) ? Projections.rowCount() : Projections.countDistinct(rowCountUniqueProperty)));
        List<TEntity> md = getDao().find(criteriaEx);
        tPagin(TypeCast.toInt(md.get(0)), 0);
    }

    /**
     * 
     * @param field
     */
    public void exclude(String field) {
        responseWrapper.addExclude(field);
    }

    /**
     * 
     * @param field
     */
    public void include(String field) {
        responseWrapper.addInclude(field);
    }

    /**
     * 
     * @param field
     * @param alias
     */
    public void alias(String field, String alias) {
        responseWrapper.setAlias(field, alias);
    }

    /**
     *
     * @throws CDCException
     */
    public void addParam() throws CDCException {
        if (oEntity == null) {
            try {
                oEntity = eClazz.newInstance();
            } catch (Exception ex) {
                throw new CDCException(ex);
            }
        } else {
            TypeCast.getMethod(eClazz, dbVersion, eClazz);
        }
    }

    /**
     * 
     * @param <TEntity>
     * @param entity
     * @return
     */
    public <TEntity> TEntity save(TEntity entity) {
        return getDao().persist(entity);
    }

    /**
     * 
     * @param <TEntity>
     * @param entitys
     */
    public <TEntity> void save(List<TEntity> entitys) {
        getDao().persist(entitys.toArray());
    }

    /**
     * 
     * @param <TEntity>
     * @param id
     * @param eClazzJoinTable
     * @param entityJoinTable
     * @return
     * @throws CDCException
     */
    public <TEntity> TEntity save(Serializable id, Class<TEntity> eClazzJoinTable, TEntity entityJoinTable) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.save.entityisnull");
        }
        TEntity entity = (TEntity) getDao().get(eClazz, id);
        Object value = TypeCast.GN(entity, "get" + eClazzJoinTable.getSimpleName() + "List");
        if (value == null) {
            value = new ArrayList<TEntity>();
            ((List) value).add(entityJoinTable);
        } else {
            ((List) value).add(entityJoinTable);
        }
        return getDao().persist(entity);
    }

    /**
     * 
     * @param id
     */
    public void remove(Serializable id) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.remove.entityisnull");
        }
        getDao().delete(getDao().get(eClazz, id));
    }

    public boolean exist(Serializable id) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.exist.entityisnull");
        }
        return (getDao().get(eClazz, id) != null);
    }

    /**
     * 
     * @param <TEntity>
     * @param id
     * @param eClazzJoinTable
     * @param idJoinTable
     * @throws CDCException
     */
    public <TEntity> void remove(Serializable id, Class<TEntity> eClazzJoinTable, Serializable idJoinTable) throws CDCException {
        if (eClazz == null) {
            throw new CDCException("cliser.msg.error.save.entityisnull");
        }
        TEntity entity = (TEntity) getDao().get(eClazz, id);
        Object value = TypeCast.GN(entity, "get" + eClazzJoinTable.getSimpleName() + "List");
        if (value != null) {
            TEntity entityJoinTable = (TEntity) getDao().get(eClazzJoinTable, idJoinTable);
            if (value instanceof List) {
                log.info("Remove Join Table: " + ((List) value).remove(entityJoinTable));
                save(entity);
            }
        }
    }

    /**
     * 
     * @param context
     */
    /*   public void setContext(WebApplicationContext context) {
    this.context = context;
    }*/
    /**
     *
     * @param request
     * @throws CDCException
     */
    public void setResquestWrapper(HttpServletRequest request) throws CDCException {
        requestWrapper = new RequestWrapper(request);
    }

    public HttpServletRequest getRequest() {
        return requestWrapper.getRequest();
    }

    /**
     * 
     * @param response
     */
    public void setResponseWrapper(HttpServletResponse response) throws CDCException {
        responseWrapper = new ResponseWrapper(response, stringValue("callback"));
    }

    public ResponseWrapper getResponseWrapper() {
        return responseWrapper;
    }

    /**
     *
     * @param dbProtocol
     */
    public void setDBProtocol(DBProtocolType dbProtocol) {
        this.dbProtocol = dbProtocol;
    }

    /**
     *
     * @param dbVersion
     */
    public void setDBVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    /**
     * 
     * @param sessionName
     */
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    /**
     * 
     * @param name
     * @return
     * @throws CDCException
     */
    public Object form(String name) throws CDCException {
        Object result = null;
        try {
            result = entry(name);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }
        return result;
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public String stringValue(String paramName) throws CDCException {
        return TypeCast.toString(form(paramName));
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Object value(String paramName) throws CDCException {
        return form(paramName);
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Short shortValue(String paramName) throws CDCException {
        return TypeCast.toShort(form(paramName));
    }

    /**
     *
     * @param paramName
     * @return
     * @throws CDCException
     */
    public BigInteger integerValue(String paramName) throws CDCException {
        return TypeCast.toBigInteger(form(paramName));
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public BigDecimal decimalValue(String paramName) throws CDCException {
        return TypeCast.toBigDecimal(form(paramName));
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Long longValue(String paramName) throws CDCException {
        return TypeCast.toLong(form(paramName));
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public Date dateValue(String paramName) throws CDCException {
        return TypeCast.toDate(form(paramName), getDateFormat());
    }

    /**
     * 
     * @param paramName
     * @param dateFormat
     * @return
     * @throws CDCException
     */
    public Date dateValue(String paramName, String dateFormat) throws CDCException {
        return TypeCast.toDate(form(paramName), dateFormat);
    }

    /**
     * 
     * @param name
     * @return
     * @throws CDCException
     */
    private String paramString(String name) throws CDCException {
        return TypeCast.toString(form(name));
    }

    /**
     * 
     * @param name
     * @param replace
     * @return
     * @throws CDCException
     */
    private String paramString(String name, String replace) throws CDCException {
        String rs = TypeCast.toString(form(name));
        return (TypeCast.isNullOrEmpy(rs)) ? rs : replace;
    }

    /**
     * 
     * @param paramName
     * @return
     * @throws CDCException
     */
    public List<Object> listValue(String paramName) throws CDCException {
        // 137,true,true|138,true,false
        List<Object> rs = new ArrayList<Object>();
        StringTokenizer idx = new StringTokenizer(stringValue(paramName), "|,|");
        while (idx.hasMoreTokens()) {
            rs.add(idx.nextToken());
        }
        return rs;
    }

    public List<String[]> arrayValue(String paramName) throws CDCException {
        // 137,true,true|138,true,false
        List<String[]> rs = new ArrayList<String[]>();
        StringTokenizer idx = new StringTokenizer(stringValue(paramName), "|");
        while (idx.hasMoreTokens()) {
            //idxValue = new StringTokenizer(idxItem.nextToken(), ",");
            rs.add(idx.nextToken().split(","));
        }
        return rs;
    }

    /**
     * 
     * @param name
     * @return
     */
    private Object entry(String name) {
        return requestWrapper.getEntry().get(name);
    }

    public <TSessionEntity> void newSession(TSessionEntity sessionEntity) throws CDCException {
        requestWrapper.getSession().setAttribute(sessionName, sessionEntity);
        // TransportContext.transport(getRequest(), sessionEntity);
        checkSession();
    }

    /**
     *
     * @param ex
     */
    public void error(CDCException ex) {
        responseWrapper.setMessage(ex);
    }

    /**
     *
     * @param e
     */
    public void error(String e) {
        responseWrapper.setMessage(e, false);
    }

    /**
     * 
     * @param ex
     */
    public void error(Exception ex) {
        responseWrapper.setMessage(ex);
    }

    /**
     * 
     * @param information
     */
    public void information(String information) {
        responseWrapper.setMessage(information, true);
    }

    /**
     * 
     * @param information
     * @param delimiter
     */
    public void information(String information, String delimiter) {
        StringTokenizer st = new StringTokenizer(information, delimiter);
        while (st.hasMoreTokens()) {
            responseWrapper.setMessage(st.nextToken(), true);
        }
    }

    /**
     *
     * @param key
     * @param data
     */
    public void data(String data) {
        responseWrapper.setData(data);
    }

    /**
     * 
     * @throws CDCException
     */
    public void commit() throws CDCException {
        responseWrapper.commit();
    }

    /**
     * @return the dao
     */
    public CliserDao getDao() {
        return dao;
    }

    /**
     * @param dao the dao to set
     * @deprecated 
     */
    public void setccDao(CliserDao dao) {
        this.dao = dao;
    }

    /**
     * Web application context
     * @param wac
     */
    public void setWac(WebApplicationContext wac) throws CDCException {
        try {
            this.wac = wac;
            dao = this.wac.getBean(BL.class).getDao();
        } catch (BeansException ex) {
            log.info("Bean BL not found.", ex);
        }
    }

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        responseWrapper.setDateFormat(dateFormat);
        this.dateFormat = dateFormat;
    }

    public void setNameIU(String nameIU) {
        this.nameIU = nameIU;
    }

    public String getRealPath() {
        return getWac().getServletContext().getRealPath(System.getProperty("file.separator"));
    }
}

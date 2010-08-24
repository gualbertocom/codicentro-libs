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

import com.codicentro.cliser.dao.Dao;
import com.codicentro.security.SessionEntityBase;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import com.codicentro.utils.Types.DBProtocolType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
public class BL implements Serializable {

    private Logger log = LoggerFactory.getLogger(BL.class);
    private RequestWrapper requestWrapper = null;
    private ResponseWrapper responseWrapper = null;
    private DBProtocolType dbProtocol = null;
    private String dbVersion = null;
    private DetachedCriteria criteria = null;
    private Object IU = "";
    private SessionEntityBase sessionEntity = null;
    private String sessionName = null;
    private WebApplicationContext context = null;
    private Class eClazz = null;
    private String eClazzAlia = null;
    private Object oEntity = null;
    private String dateFormat = null;
    @Resource
    private Dao dao;

    /**
     *
     * @throws CDCException
     */
    public void checkSession() throws CDCException {
        if ((requestWrapper.getSession() == null) || (requestWrapper.getSession().getAttribute(sessionName) == null)) {
            throw new CDCException("cliser.msg.error.sessionexpired");
        }
        sessionEntity = (SessionEntityBase) requestWrapper.getSession().getAttribute(sessionName);
        IU = sessionEntity.getIU();
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
     * @param <T>
     * @param eClazz
     */
    public <T> void entity(Class<T> eClazz) {
        this.eClazz = eClazz;
    }

    /**
     * 
     * @param <T>
     * @param eClazz
     * @param eClazzAlia
     */
    public <T> void entity(Class<T> eClazz, String eClazzAlia) {
        this.eClazz = eClazz;
        this.eClazzAlia = eClazzAlia;
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param paramName
     */
    public void paramEQ(String propertyName, String paramName) throws CDCException {
        paramEQ(propertyName, paramName, false);
    }

    /**
     * Apply case-insensitive an "equal" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param ignoreCase
     */
    public void paramEQ(String propertyName, String paramName, boolean ignoreCase) throws CDCException {
        if (!TypeCast.isNullOrEmpy(paramName)) {
            paramEQ(ignoreCase, propertyName, form(paramName));
        }
    }

    /**
     * Apply an "equal" constraint to the named property
     * @param propertyName
     * @param value
     * @throws CDCException
     */
    public void paramEQ(String propertyName, Object value) throws CDCException {
        paramEQ(false, propertyName, value);
    }

    /**
     * Apply case-insensitive an "equal" constraint to the named property when ignoreCase is true
     * @param ignoreCase
     * @param propertyName
     * @param value
     * @throws CDCException
     */
    public void paramEQ(boolean ignoreCase, String propertyName, Object value) throws CDCException {
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
            } catch (ClassCastException ex) {
            }
        }
    }

    /**
     * Apply an "like" constraint to the named property
     * @param propertyName
     * @param paramName
     * @param define, define like conditions, Values[?%||%?||%?%]
     */
    public void paramLK(String propertyName, String paramName, String define) throws CDCException {
        paramLK(propertyName, paramName, define, false);
    }

    /**
     * Apply case-insensitive an "like" constraint to the named property when ignoreCase is true
     * @param propertyName
     * @param paramName
     * @param define, define like conditions, Values[?%||%?||%?%]
     * @param ignoreCase
     */
    public void paramLK(String propertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
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

    public <T> void paramLK(String propertyNameJoin, String propertyName, String paramName, String define, boolean ignoreCase) throws CDCException {
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
     * 
     * @throws CDCException
     */
    public void find() throws CDCException {
        responseWrapper.setDataJSON(eClazz, eClazzAlia, findByCriteria());
    }

    /**
     *
     * @param <T>
     * @return
     * @throws CDCException
     */
    private <T> List<T> findByCriteria() throws CDCException {
        if (getDao() == null) {
            throw new CDCException("cliser.msg.error.dao.notinitialized");
        }
        if (criteria != null) {
            return getDao().find(criteria);
        } else {
            return getDao().find(eClazz);
        }
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
     * @param alias
     */
    public void alias(String field, String alias) {
        responseWrapper.getJSON().include(field).rootName(alias); //include(field).rootName(alias);
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
     * @param <T>
     * @param entity
     * @return
     */
    public <T> T save(T entity) {
        return getDao().persist(entity);
    }

    /**
     * 
     * @param id
     */
    public void remove(Serializable id) {
        getDao().delete(getDao().get(eClazz, id));
    }

    /**
     * 
     * @param context
     */
    public void setContext(WebApplicationContext context) {
        this.context = context;
    }

    /**
     *
     * @param request
     * @throws CDCException
     */
    public void setResquestWrapper(HttpServletRequest request) throws CDCException {
        requestWrapper = new RequestWrapper(request);
    }

    /**
     * 
     * @param response
     */
    public void setResponseWrapper(HttpServletResponse response) {
        responseWrapper = new ResponseWrapper(response);
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
    public Short shortValue(String paramName) throws CDCException {
        return TypeCast.toShort(form(paramName));
    }

    public BigInteger integerValue(String paramName) throws CDCException {
        return TypeCast.toBigInteger(form(paramName));
    }

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
     * @param name
     * @return
     */
    private Object entry(String name) {
        return requestWrapper.getEntry().get(name);
    }

    /**
     * 
     * @param sessionEntity
     */
    public void newSession(Object sessionEntity) {
        requestWrapper.getSession().setAttribute(sessionName, sessionEntity);
        IU = ((SessionEntityBase) sessionEntity).getIU();
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
    public Dao getDao() {
        return dao;
    }

    /**
     * @param dao the dao to set
     */
    public void setDao(Dao dao) {
        this.dao = dao;
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
}

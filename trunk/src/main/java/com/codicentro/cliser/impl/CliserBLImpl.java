/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Oct 01, 2008, 10:27:26 AM
 * Place: Querétaro, Querétaro, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: CliserBLImpl.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Oct 01, 2008           Alexander Villalobos Yadró      New class.
 **/
package com.codicentro.cliser.impl;

import com.codicentro.cliser.RequestWrapper;
import com.codicentro.cliser.ResponseWrapper;
import com.codicentro.security.SessionEntityBase;
import com.codicentro.model.DataSourceConnection;
import com.codicentro.model.ParamsDB;
import com.codicentro.model.Table;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.LoggerDebug;
import com.codicentro.utils.TypeCast;
import com.codicentro.utils.Types.DBProtocolType;
import com.codicentro.utils.Types.SQLType;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CliserBLImpl implements Serializable {

    private Object IU = "";
    private ResponseWrapper responseWrapper = null;
    private RequestWrapper requestWrapper = null;
    private SessionEntityBase sessionEntity = null;
    private DataSourceConnection connection = null;
    public ParamsDB param = null;
    private HashMap rsDB = null;
    public LoggerDebug logger = null;
    private DBProtocolType dbProtocol = null;
    private BigDecimal dbVersion = null;
    private String sessionName = null;

    public Object getIU() {
        return IU;
    }

    public boolean cnxOpen(String source, String prefix) throws CDCException {
        boolean r = false;
        try {
            connection = new DataSourceConnection(source, prefix);
            r = connection.open();
            if (!r) {
                throw new CDCException("Not connection available.");
            } else {
                connection.setAutoCommit(false);
            }
        } catch (CDCException ex) {
            throw new CDCException(ex);
        }
        return r;
    }

    public void cnxCommit() {
        try {
            if (cnxIsOpen()) {
                connection.commit();
            }
        } catch (CDCException ex) {
            ex.printStackTrace();
        }
    }

    public ParamsDB param() {
        return param;
    }

    public void cnxRollback() {
        try {
            if (cnxIsOpen()) {
                connection.rollback();
            }
        } catch (CDCException ex) {
            ex.printStackTrace();
        }
    }

    public boolean cnxIsOpen() throws CDCException {
        return ((connection != null) && connection.isOpen());
    }

    public boolean cnxClose() {
        try {
            if (cnxIsOpen()) {
                connection.close();
            }
        } catch (CDCException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public void cnxDestroy() {
        try {
            if (cnxIsOpen()) {
                cnxRollback();
                cnxClose();
            }
        } catch (CDCException ex) {
            ex.printStackTrace();
        }
        connection = null;
        param = null;
        rsDB = null;
    }

    /**
     * Execute routine
     * @param source
     * @param prefix
     * @return
     * @throws com.codicentro.tls.CDCException
     */
    public void dbRtn(String source, String prefix) throws CDCException {
        if (cnxOpen(source, prefix)) {
            dbRtn();
        }
    }

    public void dbRtn() throws CDCException {
        switch (dbProtocol) {
            case POSTGRESQL:
                rtn();
                break;
            case INFORMIX:
                if (dbVersion.floatValue() >= 10) {
                    rtn();
                } else {
                    rtnIfx();
                }
                break;
        }
    }

    private void rtnIfx() throws CDCException {
        rsDB = new HashMap();
        PreparedStatement pstmt = null;
        try {
            String sql = generateRtn();
            // ResultSet prmtr = connection.getConnection().getMetaData().getProcedureColumns(null, param.getSchema(), param.getSpName(), null);
            pstmt = connection.prepareStatement(sql);
            for (int i = 0; i < param.inCount(); i++) {
                switch (param.getSQLType(i)) {
                    case VARCHAR:
                        pstmt.setString(i + 1, param.getValueVarchar(i));
                        break;
                    case INTEGER:
                        pstmt.setInt(i + 1, param.getValueInteger(i));
                        break;
                    case NUMERIC:
                        pstmt.setDouble(i + 1, param.getValueNumeric(i).doubleValue());
                        break;
                    case BOOLEAN:
                        pstmt.setBoolean(i + 1, param.getValueBoolean(i));
                        break;
                    case DATE:
                        pstmt.setDate(i + 1, param.getValueDate(i));
                        break;
                    default:
                        pstmt.setObject(i + 1, param.getValue(i));
                        break;
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            if (param.outCount() > 0) {
                if ((param.outCount() == 1) && (param.getSQLTypeOUT(0) == SQLType.CURSOR)) {
                    if (param.isColumnDefineByUser()) {
                        rsDB.put(param.getIdOut(0), new Table(rs, param.getTableType(), param.getColumns()));
                    } else {
                        rsDB.put(param.getIdOut(0), new Table(rs, param.getTableType()));
                    }
                } else if (rs.next()) {
                    for (int i = 0; i < param.outCount(); i++) {
                        switch (param.getSQLTypeOUT(i)) {
                            case VARCHAR:
                                rsDB.put(param.getIdOut(i), (rs.getString(i + 1) == null) ? "null" : rs.getString(i + 1));
                                break;
                            case INTEGER:
                                rsDB.put(param.getIdOut(i), rs.getInt(i + 1));
                                break;
                            case NUMERIC:
                                rsDB.put(param.getIdOut(i), rs.getBigDecimal(i + 1));
                                break;
                            case BOOLEAN:
                                rsDB.put(param.getIdOut(i), rs.getBoolean(i + 1));
                                break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new CDCException(ex);
        } finally {
            pstmt = null;
        }
    }

    /**
     * Execute routine general
     * @return
     * @throws com.codicentro.tls.CDCException
     */
    private void rtn() throws CDCException {
        rsDB = new HashMap();
        CallableStatement cstmt = null;
        try {
            String sql = generateRtn();
            logger.log("Routine -> " + sql);
            // logger.log("Params -> " + param.getParams());
            //ResultSet prmtr = connection.getConnection().getMetaData().getProcedureColumns(null, param.getSchema(), param.getSpName(), null);
            cstmt = connection.prepareCall(sql);
            /*
            for (int i = 0; i < param.size(); ++i) {
            switch (param.getSQLParamType(i)) {
            case OUT:
            switch (param.getSQLType(i)) {
            case VARCHAR:
            cstmt.registerOutParameter(i + 1, Types.VARCHAR);
            break;
            case INTEGER:
            cstmt.registerOutParameter(i + 1, Types.INTEGER);
            break;
            case NUMERIC:
            cstmt.registerOutParameter(i + 1, Types.NUMERIC);
            break;
            case BOOLEAN:
            cstmt.registerOutParameter(i + 1, Types.BOOLEAN);
            break;
            default:
            cstmt.registerOutParameter(i + 1, Types.OTHER);
            break;
            }
            break;
            case IN:
            switch (param.getSQLType(i)) {
            case VARCHAR:
            cstmt.setObject(i + 1, param.getValueVarchar(i));
            break;
            case INTEGER:
            cstmt.setObject(i + 1, param.getValueInteger(i));
            break;
            case NUMERIC:
            cstmt.setObject(i + 1, param.getValueNumeric(i));
            break;
            case BOOLEAN:
            cstmt.setBoolean(i + 1, param.getValueBoolean(i));
            break;
            case OTHER:
            default:
            cstmt.setObject(i + 1, param.getValue(i));
            break;
            }
            break;
            }
            }*/
            cstmt.execute();
            int rsIndex = 0;
            //if (param.getTableType() != TableType.EXECUTE) {
           /* for (int i = 0; i < param.size(); ++i) {
            switch (param.getSQLParamType(i)) {
            case OUT:
            rsIndex++;
            switch (param.getSQLType(i)) {
            case VARCHAR:
            rsDB.put(param.getIdOut(i), (cstmt.getString(rsIndex) == null) ? "null" : cstmt.getString(rsIndex));
            break;
            case INTEGER:
            rsDB.put(param.getIdOut(i), cstmt.getInt(rsIndex));
            break;
            case NUMERIC:
            rsDB.put(param.getIdOut(i), cstmt.getBigDecimal(rsIndex));
            break;
            case BOOLEAN:
            rsDB.put(param.getIdOut(i), cstmt.getBoolean(rsIndex));
            break;
            default:
            rsDB.put(param.getIdOut(i), new Table((ResultSet) cstmt.getObject(rsIndex), param.getTableType()));
            break;
            }
            break;
            }
            }*/
        } catch (Exception ex) {
            throw new CDCException(ex);
        } finally {
            cstmt = null;
        }
    }

    public Table rsDBTable(String key) {
        return (Table) rsDB.get(key);
    }

    public Integer rsDBInteger(String key) throws CDCException {
        return TypeCast.toInteger(rsDB.get(key));
    }

    public String rsDBString(String key) throws CDCException {
        return TypeCast.toString(rsDB.get(key));
    }

    public Connection getCnx() throws CDCException {
        return (cnxIsOpen()) ? connection.getConnection() : null;
    }

    private Object entry(String name) {
        return requestWrapper.getEntry().get(name);
    }

    public InputStream formFile(String name) throws CDCException {
        return (InputStream) form(name);
    }

    /**
     * Return
     * @param name - Name of the property whose value is to be retrieved.
     * @return
     * @throws com.codicentro.tls.CDCException
     */
    public Object form(String name) throws CDCException {
        Object result = null;
        try {
            result = entry(name);
        } catch (java.lang.IllegalArgumentException ex) {
            throw new CDCException(ex);
        } catch (NullPointerException ex) {
            throw new CDCException(ex);
        }

        return result;
    }

    /**
     * 
     * @param name
     * @return
     * @throws com.codicentro.tls.CDCException
     */
    public String formString(String name) throws CDCException {
        return TypeCast.toString(form(name));
    }

    public boolean formBoolean(String name) throws CDCException {
        return TypeCast.ObjectToBoolean(form(name));
    }

    /**
     *
     * @param name - Name of the property whose value is to be retrieved.
     * @param replace - Replace return object when Object form is null
     * @return
     * @throws com.codicentro.tls.CDCException
     */
    public Object form(String name, Object replace) throws CDCException {
        return TypeCast.NVL(form(name), replace);
    }

    public void formArrayWrapper(String name) throws CDCException {
        requestWrapper.arrayWrapper(name);
    }

    private Object[] formArray(String name) throws CDCException {
        return (Object[]) form(name);
    }

    public int formArrayLength(String name) throws CDCException {
        return formArray(name).length;
    }

    public Object formArray(String name, int index) throws CDCException {
        return formArray(name)[index];
    }

    public Object formArray(String name, int index, int subindex) throws CDCException {
        return ((Object[]) formArray(name, index))[subindex];
    }

    public ArrayList formList(String name, SQLType type, Object r) throws CDCException {
        ArrayList rs = new ArrayList();
        String l = formString(name);
        l = ((l == null) || (l.trim().equals(""))) ? TypeCast.toString(r) : l;
        String e = null;
        StringTokenizer idx = new StringTokenizer(l, "|,|");
        while (idx.hasMoreTokens()) {
            e = idx.nextToken();
            switch (type) {
                case INTEGER:
                    rs.add(TypeCast.StringToInteger(e));
                    break;
                default:
                    rs.add(e);
                    break;
            }
        }
        return rs;
    }

    public int formInt(String name) throws CDCException {
        return TypeCast.ObjectToint(form(name));
    }

    public int formInt(String name, int replace) throws CDCException {
        return TypeCast.ObjectToint(TypeCast.NVL(form(name), replace));
    }

    /**
     *
     * @param exception
     * @param currentThread
     */
    public void error(CDCException ex) {
        responseWrapper.setMessage(ex);
    }

    public void error(String e) {
        responseWrapper.setMessage(e, false);
    }

    /**
     *
     * @param exception
     * @param currentThread
     */
    public void error(Exception ex) {
        responseWrapper.setMessage(ex);
    }

    /**
     * 
     * @param table
     */
    public void misc(Table table) throws CDCException {
        responseWrapper.setMisc(table);
    }

    /**
     *
     * @param rowCount
     */
    public void rowCount(int rowCount) {
        responseWrapper.setRowCount(rowCount);
    }

    /**
     *
     * @param colCount
     */
    public void colCount(int colCount) {
        responseWrapper.setColCount(colCount);
    }

    public void page(int page) {
        responseWrapper.setPage(page);
    }

    public void pageSize(int pageSize) {
        responseWrapper.setPageSize(pageSize);
    }

    /**
     *
     */
    public void commit() throws CDCException {
        responseWrapper.commit();
    }

    /**
     *
     * @param information
     */
    public void information(String information) {
        responseWrapper.setMessage(information, true);
    }

    public void information(String information, String delimiter) {
        StringTokenizer st = new StringTokenizer(information, delimiter);
        while (st.hasMoreTokens()) {
            responseWrapper.setMessage(st.nextToken(), true);
        }
    }

    /**
     *
     * @param data
     */
    /* public void data(Object data) {
    responseWrapper.setData(data);
    }*/
    public void data(String key, Object data) {
       // responseWrapper.setData(key, data);
    }

    public void data(String key, Object data, boolean cuotes) {
        //responseWrapper.setData(key, data, cuotes);
    }

    /**
     *
     * @return
     */
    public Object session() {
        return sessionEntity;
    }

    /**
     *
     * @return
     */
    public Object session(String method) throws CDCException {
        Object result = null;
        try {
            result = sessionEntity.getClass().getMethod("get" + method).invoke(sessionEntity);
        } catch (Exception ex) {
            throw new CDCException(ex);
        }

        return result;
    }

    /**
     * Create New Session
     * @param request
     * @param sessionEntity
     * @param sessionName
     */
    public void newSession(Object sessionEntity) {
        requestWrapper.getSession().setAttribute(sessionName, sessionEntity);
        IU = ((SessionEntityBase) sessionEntity).getIU();
    }

    public void checkSession() throws CDCException {
        if ((requestWrapper.getSession() == null) || (requestWrapper.getSession().getAttribute(sessionName) == null)) {
            throw new CDCException("lng.msg.error.sessionexpired");
        }
        sessionEntity = (SessionEntityBase) requestWrapper.getSession().getAttribute(sessionName);
        IU = sessionEntity.getIU();
    }

    public void setResquestWrapper(HttpServletRequest request) throws CDCException {
        requestWrapper = new RequestWrapper(request);
    }

    public void getWriter() throws CDCException {
        responseWrapper.getWriter();
    }

    public ResponseWrapper getResponseWrapper() throws CDCException {
        return responseWrapper;
    }

    public void setResponseWrapper(HttpServletResponse response) {
        responseWrapper = new ResponseWrapper(response);
    }

    public RequestWrapper getRequestWrapper() {
        return requestWrapper;
    }

    public void setDBProtocol(DBProtocolType dbProtocol) {
        this.dbProtocol = dbProtocol;
    }

    public void setDBVersion(BigDecimal dbVersion) {
        this.dbVersion = dbVersion;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setLogger(LoggerDebug logger) {
        this.logger = logger;
    }

    public CliserBLImpl() {
        param = new ParamsDB();
        IU = "";
        responseWrapper = null;
        requestWrapper = null;
        sessionEntity = null;
        connection = null;
        logger = null;
        dbProtocol = null;
        connection = null;
    }

    private String generateRtn() throws CDCException {
        String name = "";
        String out = null;
        String in = null;
        switch (dbProtocol) {
            case POSTGRESQL:
                /* for (int i = 0; i < param.size(); ++i) {
                switch (SQLParamType.valueOf(TypeCast.ObjectToString(param.get(i).get("PARAM-TYPE"), "IN"))) {
                case IN:
                in = (in == null) ? "?" : in + ",?";
                break;
                case OUT:
                out = (out == null) ? "?" : out + ",?";
                break;
                default:
                in = (in == null) ? "?" : in + ",?";
                out = (out == null) ? "?" : out + ",?";
                break;
                }
                }
                out = (out == null) ? "" : out + " = ";
                in = (in == null) ? "" : in;
                name = "{ " + out + "call " + param.getCallName() + "(" + in + ")" + " }";*/
                break;
            case INFORMIX:


                for (int i = 0; i < param.outCount(); i++) {
                    out = (out == null) ? "?" : out + ",?";
                }
                for (int i = 0; i < param.inCount(); i++) {
                    in = (in == null) ? "?" : in + ",?";
                }
                out = (out == null) ? "" : out + " = ";
                in = (in == null) ? "" : in;
                if (dbVersion.floatValue() >= 10) {
                    name = "{ " + out + "call " + param.getSpName() + "(" + in + ")" + " }";
                } else {
                    name = "{ call " + param.getSpName() + "(" + in + ") }";
                }
                break;
        }
        return name;
    }
}

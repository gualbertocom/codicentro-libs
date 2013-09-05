/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Oct 01, 2008, 10:27:26 AM
 * Place: Querétaro, Querétaro, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: ResponseWrapper.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Oct 01, 2008           Alexander Villalobos Yadró      New class.
 **/
package com.codicentro.cliser;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import com.codicentro.core.model.Table;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseWrapper implements Serializable {

    private Logger log = LoggerFactory.getLogger(ResponseWrapper.class);
    /**
     *
     */
    private static final long serialVersionUID = -7581873747670708222L;
    private StringBuffer message = null;
    private StringBuffer tracert = null;
    private Set<String> data = null;
    private HttpServletResponse response = null;
    private int rowCount = -1;
    private int colCount = -1;
    private int page = -1;
    private int pageSize = -1;
    private boolean success = true;
    private PrintWriter writer = null;
    private JSONSerializer dataJSON = null;
    private boolean deepSerializer = false;
    private List<String> includes = null;
    private List<String> excludes = null;
    private String dateFormat = null;
    private String callback = null;
    private Map<String, String> mAlias = null;

    public ResponseWrapper(HttpServletResponse response, String callback) {
        this.response = response;
        this.callback = callback;
        data = new HashSet<String>();
        message = new StringBuffer();
        tracert = new StringBuffer();
        rowCount = -1;
        colCount = -1;
        dataJSON = new JSONSerializer();
        includes = new ArrayList<String>();
        excludes = new ArrayList<String>();
        excludes.add("*");
        mAlias = new HashMap<String, String>();
    }

    /**
     *
     * @param field
     */
    public void addExclude(String field) {
        excludes.add(field);
    }

    /**
     *
     * @param field
     */
    public void addInclude(String field) {
        includes.add(field);
    }

    public void addInclude(String field, String alias) {
        addExclude(field);
        mAlias.put(field, alias);
    }

    public void setAlias(String field, String alias) {
        mAlias.put(field, alias);
    }

    /**
     *
     * @param data
     */
    public void setData(String data) {
        this.data.add(data);
    }

    /**
     *
     * @return
     */
    public JSONSerializer getJSON() {
        return dataJSON;
    }

    /**
     *
     * @param <TEntity>
     * @param pojos
     */
    public <TEntity> void setDataJSON(List<TEntity> pojos) {
        dataJSON(null, pojos);
    }

    /**
     *
     * @param <TEntity>
     * @param eClazz
     * @param pojos
     */
    public <TEntity> void setDataJSON(Class<TEntity> eClazz, List<TEntity> pojos) {
        setDataJSON(eClazz, null, pojos);
    }

    /**
     *
     * @param <TEntity>
     * @param eClazz
     * @param eClazzAlia
     * @param pojos
     */
    public <TEntity> void setDataJSON(Class<TEntity> eClazz, String eClazzAlia, List<TEntity> pojos) {
        eClazzAlia = (eClazzAlia == null) ? eClazz.getSimpleName() : eClazzAlia;
        dataJSON(eClazzAlia, pojos);
    }

    /**
     *
     * @param <TEntity>
     * @param eClazz
     * @param eClazzAlia
     * @param pojo
     */
    public <TEntity> void setDataJSON(TEntity pojo, Class<TEntity> eClazz, String eClazzAlia) {
        eClazzAlia = (eClazzAlia == null) ? eClazz.getSimpleName() : eClazzAlia;
        dataJSON(eClazzAlia, pojo);
    }

    private void initializeJSON(String eClazzAlia) {
        dataJSON.rootName(eClazzAlia);
        dataJSON.setIncludes(includes);
        dataJSON.setExcludes(excludes);
        dataJSON.transform(new DateTransformer(dateFormat), Date.class);
        dataJSON.setAlias(mAlias);
    }

    /**
     *
     * @param <TEntity>
     * @param eClazzAlia
     * @param pojos
     */
    private <TEntity> void dataJSON(String eClazzAlia, List<TEntity> pojos) {
        initializeJSON(eClazzAlia);
        if (!pojos.isEmpty()) {
            StringBuilder out = new StringBuilder();
            if (deepSerializer) {
                log.info("/* DEEP SERIALIZER */");
                dataJSON.deepSerialize(pojos, out);
            } else {
                log.info("/* SERIALIZER */");
                dataJSON.serialize(pojos, out);
            }
            if (out.toString().startsWith("{")) {
                data.add(out.toString().substring(1, out.toString().length() - 1));
            } else {
                data.add(out.toString());
            }
        } else {
            data.add(eClazzAlia + ":[]");
        }

    }

    /**
     *
     * @param <TEntity>
     * @param eClazzAlia
     * @param pojo
     */
    private <TEntity> void dataJSON(String eClazzAlia, TEntity pojo) {
        initializeJSON(eClazzAlia);
        if (pojo != null) {
            StringBuilder out = new StringBuilder();
            if (deepSerializer) {
                log.info("/* DEEP SERIALIZER */");
                dataJSON.deepSerialize(pojo, out);
            } else {
                log.info("/* SERIALIZER */");
                dataJSON.serialize(pojo, out);
            }
            if (out.toString().startsWith("{")) {
                data.add(out.toString().substring(1, out.toString().length() - 1));
            } else {
                data.add(out.toString());
            }
        } else {
            data.add(eClazzAlia + ":[]");
        }
    }

    /**
     *
     * @param table
     * @throws CDCException
     */
    public void setMisc(Table table) throws CDCException {
        page = table.getPage();
        pageSize = table.getPageSize();
        rowCount = table.rowCount();
        colCount = table.colCount();
    }

    public void setMisc(Map m) throws CDCException {
        page = TypeCast.toInt(m.get("page"));
        pageSize = TypeCast.toInt(m.get("pageSize"));
        rowCount = TypeCast.toInt(m.get("rowCount"));
        colCount = TypeCast.toInt(m.get("colCount"));
    }

    /**
     *
     * @param rowCount
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     *
     * @param colCount
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    /**
     *
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    /**
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     *
     * @param exception
     * @param currentThread
     */
    public void setMessage(CDCException e) {
        // setMessage(exception);
        if (e.getFrontEndMessage() == null) {
            setMessage(e.getMessage(), false);
        } else {
            setMessage(e.getFrontEndMessage(), false);
        }
    }

    /**
     *
     * @param e
     */
    public void setMessage(Exception e) {
        String msg = e.getLocalizedMessage();
        if ((msg != null) && (msg.indexOf("CDCError:") != -1)) {
            msg = msg.substring(msg.indexOf("CDCError:") + 9);
        }
        setMessage(msg, false);
    }

    /**
     *
     * @param message
     * @param success
     */
    public void setMessage(String message, boolean success) {
        if (message != null) {
            this.success = this.success && success;
            message = "\"" + TypeCast.cuotes(message) + "\"";
            this.message.append((this.message.toString().trim().length() > 0) ? ","
                    + message : message);
        }
    }

    /**
     *
     * @return @throws CDCException
     */
    public PrintWriter getWriter() throws CDCException {
        try {
            return response.getWriter();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @return
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    public String toJSON() throws CDCException {
        try {
            StringBuilder json = new StringBuilder();
            if (callback != null) {
                json.append(callback).append("(");
            }
            json.append("{\"version\":1.0");
            json.append(",\"success\":").append(success);
            json.append(",\"tracer\":[").append(((tracert == null) ? "" : tracert.toString())).append("]");
            json.append(",\"message\":[").append(((message == null) ? "" : message.toString())).append("]");
            for (Iterator<String> i = data.iterator(); i.hasNext();) {
                String tmpData = i.next();
                if (!TypeCast.isBlank(tmpData)) {
                    json.append(",").append(tmpData);
                }
            }
            json.append(",\"rowCount\":").append(rowCount);
            json.append(",\"colCount\":").append(colCount);
            json.append(",\"page\":").append(page);
            json.append(",\"pageSize\":").append(pageSize);
            json.append("}");
            if (callback != null) {
                json.append(");");
            }
            return charSpecial(json.toString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @return
     */
    public void commit() throws CDCException {
        try {
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Expires", "Mon, 01 Jan 2007 01:00:00 GMT");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Access-Control-Allow-Origin", "*");
            writer = response.getWriter();
            writer.print(toJSON());
            writer.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        }
    }

    /**
     *
     * @param data
     * @return
     */
    public static String success(String data) {
        return success(data, -1);
    }

    public static String success(String data, int total) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("data:").append(data);
        sb.append(",success:").append(true);
        sb.append(",rowCount:").append(total);
        sb.append("}");
        return charSpecial(sb.toString());
    }

    public static String failed(String message) {
        if (!TypeCast.isBlank(message)) {
            message = message.replaceAll("\"", "\\\\\"");
        }
        StringBuilder sb = new StringBuilder("{");
        sb.append("message:\"").append(message).append("\"");
        sb.append(",success:").append(false);
        sb.append("}");
        return charSpecial(sb.toString());
    }

    /**
     *
     * @param r
     * @return
     */
    public static String charSpecial(String r) {
        if (TypeCast.isBlank(r)) {
            return null;
        }
        r = r.replaceAll("\n", "\\\\n");
        r = r.replaceAll("\r\n", "\\\\n");

        r = r.replaceAll("ñ", "\\\\361");
        r = r.replaceAll("Ñ", "\\\\321");

        r = r.replaceAll("á", "\\\\341");
        r = r.replaceAll("Á", "\\\\301");

        r = r.replaceAll("é", "\\\\351");
        r = r.replaceAll("É", "\\\\311");

        r = r.replaceAll("í", "\\\\355");
        r = r.replaceAll("Í", "\\\\315");

        r = r.replaceAll("ó", "\\\\363");
        r = r.replaceAll("Ó", "\\\\323");

        r = r.replaceAll("ú", "\\\\372");
        r = r.replaceAll("Ú", "\\\\332");

        r = r.replaceAll("¿", "\\\\277");

        // r = r.replaceAll("\"", "'");
        return r;
    }

    /**
     * @return the deepSerializer
     */
    public boolean isDeepSerializer() {
        return deepSerializer;
    }

    /**
     * @param deepSerializer the deepSerializer to set
     */
    public void setDeepSerializer(boolean deepSerializer) {
        this.deepSerializer = deepSerializer;
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
        this.dateFormat = dateFormat;
    }
}

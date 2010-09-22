/**
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import com.codicentro.model.Table;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private List<String> excludes = null;
    private String dateFormat = null;
    private String callback = null;

    public ResponseWrapper(HttpServletResponse response, String callback) {
        this.response = response;
        this.callback = callback;
        data = new HashSet<String>();
        message = new StringBuffer();
        tracert = new StringBuffer();
        rowCount = -1;
        colCount = -1;
        dataJSON = new JSONSerializer();
        excludes = new ArrayList<String>();
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
     * @param <T>
     * @param pojos
     */
    public <T> void setDataJSON(List<T> pojos) {
        dataJSON(null, pojos);
    }

    /**
     * 
     * @param <T>
     * @param eClazz
     * @param pojos
     */
    public <T> void setDataJSON(Class<T> eClazz, List<T> pojos) {
        setDataJSON(eClazz, null, pojos);
    }

    /**
     *
     * @param <T>
     * @param eClazz
     * @param eClazzAlia
     * @param pojos
     */
    public <T> void setDataJSON(Class<T> eClazz, String eClazzAlia, List<T> pojos) {
        eClazzAlia = (eClazzAlia == null) ? eClazz.getSimpleName() : eClazzAlia;
        dataJSON(eClazzAlia, pojos);
    }

    /**
     * 
     * @param <T>
     * @param eClazzAlia
     * @param pojos
     */
    private <T> void dataJSON(String eClazzAlia, List<T> pojos) {
        dataJSON.rootName(eClazzAlia);
        dataJSON.setExcludes(excludes);
        dataJSON.transform(new DateTransformer(dateFormat), Date.class);
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
            data.add("\"" + eClazzAlia + "\":[]");
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

    /**
     * 
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
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
        String msg = e.getMessage();
        if ((msg != null) && (msg.indexOf("CDCError:") != -1)) {
            msg = msg.substring(msg.indexOf("CDCError:") + 9);
        } else {
            msg = (e.getCause() == null) ? e.getMessage() : e.getCause().getMessage();
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
     * @return
     * @throws CDCException
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

    /**
     *
     * @return
     */
    public void commit() throws CDCException {
        StringBuilder json = null;
        try {
            json = new StringBuilder();
            if (callback != null) {
                json.append(callback).append("(");
            }
            json.append("{\"version\":1.0");
            json.append(",\"success\":").append(success);
            json.append(",\"tracer\":[").append(((tracert == null) ? "" : tracert.toString())).append("]");
            json.append(",\"message\":[").append(((message == null) ? "" : message.toString())).append("]");
            //json.append(",data:[");
            String tmpData = null;
            for (Iterator<String> i = data.iterator(); i.hasNext();) {
                tmpData = i.next();
                if (!TypeCast.isNullOrEmpy(tmpData)) {
                    json.append(",").append(tmpData);
                }
            }
            //json.append("]");
            json.append(",\"rowCount\":").append(rowCount);
            json.append(",\"colCount\":").append(colCount);
            json.append(",\"page\":").append(page);
            json.append(",\"pageSize\":").append(pageSize);
            json.append("}");
            if (callback != null) {
                json.append(");");
            }
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Expires", "Mon, 01 Jan 2007 01:00:00 GMT");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Access-Control-Allow-Origin", "*");

            writer = response.getWriter();
            writer.print(charSpecial(json.toString()));
            writer.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new CDCException(ex);
        } finally {
            message = null;
            tracert = null;
            data = null;
            rowCount = -1;
            colCount = -1;
            success = true;
            response = null;
            writer = null;
            json = null;
        }
    }

    /**
     * 
     * @param r
     * @return
     */
    private String charSpecial(String r) {
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

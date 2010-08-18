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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.util.HashSet;
import java.util.List;
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
    private XStream xstreamJson = null;

    /**
     *
     * @param response
     */
    public ResponseWrapper(HttpServletResponse response) {
        this.response = response;
        data = new HashSet<String>();
        message = new StringBuffer();
        tracert = new StringBuffer();
        rowCount = -1;
        colCount = -1;
        xstreamJson = new XStream(new JettisonMappedXmlDriver());
        xstreamJson.setMode(XStream.NO_REFERENCES);
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
    public XStream getXStreamJson() {
        return xstreamJson;
    }

    /**
     * 
     * @param <T>
     * @param eClazz
     * @param pojos
     */
    public <T> void setDataXstreamJson(Class<T> eClazz, List<T> pojos) {
        dataXstreamJson(eClazz, null, pojos);
    }

    /**
     *
     * @param <T>
     * @param eClazz
     * @param eClazzAlia
     * @param pojos
     */
    public <T> void setDataXstreamJson(Class<T> eClazz, String eClazzAlia, List<T> pojos) {
        dataXstreamJson(eClazz, eClazzAlia, pojos);
    }

    /**
     * 
     * @param <T>
     * @param eClazz
     * @param eClazzAlia
     * @param pojos
     */
    private <T> void dataXstreamJson(Class<T> eClazz, String eClazzAlia, List<T> pojos) {
        eClazzAlia = (eClazzAlia == null) ? eClazz.getSimpleName() : eClazzAlia;
        xstreamJson.alias(eClazzAlia, eClazz);
        String tmpData = "\"" + eClazzAlia + "\":[]";
        if (!pojos.isEmpty()) {
            tmpData = xstreamJson.toXML(pojos);
            tmpData = tmpData.substring("{\"list\":[{".length(), tmpData.length() - 3);
            if (TypeCast.isNullOrEmpy(tmpData)) {
                tmpData = "\"" + eClazzAlia + "\":[]";
            }
        }
        data.add(tmpData);
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
            msg = e.getCause().getMessage();
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
            json = new StringBuilder("{version:1.0");
            json.append(",success:").append(success);
            json.append(",tracer:[").append(((tracert == null) ? "" : tracert.toString())).append("]");
            json.append(",message:[").append(((message == null) ? "" : message.toString())).append("]");
            String tmpData = null;
            for (Iterator<String> i = data.iterator(); i.hasNext();) {
                tmpData = i.next();
                if (!TypeCast.isNullOrEmpy(tmpData)) {
                    json.append(",").append(tmpData);
                }
            }
            json.append(",rowCount:").append(rowCount);
            json.append(",colCount:").append(colCount);
            json.append(",page:").append(page);
            json.append(",pageSize:").append(pageSize);
            json.append("}");
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Expires", "Mon, 01 Jan 2007 01:00:00 GMT");
            response.setHeader("Cache-Control", "must-revalidate");
            response.setHeader("Cache-Control", "no-cache");
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
        r = r.replaceAll("é", "\\\\361");
        r = r.replaceAll("É", "\\\\311");
        r = r.replaceAll("í", "\\\\351");
        r = r.replaceAll("Í", "\\\\315");
        r = r.replaceAll("ó", "\\\\363");
        r = r.replaceAll("Ó", "\\\\323");
        r = r.replaceAll("ú", "\\\\372");
        r = r.replaceAll("Ú", "\\\\332");
        return r;
    }
}

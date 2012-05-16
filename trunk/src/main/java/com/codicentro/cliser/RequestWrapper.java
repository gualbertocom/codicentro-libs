/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 20, 2009, 05:37:24 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: RequestWrapper.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Apr 20, 2009           Alexander Villalobos Yadró      New class.
 **/
package com.codicentro.cliser;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import java.io.*;
import java.util.*;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestWrapper implements Serializable {

    private Logger log = LoggerFactory.getLogger(ResponseWrapper.class);
    private ServletInputStream In;
    private byte buffer[] = new byte[4096];
    private String delimitor = null;
    private HashMap entry;
    private final String charset = "ISO-8859-1";
    private HttpServletRequest request = null;

    /**
     * 
     * @param _req
     * @throws CDCException
     */
    public RequestWrapper(HttpServletRequest _req) throws CDCException {
        try {
            request = _req;
            In = request.getInputStream();
            delimitor = request.getContentType();
            if ((delimitor != null) && (delimitor.indexOf("boundary=") != -1)) {
                delimitor = delimitor.substring(delimitor.indexOf("boundary=") + 9, delimitor.length());
                delimitor = "--" + delimitor;
            }
            Enumeration e = request.getParameterNames();
            String paramName;
            String[] values;
            entry = new HashMap();
            while (e.hasMoreElements()) {
                paramName = TypeCast.toString(e.nextElement()).trim();
                values = request.getParameterValues(paramName);
                if (values == null) {
                    entry.put(paramName, "null");
                } else if (values.length == 1) {
                    entry.put(paramName, values[0]);
                } else {
                    entry.put(paramName, values);
                }
            }
            while (hasNextParameter()) {
            }
        } catch (Exception ex) {
            log.error(ex.getCause().getMessage(), ex);
            throw new CDCException(ex);
        }
    }

    /**
     * 
     * @return
     */
    public HttpSession getSession() {
        return request.getSession();
    }

    /**
     * 
     * @return
     */
    public HashMap getEntry() {
        return entry;
    }

    /**
     *
     * @param name
     * @throws CDCException
     */
    public void arrayWrapper(String name) throws CDCException {
        if (entry.containsKey(name)) {
            StringTokenizer idx = new StringTokenizer(TypeCast.toString(entry.get(name)), "||");
            ArrayList o = (idx.hasMoreTokens()) ? new ArrayList() : null;
            StringTokenizer cm = null;
            ArrayList a = null;
            while (idx.hasMoreTokens()) {
                a = new ArrayList();
                cm = new StringTokenizer(idx.nextToken(), ",");
                while (cm.hasMoreTokens()) {
                    a.add(cm.nextToken());
                }
                o.add(a.toArray());
            }
            entry.remove(name);
            entry.put(name, o.toArray());
        }
    }

    /**
     *
     * @return
     */
    public String getEntryKeys() {
        String r = "[";
        Iterator i = entry.keySet().iterator();
        while (i.hasNext()) {
            r += (r.equals("[") ? "" : ",") + i.next();
        }
        return r + "]";
    }

    /**
     * 
     * @return
     */
    private String readLine() {
        try {
            int noData = In.readLine(buffer, 0, buffer.length);
            if (noData != -1) {
                return new String(buffer, 0, noData, charset);
            }
        } catch (Exception ex) {
            log.error(ex.getCause().getMessage(), ex);
        }
        return null;
    }

    /**
     * 
     * @return
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 
     * @return
     */
    private boolean hasNextParameter() {
        boolean isFileType = false;
        String filename = null;
        try {
            String LineIn = null, paramName = null;
            while ((LineIn = readLine()) != null) {
                if (LineIn.indexOf("name=") != -1) {
                    int c1 = LineIn.indexOf("name=");
                    int c2 = LineIn.indexOf("\"", c1 + 6);
                    paramName = LineIn.substring(c1 + 6, c2);
                    if (LineIn.indexOf("filename=") != -1) {
                        isFileType = true;
                        c1 = LineIn.indexOf("filename=");
                        c2 = LineIn.indexOf("\"", c1 + 10);
                        filename = LineIn.substring(c1 + 10, c2);
                        if (filename.lastIndexOf("\\") != -1) {
                            filename = filename.substring(filename.lastIndexOf("\\") + 1);
                        }
                        if (filename.length() == 0) {
                            filename = null;
                        }
                        //  System.out.println("fileName=" + filename);
                    }

                    //- Move the pointer to the start of the data
                    LineIn = readLine();
                    if (LineIn.indexOf("Content-Type") != -1) {
                        LineIn = readLine();
                    }
                    if (isFileType) {
                        entry.put(paramName.trim(), readFile());
                    } else {
                        entry.put(paramName.trim(), readLine().trim());
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            log.error(ex.getCause().getMessage(), ex);
        }
        return false;
    }

    /**
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Object readFile() throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int noData;
            while ((noData = In.readLine(buffer, 0, buffer.length)) != -1) {
                if (buffer[0] == '-') {
                    if (new String(buffer, 0, noData, "ISO-8859-1").indexOf(delimitor) == 0) {
                        break;
                    }
                } else {
                    baos.write(buffer, 0, noData);
                }
            }
            baos.flush();
            baos.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception ex) {
            log.error(ex.getCause().getMessage(), ex);
        }

        return null;
    }
}

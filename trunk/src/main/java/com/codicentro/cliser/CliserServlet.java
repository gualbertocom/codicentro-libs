/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Apr 20, 2009, 05:37:24 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Entry.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Apr 20, 2009           Alexander Villalobos Yadró      New class.
 **/
package com.codicentro.cliser;

import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import com.codicentro.utils.Types.DBProtocolType;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CliserServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(CliserServlet.class);
    private String webServerHome = null;
    private final String cliserConfig = "/WEB-INF/cliser-config.xml";
    private Element fConfig = null;
    private String controllerParamName = null;
    private String sessionName = null;
    private DBProtocolType dbProtocol = null;
    private String dbVersion = null;
    private ResponseWrapper rw = null;
    private WebApplicationContext wac = null;
    private String dateFormat = null;
    private String callback = null;
    private Map<String, String> mBLs = null;
    private StringBuilder errBuf = null;

    /**
     * 
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        rw = null;
        try {
            webServerHome = System.getProperty(getServletConfig().getInitParameter("property.webserver.home"));
            log.info("Start Cliser is in progress...");
            log.info("Web server home -> " + webServerHome);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(getServletContext().getRealPath("") + cliserConfig));
            fConfig = doc.getRootElement();
            Element eI = fConfig.getChild("cliser-init");
            Element e = eI.getChild("controller-param-name");
            controllerParamName = TypeCast.toString(((e != null) ? e.getValue() : ""));
            log.info("Controller param name is -> " + controllerParamName);
            e = eI.getChild("session-name");
            sessionName = TypeCast.toString(((e != null) ? e.getValue() : ""));
            log.info("Sessiomn name is -> " + sessionName);
            e = eI.getChild("db-protocol");
            dbProtocol = DBProtocolType.valueOf(TypeCast.toString(((e != null) ? e.getValue() : "")));
            log.info("Data base protocol is -> " + dbProtocol);
            dbVersion = e.getAttribute("version").getValue();
            log.info("Data base version is -> " + dbVersion);
            e = eI.getChild("date-format");
            dateFormat = (e == null) ? "dd/mm/yyyy" : e.getValue();
            log.info("Date format -> " + dateFormat);
            wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            if (wac == null) {
                log.warn("Web application context Spring is not initialize.");
            }
            log.info("Web application context -> " + wac.getId());
            errBuf = new StringBuilder();
            errBuf.append("\n*************************\n");
            errBuf.append("* Bean definition names *\n");
            errBuf.append("*************************\n");
            errBuf.append(TypeCast.toString(wac.getBeanDefinitionNames(), ", ")).append("\n");
            log.info(errBuf.toString());
            errBuf = new StringBuilder();

            errBuf.append("\n******************\n");
            errBuf.append("* Business logic *\n");
            errBuf.append("******************\n");
            mBLs = new HashMap<String, String>();
            Iterator iRootBLs = fConfig.getChildren("business-logic").iterator();
            String name = null;
            String schema = null;
            String rootPackage = null;
            String blPackage = null;
            String idBL = null;
            String className = null;
            Element eRootBL = null;
            Element eBL = null;           
            Iterator iBLs = null;
            while (iRootBLs.hasNext()) {
                eRootBL = (Element) iRootBLs.next();
                rootPackage = ((eRootBL.getAttribute("package") != null) ? eRootBL.getAttribute("package").getValue() : null);
                iBLs = eRootBL.getChildren().iterator();
                errBuf.append("Root package: ").append(rootPackage).append("\n");
                while (iBLs.hasNext()) {
                    eBL = (Element) iBLs.next();
                    blPackage = ((eBL.getAttribute("package") != null) ? eBL.getAttribute("package").getValue() : null);
                    schema = ((eBL.getAttribute("schema") != null) ? eBL.getAttribute("schema").getValue() : null);
                    name = ((eBL.getValue() != null) ? eBL.getValue() : "");
                    idBL = ((schema != null) ? schema : "") + name;
                    className = ((blPackage != null) ? blPackage + "." : ((rootPackage != null) ? rootPackage + "." : "")) + idBL + "BL";
                    errBuf.append("ID: ").append(idBL);
                    errBuf.append((blPackage != null) ? ", Package: " + blPackage : "");
                    errBuf.append(", Name: ").append(className).append("\n");
                    if (mBLs.containsKey(idBL)) {
                        log.error("Error: Duplicate business logic " + idBL + ".");
                        throw new CDCException("Error: Duplicate business logic " + idBL + ".");
                    }
                    mBLs.put(idBL, className);
                }
            }
            log.info(errBuf.toString());
            log.info("Cliser is started...");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        rw = null;
        doAction(request, response);
    }

    /**
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        rw = null;
        doAction(request, response);
    }

    /**
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    private void doAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idBL = null;
        String className = null;
        String methodName = null;
        rw = null;
        try {
            callback = request.getParameter("callback");
            idBL = request.getServletPath().replaceAll(".cs", "").replaceFirst("/", "");
            if (!mBLs.containsKey(idBL)) {              
                throw new CDCException("Business logic " + idBL + " not found.");
            }
            className = mBLs.get(idBL);
            BL _cliser = (BL) Class.forName(className).newInstance();        
            _cliser.setResquestWrapper(request);
            _cliser.setResponseWrapper(response);
            _cliser.setWac(wac);            
            _cliser.setDBProtocol(dbProtocol);
            _cliser.setDateFormat(dateFormat);
            _cliser.setDBVersion(dbVersion);
            _cliser.setSessionName(sessionName);
            methodName = TypeCast.toString(_cliser.form(controllerParamName));
            if (methodName == null) {              
                throw new CDCException("Could not find the controller for the parameter name \"" + controllerParamName + "\".");
            }
            Method _method = _cliser.getClass().getMethod(methodName);
            _method.invoke(_cliser);
        } catch (InstantiationException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (IllegalAccessException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (InvocationTargetException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (NoSuchMethodException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage("Controller \"" + methodName + "\" not found in businnes logic \"" + className + "\".", false);
        } catch (SecurityException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (ClassNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage("Class businnes logic \"" + className + "\" not found.", false);
        } catch (CDCException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        }
        if (rw != null) {
            try {
                rw.commit();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}

/**
 * Author: Alexander Villalobos Yadró E-Mail: avyadro@yahoo.com.mx Created on
 * Apr 20, 2009, 05:37:24 PM Place: Monterrey, Nuevo León, México. Company:
 * Codicentro Web: http://www.codicentro.com Class Name: CliserServlet.java
 * Purpose: Revisions: Ver Date Author Description --------- ---------------
 * ----------------------------------- ------------------------------------
 * 1.0.0 Apr 20, 2009 Alexander Villalobos Yadró New class.
 *
 */
package com.codicentro.cliser;

import com.codicentro.core.CDCException;
import com.codicentro.core.TypeCast;
import com.codicentro.core.Types.DBProtocolType;
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
    private Map<String, BLClass> mBLs = null;
    private StringBuilder errBuf = null;
    private String IU = "getIU";

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
            /**
             * * CONTROLLER PARAM NAME **
             */
            Element e = eI.getChild("controller-param-name");
            controllerParamName = TypeCast.toString(((e != null) ? e.getValue() : ""));
            log.info("Controller param name is -> " + controllerParamName);
            /**
             * * SESSION NAME **
             */
            e = eI.getChild("session-name");
            sessionName = TypeCast.toString(((e != null) ? e.getValue() : ""));
            log.info("Sessiomn name is -> " + sessionName);
            /**
             * * IU NAME **
             */
            e = eI.getChild("iu");
            IU = TypeCast.toString(((e != null) ? e.getValue() : ""));
            log.info("IU Identifier is -> " + IU);
            /**
             * * DATA BASE PROTOCOL **
             */
            e = eI.getChild("db-protocol");
            dbProtocol = DBProtocolType.valueOf(TypeCast.toString(((e != null) ? e.getValue() : "")));
            log.info("Data base protocol is -> " + dbProtocol);
            /**
             * * DATA BASE VERSION **
             */
            dbVersion = e.getAttribute("version").getValue();
            log.info("Data base version is -> " + dbVersion);
            /**
             * * DEFAULT DATE FORMAT **
             */
            e = eI.getChild("date-format");
            dateFormat = (e == null) ? "dd/mm/yyyy" : e.getValue();
            log.info("Date format -> " + dateFormat);
            /**
             * * WEB APPLICATION CONTEXT **
             */
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
            mBLs = new HashMap<String, BLClass>();
            Iterator iRootBLs = fConfig.getChildren("business-logic").iterator();
            while (iRootBLs.hasNext()) {
                Element eRootBL = (Element) iRootBLs.next();
                String rootPackage = ((eRootBL.getAttribute("package") != null) ? eRootBL.getAttribute("package").getValue() : null);
                Iterator iBLs = eRootBL.getChildren().iterator();
                errBuf.append("Root package: ").append(rootPackage).append("\n");
                while (iBLs.hasNext()) {
                    Element eBL = (Element) iBLs.next();
                    String blPackage = ((eBL.getAttribute("package") != null) ? eBL.getAttribute("package").getValue() : null);
                    String schema = ((eBL.getAttribute("schema") != null) ? eBL.getAttribute("schema").getValue() : null);
                    boolean publicAccess = ((eBL.getAttribute("publicAccess") != null) ? TypeCast.toBoolean(eBL.getAttribute("publicAccess").getValue()) : false);
                    String name = ((eBL.getValue() != null) ? eBL.getValue() : "");
                    String idBL = ((schema != null) ? schema : "") + name;
                    BLClass blClass = new BLClass(((blPackage != null) ? blPackage + "." : ((rootPackage != null) ? rootPackage + "." : "")) + idBL + "BL");
                    errBuf.append("ID: ").append(idBL);
                    errBuf.append((blPackage != null) ? ", Package: " + blPackage : "");
                    errBuf.append(", Name: ").append(blClass).append("\n");
                    if (mBLs.containsKey(idBL)) {
                        log.error("Error: Duplicate business logic " + idBL + ".");
                        throw new CDCException("Error: Duplicate business logic " + idBL + ".");
                    }
                    blClass.setPublicAccess(publicAccess);
                    mBLs.put(idBL, blClass);
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
        BLClass blClass = null;
        String methodName = null;
        rw = null;
        try {
            callback = request.getParameter("callback");
            idBL = request.getServletPath().replaceAll(".cs", "").replaceFirst("/", "");
            if (!mBLs.containsKey(idBL)) {
                throw new CDCException("Business logic " + idBL + " not found.");
            }
            blClass = mBLs.get(idBL);
            BL _cliser = (BL) Class.forName(blClass.getName()).newInstance();
            _cliser.setResquestWrapper(request);
            _cliser.setResponseWrapper(response);
            _cliser.setWac(wac);
            _cliser.setDBProtocol(dbProtocol);
            _cliser.setDateFormat(dateFormat);
            _cliser.setDBVersion(dbVersion);
            _cliser.setSessionName(sessionName);
            _cliser.setNameIU(IU);
            if (!blClass.isPublicAccess()) {
                _cliser.checkSession();
            }
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
            rw.setMessage("Controller \"" + methodName + "\" not found in businnes logic \"" + blClass.getName() + "\".", false);
        } catch (SecurityException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage(ex);
        } catch (ClassNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            rw = new ResponseWrapper(response, callback);
            rw.setMessage("Class businnes logic \"" + blClass.getName() + "\" not found.", false);
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

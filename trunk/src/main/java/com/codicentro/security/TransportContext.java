/**
 * Author: Alexander Villalobos Yadro
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Dec 23, 2008, 10:27:26 AM
 * Place: Monterrey, Nuevo Leon, Mexico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: TransportContext.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       Dec 23, 2008           Alexander Villalobos Yadr�      New class.
 **/
package com.codicentro.security;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class TransportContext {

    public TransportContext() {
    }

    public TransportContext(HttpServletRequest request, Object value) {
        request.getSession().getServletContext().setAttribute(request.getSession().getId(), value);
    }

    public Object getTransportAttribute(HttpServletRequest request, String contextName, String idSession) {
        ServletContext sc = request.getSession().getServletContext().getContext(contextName);
        Object res = sc.getAttribute(idSession);
        sc.removeAttribute(idSession);
        sc = null;
        return res;
    }
}

/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on May 22, 2008, 10:27:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: CDCException.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        May 22, 2008      Alexander Villalobos Yadr�           1. New class.
 * 1.0.1      Jul 16, 2008      Alexander Villalobos Yadr�           2. Add property code  
 **/
package com.codicentro.utils;

import java.io.Serializable;
import java.sql.SQLException;

public class CDCException extends Exception implements Serializable {

    private String frontEndMessage;
    private String backEndMessage;
    private int errorCode;

    public CDCException(String message) {
        super(message);
        frontEndMessage = message;
        backEndMessage = message;
        errorCode = -1;
    }

    public CDCException(String frontEndMessage, String backEndMessage) {
        super(frontEndMessage);
        this.frontEndMessage = frontEndMessage;
        this.backEndMessage = frontEndMessage + "\n" + backEndMessage;
        errorCode = -1;
    }

    public CDCException(Exception e) {
        super(e);
        frontEndMessage = e.getMessage();
        backEndMessage = (e.getCause() != null) ? e.getCause().toString() : null;
        if (e instanceof SQLException) {
            errorCode = ((SQLException) e).getErrorCode();
        } else {
            errorCode = -1;
        }
    }
    /*
    public CDCException(SQLException e) {
    super(e);
    frontEndMessage = e.getMessage();
    backEndMessage = (e.getCause() != null) ? e.getCause().toString() : null;
    errorCode = e.getErrorCode();
    }
     */

    public String getFrontEndMessage() {
        return frontEndMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

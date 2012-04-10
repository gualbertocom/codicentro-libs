/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on May 08, 2009, 10:27:26 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: LoggerDebug.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       May 08, 2009           Alexander Villalobos Yadró      New class.
 **/
package com.codicentro.utils;

import com.codicentro.core.CDCException;
import com.codicentro.core.Types.LoggerOutType;
import com.codicentro.core.model.ParamsDB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Deprecated
public class LoggerDebug {

    private PrintStream console = null;
    private PrintStream file = null;
    private String loggerName = null;
    private String loggerPath = null;
    private String loggerFileName = null;
    private boolean loggerAddCurrentFileName = true;
    private StringBuffer longMessage = null;

    /**
     * 
     * @param name
     * @param path
     * @param fileName
     * @param lot
     */
    public LoggerDebug(String name, String path, String fileName, LoggerOutType lot, boolean addCurrentFileName) {
        loggerName = name;
        loggerPath = (path != null) ? path : System.getProperty("user.home") + "/";
        loggerFileName = (fileName != null) ? fileName : "cliser";
        loggerAddCurrentFileName = addCurrentFileName;
        if (lot == LoggerOutType.CONSOLE) {
            console = System.err;
        } else if (lot == LoggerOutType.FILE) {
            file = createFile();
        } else {
            console = System.err;
            file = createFile();
        }

    }

    /**
     *
     * @return
     */
    private Date current() {
        //    DateFormat dateFormat = new SimpleDateFormat("MM yyyyMMdd");
        return new Date();
    }

    /**
     *
     * @return
     */
    private PrintStream createFile() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            loggerFileName = loggerPath + loggerFileName + ((loggerAddCurrentFileName) ? dateFormat.format(new Date()) : "") + ".log";
            return new PrintStream(new FileOutputStream(new File(loggerFileName), true), true);
        } catch (Exception e) {
            //console.println(e);
            return null;
        }
    }

    /**
     *
     * @param msg
     */
    public void start(String msg) {
        log(msg);
    }

    private void startFinish(String sf, String msg, StackTraceElement e[]) {
        msg += " -> " + sf + " controller <" + e[1].getMethodName() + "> in business logic <" + e[1].getClassName() + ">";
        log(msg);
    }

    public void start(String msg, StackTraceElement e[]) {
        startFinish("Start", msg, e);
    }

    /**
     *
     * @param msg
     */
    public void finish(String msg) {
        log(msg);
    }

    public void finish(String msg, StackTraceElement e[]) {
        startFinish("Finish", msg, e);
    }

    /**
     *
     * @param msg
     */
    public void log(String msg) {
        if (console != null) {
            console.println(current() + ":" + msg);
        }
        if (file != null) {
            file.println(current() + ":" + msg);
        }
    }

    /**
     *
     * @param msg
     * @param ex
     */
    public void log(String msg, Exception ex) {
        if (console != null) {
            console.println(current() + ":" + msg);
            ex.printStackTrace(console);
        }
        if (file != null) {
            file.println(current() + ":" + msg);
            ex.printStackTrace(file);
        }
    }

    /**
     *
     * @param ex
     */
    public void log(Exception ex) {
        if (console != null) {
            ex.printStackTrace(console);
        }
        if (file != null) {
            ex.printStackTrace(file);
        }
    }

    public String log(ParamsDB param, Exception ex) {
        if (console != null) {
            console.println(param.toString());
            ex.printStackTrace(console);
        }
        if (file != null) {
            file.println(param.toString());
            ex.printStackTrace(file);
        }
        if (ex instanceof CDCException) {
            return ((CDCException) ex).getFrontEndMessage();
        } else {
            return ex.getMessage();
        }
    }
}

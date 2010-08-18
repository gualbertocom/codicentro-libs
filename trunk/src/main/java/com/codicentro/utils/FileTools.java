/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Jun 11, 2006, 12:07:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: FileTools.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Jun 11, 2006           Alexander Villalobos Yadr�           1. New class.
 **/
package com.codicentro.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class FileTools {

    /**
     *
     * @param req
     * @return
     */
    public static String getRootPath(HttpServletRequest req) {
        File p = new File("");

        return req.getSession().getServletContext().getRealPath("");
    }

    /**
     *
     * @return
     */
    public static String getRootPath() {
        String res = "";
        try {
            File p = new File(".");
            res = p.getCanonicalPath();
        } catch (IOException ex) {
            
            //Logs.setLogs("codicentro-tools", "Class:FileTools.java\nMethods:getRootPath()\n" + ex.getMessage());
        }

        return res;
    }

    /**
     *
     * @param directory
     * @return
     */
    public static boolean deleteAllFiles(String directory) {
        File f = new File(directory);
        if (!(f.exists())) {
            f.mkdirs();
        }

        String[] dirs = f.list();
        if ((dirs != null) && (dirs.length > 0)) {
            for (int i = 0; i < dirs.length; ++i) {
                f = new File(directory + File.separator + dirs[i]);
                f.delete();
            }
        }
        return true;
    }

    /**
     *
     * @param response
     * @param book
     * @param filename
     * @throws java.lang.Exception
     */
    public static void exportXLS(HttpServletResponse response, HSSFWorkbook book, String filename) throws Exception {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-disposition", "attachment;filename=\"" + filename + ".xls\"");
        response.setHeader("Pragma", "public");
        response.setContentType("application/vnd.ms-excel");
        OutputStream out = response.getOutputStream();
        book.write(out);
        out.close();
    }
}
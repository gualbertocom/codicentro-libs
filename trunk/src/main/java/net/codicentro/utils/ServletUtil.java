/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Sep 28, 2012 at 11:56:38 AM
 * @place: Toluca, Estado de México, México
 * @company: Codicentro©
 * @web: http://www.codicentro.net
 * @className: ServletUtil.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package net.codicentro.utils;

import net.codicentro.core.CDCException;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtil {

    public static void doDownload(
            HttpServletRequest request,
            HttpServletResponse response,
            String filename,
            String originalFilename) throws CDCException, IOException {
        File f = new File(filename);
        if (!f.exists()) {
            throw new CDCException("File not found: " + filename + " (No such file or directory)");
        }
        int length = 0;
        ServletOutputStream op = response.getOutputStream();
        ServletContext context = request.getSession().getServletContext();
        String mimetype = context.getMimeType(filename);
        response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
        response.setContentLength((int) f.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + originalFilename + "\"");
        Cookie ck = new Cookie("fileDownloadStatus", "complete");
        ck.setSecure(false);
        ck.setPath("/");        
        response.addCookie(ck);
        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));//
        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            op.write(bbuf, 0, length);
        }
        in.close();
        op.flush();
        op.close();
    }
}

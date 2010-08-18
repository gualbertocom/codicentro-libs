/**
 * Author: Alexander Villalobos Yadr
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 09, 2009, 03:08:26 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Encryption.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 09, 2006           Alexander Villalobos Yadró           1. New class.
 **/
package com.codicentro.security;

import com.codicentro.utils.TypeCast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    private String data;

    public Encryption(String data) {
        this.data = data;
    }

    private String EXEC(String algorithm) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(data.getBytes());
        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return TypeCast.toString(md.digest());
    }

    public String SHA1() {
        return EXEC("SHA-1");
    }

    public String MD5() {
        return EXEC("MD5");
    }
}

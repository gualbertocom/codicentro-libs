/**
 * Author: Alexander Villalobos Yadr
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Mar 09, 2009, 03:08:26 AM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: FileTools.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Mar 09, 2006           Alexander Villalobos Yadró           1. New class.
 **/
package com.codicentro.utils;

import com.codicentro.security.Encryption;
import com.codicentro.utils.Types.EncrypType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {

    /**
     *
     * @return
     */
    public static String makeId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(new Date());
    }

    /**
     *
     * @return
     */
    public static String makeId(EncrypType et) {
        String result = null;
        Encryption encryption = null;
        Random random = new Random(1000000);
        switch (et) { 
            case SHA1:
                encryption = new Encryption(makeId() + TypeCast.toString(random.nextInt()));
                result = encryption.SHA1();
                break;
            default:
                result = makeId() + TypeCast.toString(random.nextInt());
                break;
        }
        return result;
    }
}

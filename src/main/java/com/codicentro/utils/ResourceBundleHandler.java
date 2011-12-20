/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on May 19, 2008, 10:27:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: ResourceBundleHandler.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        May 19, 2008           Alexander Villalobos Yadr�           1. New class.
 **/
package com.codicentro.utils;

import java.util.ResourceBundle;

public class ResourceBundleHandler {

    private String source = null;
    private ResourceBundle resourceBundle = null;

    public ResourceBundleHandler(String source)
            throws com.codicentro.utils.CDCException {
        this.source = source;
        try {
            this.resourceBundle = ResourceBundle.getBundle(source);
        } catch (Exception e) {
            throw new com.codicentro.utils.CDCException("File " + source + " is not found.");
        }
    }

    public String getValue(String key) throws com.codicentro.utils.CDCException {
        String result = null;
        try {
            result = resourceBundle.getString(key);
        } catch (Exception ex) {
            throw new com.codicentro.utils.CDCException("The key " + key + " not found in the file config " + source + ".properties");
        }

        return result;
    }

    public String getValue(String key, String[] params)
            throws com.codicentro.utils.CDCException {
        String result = null;
        try {
            result = getValue(key);
            for (int i = 0; i < params.length; ++i) {
                result = result.replaceAll("\\{" + i + "\\}", params[i]);
            }
        } catch (Exception ex) {
            throw new com.codicentro.utils.CDCException("The key " + key + " not found in the file config " + source + ".properties");
        }

        return result;
    }
}
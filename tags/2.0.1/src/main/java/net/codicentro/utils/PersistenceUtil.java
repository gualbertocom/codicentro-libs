/*
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Jan 25, 2012 at 1:06:21 PM
 * @place: Toluca, Estado de México, México
 * @company: Codicentro©
 * @web: http://www.codicentro.net
 * @className: PersistenceUtil.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0       May 08, 2009           Alexander Villalobos Yadró      New class.
 **/
package net.codicentro.utils;

public class PersistenceUtil {

    /**
     * javax.persistence.Column
     *
     * @param clazz
     * @param name
     * @return
     */
    public static String findColumnNameByFieldName(final Class<?> clazz, final String name) {
        String columnName = null;
        Integer idxField = 0;
        while (columnName == null && idxField < clazz.getDeclaredFields().length) {
            if (clazz.getDeclaredFields()[idxField].getName().equals(name)) {
                columnName = clazz.getDeclaredFields()[idxField].getAnnotation(javax.persistence.Column.class).name();
            }
            idxField++;
        }
        return columnName;
    }
}

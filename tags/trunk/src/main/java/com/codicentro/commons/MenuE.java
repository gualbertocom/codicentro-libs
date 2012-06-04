/*
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 4/06/2009, 03:31:21 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: MenuE.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0      4/06/2009           Alexander Villalobos Yadró           New class.
 **/
package com.codicentro.commons;

public class MenuE {

    private int idMenu = 0;
    private int idParent = 0;
    private String name = null;

    public MenuE(int idMenu, int idParent, String name) {
        this.idMenu = idMenu;
        this.idParent = idParent;
        this.name = name;
    }

    /**
     * @return the idMenu
     */
    public int getIdMenu() {
        return idMenu;
    }

    /**
     * @param idMenu the idMenu to set
     */
    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    /**
     * @return the idParent
     */
    public int getIdParent() {
        return idParent;
    }

    /**
     * @param idParent the idParent to set
     */
    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}

/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 22/07/2009, 03:24:20 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: TreeE.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0      22/07/2009           Alexander Villalobos Yadró           New class.
 **/
package com.codicentro.commons;

public class TreeE {

    private int idTree = 0;
    private int idParent = 0;
    private String name = null;

    public TreeE(int idTree, int idParent, String name) {
        this.idTree = idTree;
        this.idParent = idParent;
        this.name = name;
    }

    /**
     * @return the idTree
     */
    public int getIdTree() {
        return idTree;
    }

    /**
     * @param idTree the idTree to set
     */
    public void setIdTree(int idTree) {
        this.idTree = idTree;
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

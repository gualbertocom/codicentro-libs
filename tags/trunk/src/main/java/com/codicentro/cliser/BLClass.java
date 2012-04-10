/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: 25/01/2011 at 05:57:33 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: BLClass.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.cliser;

public class BLClass {

    private String name;
    private boolean publicAccess = false;

    public BLClass(String name) {
        this.name = name;
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

    /**
     * @return the publicAccess
     */
    public boolean isPublicAccess() {
        return publicAccess;
    }

    /**
     * @param publicAccess the publicAccess to set
     */
    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

}

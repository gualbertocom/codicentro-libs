package com.codicentro.security;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionEntityBase {

    private Object IU;
    public SessionEntityBase() {

    }

    /**
     * 
     * @param IU
     */
    public SessionEntityBase(Object IU) {
        this.IU = IU;
    }

    /**
     * 
     * @return
     */
    public Object getIU() {
        return IU;
    }

    public String toJSON() {
        String rs = "";
        String mn = null;// Method name
        Object v = null;
        for (int i = 0; i < this.getClass().getMethods().length; i++) {
            mn = this.getClass().getMethods()[i].getName();
            if ((mn.startsWith("get")) && (!mn.startsWith("getClass"))) {
                try {
                    v = this.getClass().getMethods()[i].invoke(this);
                    if (v instanceof String) {
                        v = "\"" + v + "\"";
                    }
                    rs += ((rs.equals("")) ? "" : ",") + this.getClass().getMethods()[i].getName().replace("get", "") + ":" + v;
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(SessionEntityBase.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(SessionEntityBase.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(SessionEntityBase.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return rs;
    }
}

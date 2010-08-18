package com.codicentro.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionEntityBase {

    private Logger log = LoggerFactory.getLogger(Encryption.class);
    private Object IU;

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

    /**
     * 
     * @return
     */
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
                } catch (Exception ex) {
                    log.error(ex.getCause().getMessage(), ex);
                }
            }
        }
        return rs;
    }
}

/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 22/07/2009, 03:24:13 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Tree.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0      22/07/2009           Alexander Villalobos Yadró           New class.
 **/
package com.codicentro.commons;

import com.codicentro.core.CDCException;
import com.codicentro.core.model.Table;
import java.io.Serializable;

public class Tree implements Serializable {

    private Table tree = null;

    public Tree(Table tree) {
        this.tree = tree;
    }

    public String renderTo() throws CDCException {
        return renderTo(tree);
    }

    private String renderTo(Table tree) throws CDCException {

        StringBuffer sb = new StringBuffer();
        StringBuffer item = null;
        String cc = "";////Contains childs
        int idx = 0;
        int ln = 0;
        int od = 0;
        // ArrayList  jsonData = TypeCast.tableToJSON(tree);
        while (tree.hasNext()) {
            tree.next();
            item = new StringBuffer();
            item.append("{");
            item.append("mid:\"" + tree.getStringValue("ID_MENU") + "\",");
            item.append("id:\"" + tree.getStringValue("ID_MENU") + "\",");
            item.append("text:lng.menu.caption." + tree.getStringValue("NAME") + ",");
            item.append("iconCls:\"" + tree.getStringValue("ICON") + "\",");
            item.append("checked:" + tree.getStringValue("ALLOW") + ",");
            item.append("data:{" + tree.getJsonValue() + "}");
            item.append("}");
            if (tree.getStringValue("ID_MENU").equals(tree.getStringValue("ID_PARENT"))) {
                if (sb.toString().equals("")) {
                    sb.append(item);
                } else {
                    sb.append("," + item);
                }
                tree.removeRow();
            } else {
                idx = sb.indexOf("{mid:\"" + tree.getStringValue("ID_PARENT") + "\",");
                ln = ("{mid:\"" + tree.getStringValue("ID_PARENT") + "\",").length() - 1;
                if (idx != -1) {
                    if (sb.indexOf("{mid:\"" + tree.getStringValue("ID_PARENT") + "\",children:") == -1) {
                        sb.insert(idx + ln, ",children:[" + item + "]");
                    } else {
                        sb.insert(idx + ln + 11, item + ",");
                    }
                    tree.removeRow();
                }
            }

        }
        return "[" + sb.toString() + "]";
    }
}

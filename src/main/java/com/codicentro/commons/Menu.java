/**
 * Author: Alexander Villalobos Yadró
 * E-Mail: avyadro@yahoo.com.mx
 * Created on 3/06/2009, 02:42:14 PM
 * Place: Monterrey, Nuevo León, México.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: Menu.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0.0      3/06/2009           Alexander Villalobos Yadró           New class.
 **/
package com.codicentro.commons;

import com.codicentro.model.Table;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import java.io.Serializable;

public class Menu implements Serializable {

    private Table menu = null;

    public Menu(Table menu) {
        this.menu = menu;
    }

    public String renderTo() throws CDCException {
        return renderTo(menu);
    }

    private String renderTo(Table menu) throws CDCException {
        StringBuilder sb = new StringBuilder();
        StringBuffer item = null;
        String cc = "";////Contains childs
        int idx = 0;
        int ln = 0;
        String scriptName = "";
        String scriptPath = "";
        String params = "";
        String script = "";
        while (menu.hasNext()) {
            menu.next();
            item = new StringBuffer();
            item.append("{");
            item.append("mid:\"").append(menu.getStringValue("ID_MENU")).append("\",");
            item.append("id:\"").append(menu.getStringValue("ID_MENU")).append("\",");
            item.append("text:lng.)").append(menu.getStringValue("ID_APPLICATION")).append("menu.caption.").append(menu.getStringValue("NAME")).append(",");
            item.append("iconCls:\"").append(menu.getStringValue("ICON")).append("\"");
            scriptName = menu.getStringValue("SCRIPT_NAME", "");
            scriptPath = menu.getStringValue("SCRIPT_PATH", "");
            params = menu.getStringValue("PARAMS", "");
            script = menu.getStringValue("SCRIPT", "");
            script = script.replaceAll("\n", "");
            if ((!scriptName.equals("")) || (!scriptPath.equals("")) || (!script.equals(""))) {
                item.append(",handler:function(){");
                item.append("ctrlWaitingStart();");
                item.append(script);
                if (!scriptName.equals("")) {
                    item.append("new File({url:\"").append(scriptPath).append(scriptName).append(".js\",method:\"include\"});");
                    item.append(TypeCast.toFirtLowerCase(scriptName)).append("=new ").append(scriptName).append("(").append(params).append(");");
                }
                item.append("}");
            }
            item.append("}");
            if (menu.getStringValue("ID_MENU", "").equals(menu.getStringValue("ID_PARENT"))) {
                if (sb.toString().equals("")) {
                    sb.append("[").append(item).append("]");
                } else {
                    sb.append(",[").append(item).append("]");
                }
                menu.removeRow();
            } else {
                idx = sb.indexOf("{mid:\"" + menu.getStringValue("ID_PARENT") + "\",");
                ln = ("{mid:\"" + menu.getStringValue("ID_PARENT") + "\",").length() - 1;
                if (idx != -1) {
                    if (sb.indexOf("{mid:\"" + menu.getStringValue("ID_PARENT") + "\",menu:") == -1) {
                        sb.insert(idx + ln, ",menu:{items:[" + item + "]}");
                    } else {
                        sb.insert(idx + ln + 14, item + ",");
                    }
                    menu.removeRow();
                }
            }

        }
        return "[" + sb.toString() + "]";
    }
}

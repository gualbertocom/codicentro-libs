## Example BL ##

```
package com.codicentro.example.catalogues.bl;

import com.codicentro.example.catalogues.pojos.State;
import com.codicentro.cliser.BL;
import com.codicentro.utils.CDCException;
import com.codicentro.utils.TypeCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateBL extends BL {

    private Logger log = LoggerFactory.getLogger(StateBL.class);
    /**
     * Search 
     */
    public void ls() throws CDCException {
        try {
            checkSession();
            log.info("[" + getIU() + "]", Thread.currentThread().getStackTrace());
            entity(State.class, "States");
            include("idState");
            include("description");
            if (shortValue("idState") != null) {
                EQ("idState", integerValue("idState"));
            }
            if (!TypeCast.isNullOrEmpy(stringValue("description"))) {
                LK("description", "description", "%?%", true);
            }
            if (!TypeCast.isNullOrEmpy(stringValue("search"))) {
                if (integerValue("search") != null) {
                    EQ("idState", integerValue("search"));
                } else {
                    LK("description", "search", "%?%", true);
                }
            }
            find();
            information("lng.msg.information.success");
            log.info("[" + getIU() + "]", Thread.currentThread().getStackTrace());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            error(ex);
        } finally {
            commit();
        }
    }

    /**
     * Save or update state
     * @throws CDCException
     */
    public void sv() throws CDCException {
        try {
            checkSession();
            log.info("[" + getIU() + "]", Thread.currentThread().getStackTrace());
            State state = new State(integerValue("idState"));
            if (stringValue("description") == null) {
                throw new CDCException("lng.msg.error.example.state.required.description");
            }
            group.setDescription(stringValue("description"));
            save(state);
            information("lng.msg.information.success");
            log.info("[" + getIU() + "]", Thread.currentThread().getStackTrace());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            error(ex);
        } finally {
            commit();
        }
    }

    /**
     * Remove exist state
     * @throws CDCException
     */
    public void rm() throws CDCException {
        try {
            checkSession();
            log.info("[" + getIU() + "]", Thread.currentThread().getStackTrace());
            entity(State.class);
            remove(integerValue("idState"));
            information("lng.msg.information.success");
            log.info("[" + getIU() + "]", Thread.currentThread().getStackTrace());
        } catch (Exception ex) {
            error(ex);
        } finally {
            commit();
        }
    }
}
```

## Example Cliser Config ##

/WEB-INF/cliser-config.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<cliser-config>
   <cliser-init>
        <tracert-logs id="EXAMPLE 1">
            <active>true</active>
            <out-type>FILE</out-type>
            <filename>log-filename</filename>
        </tracert-logs>
        <controller-param-name>ctrl</controller-param-name>        
        <session-name>EXAMPLE-1-SESSION-NAME</session-name>
        <iu>getUserName</iu>
        <db-protocol version="1.0">DB-PROVIDER</db-protocol>
        <date-format>dd/MM/yyyy</date-format>
        <resource-locations>
            <value>resource-example1</value>
        </resource-locations>
    </cliser-init>
    <!--- CATALOGUES -->
    <business-logic package="com.codicentro.example.catalogues">
        <bl>State</bl>
        <bl publicAccess="true">City</bl>
        <bl package="com.codicentro.example.catalogues.others">Year</bl>
    </business-logic>   
</cliser-config>
```
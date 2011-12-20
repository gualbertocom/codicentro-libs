/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Oct 19, 2010 at 11:58:27 AM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: HqlToSqlTranslator.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.utils;

import java.util.Collections;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;

public class HqlToSqlTranslator {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public String toSql(String hqlQueryText) {
        if (hqlQueryText != null && hqlQueryText.trim().length() > 0) {
            final QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
            final SessionFactoryImplementor factory =
                    (SessionFactoryImplementor) sessionFactory;
            final QueryTranslator translator = translatorFactory.createQueryTranslator(
                    hqlQueryText,
                    hqlQueryText,
                    Collections.EMPTY_MAP, factory);
            translator.compile(Collections.EMPTY_MAP, false);
            return translator.getSQLString();
        }
        return null;
    }
}

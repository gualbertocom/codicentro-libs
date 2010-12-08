/**
 * Author: Alexander Villalobos Yadr�
 * E-Mail: avyadro@yahoo.com.mx
 * Created on Jun 11, 2006, 12:07:26 AM
 * Place: Quer�taro, Quer�taro, M�xico.
 * Company: Codicentro
 * Web: http://www.codicentro.com
 * Class Name: FileTools.java
 * Purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 * 1.0        Jun 11, 2006           Alexander Villalobos Yadr�           1. New class.
 **/
package com.codicentro.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class FileTools {

    /**
     *
     * @param req
     * @return
     */
    public static String getRootPath(HttpServletRequest req) {
        File p = new File("");

        return req.getSession().getServletContext().getRealPath("");
    }

    /**
     *
     * @return
     */
    public static String getRootPath() {
        String res = "";
        try {
            File p = new File(".");
            res = p.getCanonicalPath();
        } catch (IOException ex) {
            //Logs.setLogs("codicentro-tools", "Class:FileTools.java\nMethods:getRootPath()\n" + ex.getMessage());
        }

        return res;
    }

    /**
     *
     * @param directory
     * @return
     */
    public static boolean deleteAllFiles(String directory) {
        File f = new File(directory);
        if (!(f.exists())) {
            f.mkdirs();
        }

        String[] dirs = f.list();
        if ((dirs != null) && (dirs.length > 0)) {
            for (int i = 0; i < dirs.length; ++i) {
                f = new File(directory + File.separator + dirs[i]);
                f.delete();
            }
        }
        return true;
    }

    /**
     *
     * @param response
     * @param book
     * @param filename
     * @throws java.lang.Exception
     */
    public static void exportXLS(HttpServletResponse response, HSSFWorkbook book, String filename) throws Exception {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-disposition", "attachment;filename=\"" + filename + ".xls\"");
        response.setHeader("Pragma", "public");
        response.setContentType("application/vnd.ms-excel");
        OutputStream out = response.getOutputStream();
        book.write(out);
        out.close();
    }

    private static void byTemplate(HSSFWorkbook book, HSSFSheet sheet, HSSFRow row, Element column, List<String> idCell, int idxCell, boolean isHeader) throws CDCException {
        idCell.add(column.getAttribute("name").getValue());
        /*** VARS ***/
        HSSFCellStyle style = book.createCellStyle();
        style.setBorderBottom(TypeCast.toShort(1));
        style.setBorderTop(TypeCast.toShort(1));
        style.setBorderLeft(TypeCast.toShort(1));
        style.setBorderRight(TypeCast.toShort(1));

        HSSFFont font = book.createFont();
        Boolean bold = null;
        String alignment = null;
        Short background = null;
        BigDecimal width = null;

        HSSFCell cell = row.createCell(idxCell);
        /*** ALIGMENT ***/
        alignment = (column.getAttribute("alignment") == null) ? null : column.getAttribute("alignment").getValue();
        if (!TypeCast.isNullOrEmpy(alignment)) {
            if (alignment.equals("alLeft")) {
                style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            } else if (alignment.equals("alCenter")) {
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            } else if (alignment.equals("alRight")) {
                style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            }
        }
        /*** BACKGROUND ***/
        background = (column.getAttribute("background") == null) ? null : TypeCast.toShortD(column.getAttribute("background").getValue());
        if (background != null) {
            style.setFillPattern(HSSFPatternFormatting.SOLID_FOREGROUND);
            style.setFillForegroundColor(background);
        }
        /*** WIDTH ***/
        width = (column.getAttribute("width") == null) ? null : TypeCast.toBigDecimal(column.getAttribute("width").getValue());
        if (width != null) {
            width = TypeCast.toBigDecimal(width.doubleValue() * 1308.90);
            sheet.setColumnWidth(idxCell, width.intValue());
        }
        /*** FONT BOLD ***/
        bold = (column.getAttribute("bold") == null) ? TypeCast.toBoolean("false") : TypeCast.toBoolean(column.getAttribute("alignment").getValue());
        if (bold) {
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        }
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(column.getValue());

    }

    public static void exportXLS(String template, String idHeader, List<Map<String, Object>> values, HttpServletResponse response, String filename) throws Exception {
        /*** INITIALIZE TEMPLATE ***/
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File(template));
        Element root = doc.getRootElement();
        /*** INITIALIZED WORKBOOK ***/
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet();
        int idxRow = 0;
        HSSFRow row = sheet.createRow(idxRow);
        Element headers = root.getChild("headers");
        Iterator<Element> iHeader = headers.getChildren("header").iterator();
        Iterator<Element> iColumn = null;
        Element header = null;
        while ((iHeader.hasNext()) && (iColumn == null)) {
            header = iHeader.next();
            if ((header.getAttribute("name") != null) && (header.getAttribute("name").getValue().equals(idHeader))) {
                iColumn = header.getChildren("column").iterator();
            }
        }
        List<String> idCells = new ArrayList<String>();
        /*** HEADERS ***/
        int idxCell = -1;
        while (iColumn.hasNext()) {
            idxCell++;
            byTemplate(book, sheet, row, iColumn.next(), idCells, idxCell, true);
        }
        HSSFCell cell = null;
        Object oValue = null;
        for (Map<String, Object> value : values) {
            idxRow++;
            row = sheet.createRow(idxRow);
            for (idxCell = 0; idxCell < idCells.size(); idxCell++) {
                cell = row.createCell(idxCell);
                oValue = value.get(idCells.get(idxCell));
                if (oValue instanceof java.lang.Number) {
                    cell.setCellValue(TypeCast.toBigDecimal(oValue).doubleValue());
                } else {
                    cell.setCellValue(TypeCast.toString(oValue));
                }
            }
        }
        exportXLS(response, book, filename);

//        FileInputStream fis = new FileInputStream(template);
//        
//       
//        
//        
//        Object value = null;
//        sheet.shiftRows(1, sheet.getLastRowNum(), values.size(), true, true);
//        for (int r = 0; r < values.size(); r++) {
//            row = sheet.createRow(r + 1);
//            for (int c = 0; c < values.get(r).size(); c++) {
//                value = values.get(r).get(c);
//                cell = row.createCell(c);
//                if (value instanceof java.lang.Number) {
//                    cell.setCellValue(TypeCast.toBigDecimal(value).doubleValue());
//                } else {
//                    cell.setCellValue(TypeCast.toString(value));
//                }
//            }
//        }
//        exportXLS(response, book, filename);
    }
}

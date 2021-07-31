package com.ztq.utils.ppt;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author: zhengtianqi
 */
public class PoiUtils {
    /**
     * 标题样式
     */
    public static XSSFCellStyle getStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        /*style.setFillForegroundColor(HSSFCellStyle.ALT_BARS);
        style.setFillBackgroundColor(new XSSFColor(new Color(146,208,80)));
        style.setFillPattern(CellStyle.ALIGN_FILL);*/
        //边框
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor((IndexedColors.BLACK.getIndex()));

        return style;
    }
}

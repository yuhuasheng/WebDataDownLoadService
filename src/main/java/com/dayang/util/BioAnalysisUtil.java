package com.dayang.util;

import com.dayang.domain.BioAnalysisInfo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

public class BioAnalysisUtil {

    /**
     * 添加表头列
     *
     * @param sheet       Sheet表对象
     * @param columnStyle 列样式对象
     */
    public static void addTitleData(HSSFSheet sheet, HSSFCellStyle columnStyle) {
        String[] title = {"编号", "单位名称", "主营", "联系人", "联系电话", "手机", "邮件"};
        HSSFCell cell = null;
        HSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(40);
        for (int j = 0; j < title.length; j++) {
            cell = row.createCell(j);
            cell.setCellValue(new HSSFRichTextString(title[j]));
            cell.setCellStyle(columnStyle);
        }
    }

    /**
     * 设置每一列的宽度
     *
     * @param sheet Excel表对象
     */
    public static void setColumnWidth(HSSFSheet sheet) {
        for (int i = 0; i < 7; i++) {
            if (i == 0) {
                sheet.setColumnWidth(i, 3000);
            } else if (i == 1) {
                sheet.setColumnWidth(i, 10000);
            } else if (i == 2) {
                sheet.setColumnWidth(i, 25000);
            } else if (i == 6) {
                sheet.setColumnWidth(i, 6000);
            } else {
                sheet.setColumnWidth(i, 4500);
            }
        }
    }

    /**
     * 设置列样式
     *
     * @param workbook Excel表对象
     * @return 返回列样式对象
     */
    @SuppressWarnings("resource")
    public static HSSFCellStyle setColumnStyle(HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle1 = workbook.createCellStyle();
        cellStyle1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle1.setWrapText(true);
        return cellStyle1;
    }


    /**
     * 设置内容的样式
     *
     * @param workbook Excel表对象
     * @return 返回内容样式对象
     */
    public static HSSFCellStyle setContentStyle(HSSFWorkbook workbook) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Times New Roman");
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        return cellStyle;
    }


    /**
     * 冻结表头
     *
     * @param sheet 表对象
     */
    public static void freezeHeader(HSSFSheet sheet) {
        // 冻结第一行
        sheet.createFreezePane(0, 1, 0, 1);
    }


    public static void createLine(HSSFSheet sheet, HSSFCellStyle cellStyle, int number, BioAnalysisInfo bioAnalysisInfo) {
        HSSFCell cell = null;
        HSSFRow row = sheet.createRow(number);
        row.setHeightInPoints(30);

        cell = row.createCell(0);
        cell.setCellValue(new HSSFRichTextString(String.valueOf(number)));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue(new HSSFRichTextString(bioAnalysisInfo.getCompany()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue(new HSSFRichTextString(bioAnalysisInfo.getMajor()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue(new HSSFRichTextString(bioAnalysisInfo.getContacts()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue(new HSSFRichTextString(bioAnalysisInfo.getPhone()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue(new HSSFRichTextString(bioAnalysisInfo.getMobilePhone()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue(new HSSFRichTextString(bioAnalysisInfo.getEmail()));
        cell.setCellStyle(cellStyle);
    }
}

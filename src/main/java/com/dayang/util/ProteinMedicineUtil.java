package com.dayang.util;

import com.dayang.domain.BioAnalysisInfo;
import com.dayang.domain.NewCrownMedicineInfo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ProteinMedicineUtil {

    /**
     * 添加表头列
     *
     * @param sheet       Sheet表对象
     * @param columnStyle 列样式对象
     */
    public static void addTitleData(HSSFSheet sheet, HSSFCellStyle columnStyle) {
        String[] title = {"编号", "产品货号", "产品名称", "描述", "表达系统", "种属", "Accession #", "别名",
                "预测分子量", "实际分子量", "纯度", "内毒素", "制剂", "运输方式", "稳定性&储存", "复溶", "应用", "背景"};
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
        for (int i = 0; i < 18; i++) {
            if (i == 0) {
                sheet.setColumnWidth(i, 3000);
            } else if (i == 3) {
                sheet.setColumnWidth(i, 25000);
            } else if (i == 14) {
                sheet.setColumnWidth(i, 15000);
            } else if (i == 17){
                sheet.setColumnWidth(i, 40000);
            } else {
                sheet.setColumnWidth(i, 6000);
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


    public static void createLine(HSSFSheet sheet, HSSFCellStyle cellStyle, int number, NewCrownMedicineInfo newCrownMedicineInfo) {
        HSSFCell cell = null;
        HSSFRow row = sheet.createRow(number);
        row.setHeightInPoints(30);

        cell = row.createCell(0);
        cell.setCellValue(new HSSFRichTextString(String.valueOf(number)));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getProductNumber()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getProductName()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getDescription()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getExpressionSystem()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getSpecies()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getAccession()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getAlias()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getPredictedMolecularWeight()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getActualMolecularWeight()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getPurity()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(11);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getEndotoxin()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(12);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getPreparation()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(13);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getTypeShipping()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(14);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getStabilityStorage()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(15);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getReconstitution()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(16);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getApplication()));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(17);
        cell.setCellValue(new HSSFRichTextString(newCrownMedicineInfo.getBackground()));
        cell.setCellStyle(cellStyle);
    }
}

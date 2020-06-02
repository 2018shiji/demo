package com.example.demo.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;
import java.util.Map;

public class ToExcelUtil {

    public static Workbook createWorkBook(List<Map<String, Object>> list, String[] keys, String[] columnNames){
        //创建excel工作簿
        Workbook workbook = new SXSSFWorkbook(1000);//keep 1k rows in memory, exceeding rows will be flushed to disk

        //创建第一个sheet(页)，并命名
        Sheet sheet = workbook.createSheet(list.get(0).get("sheetName").toString());

        for(int i = 0; i < keys.length; i++){
            sheet.setColumnWidth(i, (short)35.7 * 150);
        }

        Row row = sheet.createRow(0);
        CellStyle cs = workbook.createCellStyle();
        Font f = workbook.createFont();

        return workbook;
    }

}

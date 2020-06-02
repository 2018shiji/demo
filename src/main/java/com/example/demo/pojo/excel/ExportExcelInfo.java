package com.example.demo.pojo.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 导出的Excel列信息
 * @param <TRow>
 */
@Data
public class ExportExcelInfo<TRow> {

    String fieldName;
    List<TRow> dataSource;
    String Sheet = "sheet1";
    boolean IsDisplayColumnName = false;
    List<ExcelColumn<TRow>> listColumn = new ArrayList<>();
    int colulmnType;//列的类型  0：text（文本） 1：columnName

    public void addExcelColumn(ExcelColumn column){
        listColumn.add(column);
    }

    public ExcelColumn<TRow> addExcelColumn(String text, String columnName){
        ExcelColumn<TRow> column = new ExcelColumn<>();
        column.setText(text);
        column.setColumnName(columnName);
        column.setColumnType(EnumExcelColumnType.ColumnType_String);
        listColumn.add(column);
        return column;
    }

    public ExcelColumn<TRow> getErrorColumn(){
        ExcelColumn<TRow> column = new ExcelColumn<>();
        column.setText("异常信息");
        column.setColumnName("errorMsg");
        column.setColumnType(EnumExcelColumnType.ColumnType_String);
        column.setCamelColumnName("errorMsg");
        return column;
    }

}

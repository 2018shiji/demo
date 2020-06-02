package com.example.demo.pojo.excel;

import com.example.demo.FunctionFormatter;
import com.example.demo.util.CamelUtil;
import lombok.Data;

@Data
public class ExcelColumn<T> {
    String text;
    String tableName;
    String columnName;
    String camelColumnName;
    EnumExcelColumnType columnType;
    boolean isNull = true;
    int orderIndex;

    FunctionFormatter<Object, T, Integer, Object> formatter;
    double columnWidth;

    public ExcelColumn(){}

    public ExcelColumn(String columnName, int orderIndex, String tableName,
                       String text, EnumExcelColumnType columnType, boolean isNull){
        this.columnName = columnName;
        this.camelColumnName = CamelUtil.underlineToCamel(this.columnName);
        this.orderIndex = orderIndex;
        this.tableName = tableName;
        this.text = text;
    }

}

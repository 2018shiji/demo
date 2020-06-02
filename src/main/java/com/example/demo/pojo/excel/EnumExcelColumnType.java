package com.example.demo.pojo.excel;

public enum EnumExcelColumnType {
    ColumnType_Double(0),
    ColumnType_Date(1),
    ColumnType_Calendar(2),
    ColumnType_String(3),
    ColumnType_Boolean(4),
    ColumnType_Error(5);

    private int RowId;
    EnumExcelColumnType(int id){
        RowId = id;
    }

    public int GetRowId(){
        return RowId;
    }
}

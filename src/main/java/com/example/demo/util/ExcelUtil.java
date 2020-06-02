package com.example.demo.util;

import com.example.demo.pojo.excel.ExcelColumn;
import com.example.demo.pojo.excel.ExportExcelInfo;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 将导入的文件列信息转换为model
 */
public class ExcelUtil {

    public <TModel> List<TModel> excelToList(
            HttpServletRequest request,
            ExportExcelInfo<TModel> info,
            List<Map<String, Object>> listErrorMap,
            Class<TModel> entityClass) throws Exception {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        String fileName = multiRequest.getFileNames().next();

        MultipartFile multipartFile = multiRequest.getFile(fileName);
        //取得上传文件流
        InputStream in = multipartFile.getInputStream();
        return excelToList(in, info, listErrorMap, entityClass);
    }

    public <TModel> List<TModel> excelToList(
            InputStream in,
            ExportExcelInfo<TModel> info,
            List<Map<String, Object>> listErrorMap,
            Class<TModel> entityClass) throws Exception {
        List<TModel> resultList = new ArrayList<>();
        String sheetName = info.getSheet();
        Workbook wb = WorkbookFactory.create(in);
        Sheet sheet;//获取工作表
        if(wb.getNumberOfSheets() > 1 && !"".equals(sheetName))
            sheet = wb.getSheet(sheetName);
        else
            sheet = wb.getSheetAt(0);
        importSheet(sheet, info, resultList, listErrorMap, entityClass);
        return resultList;
    }

    public <TModel> void importSheet(Sheet productSheet,
                                            ExportExcelInfo<TModel> info,
                                            List<TModel> listTModel,
                                            List<Map<String, Object>> listErrorMap,
                                            Class<TModel> entityClass) throws Exception {
        importSheet(productSheet, info, listTModel, listErrorMap, entityClass, 0);
    }

    /**
     *
     * @param productSheet listTModel
     * @param info
     * @param listTModel 导入集合
     * @param listErrorMap 导入错误集合
     * @param entityClass
     * @param columnRowIndex
     * @param <TModel>
     * @throws Exception
     */
    public <TModel> void importSheet(Sheet productSheet,
                                            ExportExcelInfo<TModel> info,
                                            List<TModel> listTModel,
                                            List<Map<String, Object>> listErrorMap,
                                            Class<TModel> entityClass,
                                            int columnRowIndex) throws Exception {
        int lastCellNum = productSheet.getRow(0).getLastCellNum();//列数量
        Row rowColumn = productSheet.getRow(columnRowIndex);//excel列所在行
        Map<String, Integer> mapExcelColumn = new HashMap<>();//excel 列名字和列索引对应     key:excel列名字 <----> value:excel列索引
        for(int i = 0; i < lastCellNum; i++){
            mapExcelColumn.put(rowColumn.getCell(i).getStringCellValue(), i);
        }
        Map<String, Field> mapField = getMapField(entityClass);
        int LastRowNum = productSheet.getLastRowNum();
        TModel model;
        String errorMsg = null;
        for(int i = columnRowIndex + 1; i <= LastRowNum; i++){
            Row row = productSheet.getRow(i);

            model = entityClass.getConstructor().newInstance();
            if(row != null)
                errorMsg = rowToModel(mapExcelColumn, info, row, model, mapField);//行转model
            else
                errorMsg = null;
            if(!StringUtils.isEmpty(errorMsg)){//转换失败，保存错误行
                Map<String, Object> errorMap = getErrorMap(row, mapExcelColumn);//行转map
                errorMap.put("errorMsg", errorMsg);
                listErrorMap.add(errorMap);
            }else{//转换成功
                listTModel.add(model);
            }
        }
    }

    private <TModel> Map<String, Field> getMapField(Class<TModel> entityClass) {
        Map<String, Field> mapField = new HashMap<>();
        List<Field> listField = ReflectUtil.getListField(entityClass);
        for(Field field : listField){
            mapField.put(field.getName(), field);
        }
        return mapField;
    }

    private Map<String, Object> getErrorMap(Row row, Map<String, Integer> mapExcelColumn){
        Map<String, Object> map = new HashMap();
        for(String key : mapExcelColumn.keySet()){
            Cell cell = row.getCell(mapExcelColumn.get(key));
            map.put(key, getString(cell));
        }
        return map;
    }

    /**
     * 行转model
     */
    protected <TModel> String rowToModel(Map<String, Integer> mapExcelColumn,
                                      ExportExcelInfo<TModel> info,
                                      Row row,
                                      TModel model,
                                      Map<String, Field> mapFiled) throws Exception {
        StringBuilder errorMsg = new StringBuilder();
        boolean hasValue = false;
        for(ExcelColumn column : info.getListColumn()){
            if(mapExcelColumn.containsKey(column.getText())){
                if(!mapFiled.containsKey(column.getCamelColumnName()))
                    throw new Exception(model.getClass().getName() + "不存在字段" + column.getCamelColumnName());

                Field field = mapFiled.get(column.getCamelColumnName());
                String content = getString(row.getCell(mapExcelColumn.get(column.getText())));
                if(!column.isNull()){
                    if(StringUtils.isEmpty(content))
                        errorMsg.append(column.getCamelColumnName() + "不能为空");
                }
                try{
                    if(!StringUtils.isEmpty(content)){
                        hasValue = true;
                        ReflectUtil.setFieldValueByName(field, content.trim(), model);
                    }
                } catch (Exception e) {
                    errorMsg.append(column.getCamelColumnName() + ":" + e.getMessage());
                }
            }
        }

        if(!hasValue)
            errorMsg.append("该表格中有空行");

        return errorMsg.toString();
    }

    private String getString(Cell cell){
        String result = "";
        if(cell == null)
            return result;

        switch(cell.getCellType()){
            case NUMERIC:
                if(HSSFDateUtil.isCellDateFormatted(cell)){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil.getJavaDate(value);
                    result = sdf.format(date);
                } else {
                    Double value = cell.getNumericCellValue();
                    DecimalFormat format = new DecimalFormat();
                    format.applyPattern("###################.###################");
                    result = format.format(value);
                }
                break;
            case STRING:
                result = cell.getRichStringCellValue().toString();
                break;
            case FORMULA://公式型
                //读公式计算值
                try{
                    double value = cell.getNumericCellValue();
                    /**
                     * 如果获取的数值为非法值，则转换为获取字符串
                     */
                    if(Double.isNaN(value)){
                        result = cell.getRichStringCellValue().toString();
                    } else {
                        DecimalFormat format = new DecimalFormat();
                        format.applyPattern("###################.###################");
                        result = format.format(value);
                    }
                } catch (Exception e) {
                    result = cell.getRichStringCellValue().toString();
                }
                break;
            default:
                result = "";
        }
        return result;
    }

}

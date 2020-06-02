package com.example.demo.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReflectUtil {

    public static void setFieldValueByName(Field field, Object fieldValue, Object o) throws Exception{
        field.setAccessible(true);
        //获取字段类型
        Class<?> fieldType = field.getType();
        //根据字段类型给字段赋值
        if (String.class == fieldType) {
            field.set(o, String.valueOf(fieldValue));
        } else if ((Integer.TYPE == fieldType)
                || (Integer.class == fieldType)) {
            field.set(o, Integer.parseInt(fieldValue.toString()));
        } else if ((Boolean.TYPE == fieldType)
                || (Boolean.class == fieldType)) {
            field.set(o, Boolean.valueOf(fieldValue.toString()));
        } else if ((Long.TYPE == fieldType)
                || (Long.class == fieldType)) {
            field.set(o, Long.valueOf(fieldValue.toString()));
        } else if ((Float.TYPE == fieldType)
                || (Float.class == fieldType)) {
            field.set(o, Float.valueOf(fieldValue.toString()));
        } else if ((Short.TYPE == fieldType)
                || (Short.class == fieldType)) {
            field.set(o, Short.valueOf(fieldValue.toString()));
        } else if ((Byte.TYPE == fieldType)
                || (Byte.class == fieldType)) {
            field.set(o, Byte.valueOf(fieldValue.toString()));
        } else if ((Double.TYPE == fieldType)
                || (Double.class == fieldType)) {
            field.set(o, Double.valueOf(fieldValue.toString()));
        } else if (Character.TYPE == fieldType) {
            if ((fieldValue != null) && (fieldValue.toString().length() > 0))
                field.set(o, fieldValue.toString().charAt(0));
        } else if (BigDecimal.class == fieldType) {
            Long v1 = Long.valueOf(fieldValue.toString());
            field.set(o, BigDecimal.valueOf(v1));
        } else if (Date.class == fieldType) {
            field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
        } else if (String[].class == fieldType) {
            String[] array = JsonUtils.fromJson(fieldValue.toString(), String[].class);
            field.set(o, array);
        } else {
            field.set(o, fieldValue);
        }
    }

    public static List<Field> getListField(Class<?> clazz) {
        Field[] selfFields = clazz.getDeclaredFields();
        List<Field> list = new ArrayList<>(Arrays.asList(selfFields));
        /**
         * direct superClass, 但方法进行递归，实现对多重父类的全查找
         */
        Class<?> superClazz = clazz.getSuperclass();
        if(superClazz != null && superClazz != Object.class) {
            List<Field> listSuperField = getListField(superClazz);
            for(Field field : listSuperField)
                list.add(field);
        }
        return list;
    }

    public static Field getFieldByName(String fieldName, Class<?> clazz){
        Field[] selfFields = clazz.getDeclaredFields();
        for(Field field : selfFields){
            if(field.getName().equals(fieldName))
                return field;
        }
        /**
         * direct superClass, 但方法进行递归，实现对多重父类的全查找
         */
        Class<?> superClazz = clazz.getSuperclass();
        if(superClazz != null && superClazz != Object.class)
            return getFieldByName(fieldName, superClazz);

        //如果本类和所有父类都没有，则返回null
        return null;
    }

    /**
     * @param fieldNameSequence 带路径的属性名或简单属性名
     * @param o                 对象
     * @return 属性值
     * @throws Exception
     */
    public static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {
        Object value;
        //将fieldNameSequence进行拆分
        String[] attributes = fieldNameSequence.split("\\.");
        if(attributes.length == 1){
            value = getFieldValueByName(fieldNameSequence, o);
        } else {
            Object fieldObj = getFieldValueByName(attributes[0], o);
            String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }

        return value;
    }

    public static Object getFieldValueByName(String fieldName, Object o) throws Exception {
        Object value;
        Field field = getFieldByName(fieldName, o.getClass());
        if(field == null)
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名： " + fieldName);

        field.setAccessible(true);
        value = field.get(o);
        return value;
    }

}

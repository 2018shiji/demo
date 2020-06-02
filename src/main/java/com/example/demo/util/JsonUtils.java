package com.example.demo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * json对象转换
 */
public class JsonUtils {

    public static String toJson(Object object){
        try{
            return (new ObjectMapper()).setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> E fromJson(String json, Class<E> type){
        try{
            return (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(json, type);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return (new ObjectMapper()).getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static String toJsonString(Object object){
        try {
            return (new ObjectMapper()).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map writeJsonToMap(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        Map map = null;

        try{
            map = (Map)mapper.readValue(json, Map.class);
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

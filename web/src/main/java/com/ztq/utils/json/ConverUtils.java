package com.ztq.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ztq.entity.SystemUser;
import com.ztq.utils.datetime.TimeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 工具转换类
 */
public class ConverUtils {

    private static Gson gson = new GsonBuilder().setDateFormat(TimeUtils.FORMAT6).disableHtmlEscaping().create();

    /**
     * 按指定的时间格式转换为json
     */
    public static String toJson(String format, Object object) {
        Gson gson = new GsonBuilder().setDateFormat(format).disableHtmlEscaping().create();
        return gson.toJson(object);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }


    /**
     * 转换为对象
     */
    public static <T> T toObj(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 转换为Map
     */
    public static Map<String, Object> json2Map(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     * 转换为list
     */
    public static <T> List<T> json2List(String json) {
        return gson.fromJson(json, new TypeToken<List<T>>() {
        }.getType());
    }

    public static <T> List<T> parseString2List(String json, Class clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        List<T> list = new Gson().fromJson(json, type);
        return list;
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        String json = "{\"username\":\"admin\",\"password\":\"quantity\"}";
        System.out.println(json);

        System.out.println(ConverUtils.toJson(ConverUtils.toObj(json, SystemUser.class)));

    }
}
package com.ln.xproject.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class JsonUtils {

    /**
     * 对象转Json
     * 
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    /**
     * Json转对象
     * 
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * Json转列表
     * 
     * @param json
     * @param clazz
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        return new ArrayList<>(JSON.parseArray(json, clazz));
    }

}

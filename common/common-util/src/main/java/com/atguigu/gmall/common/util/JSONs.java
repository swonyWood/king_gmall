package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Kingstu
 * @date 2022/6/25 15:50
 */
@Slf4j
public class JSONs {
    static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 对象转json
     * @param o
     * @return
     */
    public static String toStr(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 一般类型转换
     * @param str
     * @param ref
     * @param <T>
     * @return
     */
    public static<T> T toObj(String str, Class<T> ref){
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        try {
            T t = objectMapper.readValue(str, ref);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 复杂泛型的转化
     * @param str
     * @param ref
     * @param <T>
     * @return
     */
    public static<T> T toObj(String str, TypeReference<T> ref){
        T t = null;
        try {
            t = objectMapper.readValue(str, ref);
        } catch (JsonProcessingException e) {
            log.error("json转对象异常: {}",e);
        }
        return t;
    }
}

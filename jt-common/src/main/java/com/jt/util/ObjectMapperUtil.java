package com.jt.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtil {
    private static final ObjectMapper MAPPER=new ObjectMapper();
	/*
     * 方法说明：
     * 根据api将对象转换为json，同时将json转化为对象
     */
	public static String toJSON(Object obj) {
		String result = null;
		try {
			result=MAPPER.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return result;
	}
	/*
	 * json转化为对象
	 */
	public static <T> T toObject(String json,Class<T> targetClass) {
		T obj=null;
	    try {
			obj=MAPPER.readValue(json, targetClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	    return obj;
	}
	
}

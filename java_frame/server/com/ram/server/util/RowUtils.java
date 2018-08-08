package com.ram.server.util;


import java.util.Map;

//单条数据
public class RowUtils {

	@SuppressWarnings("unchecked")
	public static Object findColumnValue(Object data, Object column) {
		if (data == null) {
			return null;
		}
		if (data instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) data;
			return map.get(column);
		}
		return null;
	}
}

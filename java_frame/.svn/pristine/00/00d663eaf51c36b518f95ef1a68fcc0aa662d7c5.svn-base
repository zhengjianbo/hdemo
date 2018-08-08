package com.ram.kettle.database;

import java.util.concurrent.ConcurrentHashMap;

import com.ram.kettle.log.KettleException;

/**
 * 内容存放变量，维度变量存放，也可以放入BeetlApplication 这样可以定时清理 防止内存过量
 *  
 */
public class CacheApplication {
	private static final CacheApplication instanceSingle = new CacheApplication();
	private static ConcurrentHashMap<String, Object> dimCacheMap = new ConcurrentHashMap<String, Object>(10000,  0.9f);

	public static CacheApplication getInstanceSingle() {
		return instanceSingle;
	}

	public static void init() {

	}

	public static ConcurrentHashMap<String, Object> getDimCacheMap() {
		return dimCacheMap;
	}

	public void put(String key, Object object) throws KettleException {
		dimCacheMap.put(key, object);
	}

	public void put(String key, String object) throws KettleException {
		dimCacheMap.put(key, object);
	}

	public String get(Object key, String defaultValue) throws KettleException {
		if (key == null) {
			return defaultValue;
		}
		if (dimCacheMap.containsKey(key)) {
			Object rValue = dimCacheMap.get(key);
			if (rValue == null) {
				return defaultValue;
			}
			return rValue + "";
		}
		return defaultValue;
	}

	public String get(Object key) throws KettleException {
		return get(key, null);
	}

	public Object get(String key) throws KettleException {
		return dimCacheMap.get(key);
	}
}
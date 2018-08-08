package com.ram.kettle.cache;

import com.ram.kettle.util.Const;
import com.ram.server.util.BaseLog;
 
//缓存调研
public class DataCacheApplication {

	private static DataCacheApplication instance;
	private static Object lock = new Object();
	private DataCacheInter baseCache;
 
	private DataCacheApplication(String className) throws Exception {
		// 这个根据配置文件来，初始BaseCache
		if(Const.isEmpty(className)){
			className="com.ram.kettle.cache.impl.OSCache";
		}
		baseCache = (DataCacheInter) Class.forName(className).newInstance();

		baseCache.set("CacheManager", 120);
	}

	public static DataCacheApplication getInstance(String className)
			throws Exception {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DataCacheApplication(className);
				}
			}
		}
		return instance;
	}

	public static DataCacheApplication getInstance() {
		return instance;
	}

	public void put(Object key, Object value, int seconds) {
		baseCache.put(key + "", value, seconds);
	}

	public void put(Object key, Object value) {
		put(key, value, 10);// 默认缓存10秒
	}

	public Object get(Object key) throws Exception {
		BaseLog.debug("key:"+key);
		return baseCache.get(key + "");
	}

	public static void main(String args[]) throws Exception {
		DataCacheApplication.getInstance("com.ram.kettle.cache.impl.OSCache");
		DataCacheApplication instance = DataCacheApplication.getInstance();

		long start=System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			String key = "key" + (i + ""); 
			// System.out.println("====" +key);
			instance.put(key, "ff", 1000);
		} 
		
		System.out.println("====" + instance.get("key199")); //找不到 发送错误 处理
		long end=System.currentTimeMillis();
		System.out.println("==cost==" + (end-start));
		
	}

}

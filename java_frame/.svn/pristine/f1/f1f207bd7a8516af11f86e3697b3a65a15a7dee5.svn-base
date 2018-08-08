package com.ram.kettle.cache;
 
public interface DataCacheInter {
	public void put(String key, Object value, int seconds);

	public void put(String key, Object value);

	public Object get(String key) throws Exception;

	public void set(String keyPrefix, int refreshPeriod);
}

package com.ram.kettle.cache.impl;

import java.util.Date;

import com.google.gson.Gson;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;
import com.ram.kettle.cache.DataCacheInter;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.util.Const;
import com.ram.server.util.BaseLog;

public class OSCache extends GeneralCacheAdministrator implements
		DataCacheInter {

	private static final long serialVersionUID = 123462462453455L;
	// 过期时间(单位为秒);
	private int refreshPeriod;
	// 关键字前缀字符;
	private String keyPrefix;

	/**
	 * <pre>
	 * 构造器    
	 * @param keyPrefix
	 * @param refreshPeriod
	 * </pre>
	 */
	public OSCache(String keyPrefix, int refreshPeriod) {
		super();
		this.keyPrefix = keyPrefix;
		this.refreshPeriod = refreshPeriod;
	}

	public OSCache() {
		super();
	}

	public void set(String keyPrefix, int refreshPeriod) {
		this.keyPrefix = keyPrefix;
		this.refreshPeriod = refreshPeriod;
	}

	// 添加被缓存的对象;
	public void put(String key, Object value) {
		// 调用父类的putInCache(String key,Object content)方法
		this.putInCache(this.keyPrefix + "_" + key, value);
	}

	// 添加被缓存的对象+过期时间
	public void put(String key, Object value, int time) {
		if (time <= 0) {
			return;
		}
		StringBuffer keyBuffer = new StringBuffer();
		this.putInCache(this.keyPrefix + "_" + key, RowDataUtil
				.copyParamValue(value), new ExpiresRefreshPolicy(time));
	}

	public void removeAll() {
		// 调用父类的flushAll()方法
		this.flushAll();
	}

	// 在给定时间清除缓存的对象; 1
	public void removeAll(Date date) {
		// 调用父类的flushAll(Date date)方法
		this.flushAll(date);
	}

	// 删除被缓存的对象;
	public void remove(String key) {
		// 调用父类的flushEntry(String key)方法
		this.flushEntry(this.keyPrefix + "_" + key);
	}

	// 获取被缓存的对象;
	public Object get(String key) throws Exception {
		try {
			Object data = this.getFromCache(this.keyPrefix + "_" + key,
					this.refreshPeriod);
			return data;
		} catch (NeedsRefreshException e) {
			this.cancelUpdate(this.keyPrefix + "_" + key);
			throw e;
		}

		// Object myValue=null;
		// try {
		// myValue = getFromCache(this.keyPrefix + "_" + key, refreshPeriod);
		// } catch (NeedsRefreshException nre) {
		// boolean updated = false;
		// try {
		// myValue = "This is the content retrieved.";
		// putInCache(this.keyPrefix + "_" + key, myValue);
		// updated = true;
		// } finally {
		// if (!updated) {
		// // It is essential that cancelUpdate is called if the
		// // cached content could not be rebuilt
		// this.cancelUpdate(this.keyPrefix + "_" + key);
		// }
		// }
		// }
		// return myValue;
	}

}
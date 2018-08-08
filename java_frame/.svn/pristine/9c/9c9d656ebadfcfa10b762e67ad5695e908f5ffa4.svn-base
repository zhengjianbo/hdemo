package com.ram.kettle.database;

import java.util.concurrent.ConcurrentHashMap;

import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;

public class DataSourceApplication {
	private static final DataSourceApplication instanceSingle = new DataSourceApplication();

	private static ConcurrentHashMap<Object, Object> dataSourceRamHashMap = new ConcurrentHashMap<Object, Object>();
	private static ConcurrentHashMap<Object, Object> dataSourceMetaHashMap = new ConcurrentHashMap<Object, Object>();

	public static DataSourceApplication getInstanceSingle() {
		return instanceSingle;
	}

	public void put(Object key, Object object) throws KettleException {
		dataSourceRamHashMap.put((key + "").toUpperCase(), object);
	}

	public Object get(Object key) throws KettleException {
		if (dataSourceRamHashMap.containsKey((key + "").toUpperCase())) {
			return dataSourceRamHashMap.get((key + "").toUpperCase());
		}
		return null;
	}

	public void putMeta(Object key, Object object) throws KettleException {
		ConstLog.message("dbName key:"+key+",object:"+object);
		dataSourceMetaHashMap.put((key + "").toUpperCase(), object);
	}

	public Object getMeta(Object key) throws KettleException {
		if (dataSourceMetaHashMap.containsKey((key + "").toUpperCase())) {
			return dataSourceMetaHashMap.get((key + "").toUpperCase());
		}
		return null;
	}
}

package com.ram.beetl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.server.util.FinalConst;

/**
 * Beetl内存变量 用于控制判断
 * 
 * 后期统一使用 DataCacheApplication->(通用处理)
 * 
 */
public class BeetlApplication {
	private static final BeetlApplication instanceSingle = new BeetlApplication();
	
	private static ConcurrentHashMap<String, Object[]> dimCacheMap = new ConcurrentHashMap<String, Object[]>(20000,  0.9f);

	private static int MAXIDLE = 2 * 60 * 60;// -1 情况下  最大有效期2小时  两小时获取到的时候自动清理 或者定时清理,登录情况下 两小时后需要登录

	public static BeetlApplication getInstanceSingle() {
		return instanceSingle;
	}

	public static void init() {

	}

	public static ConcurrentHashMap<String, Object[]> getDimCacheMap() {
		return dimCacheMap;
	}

	public void put(String key, Object object, int timeout)
			throws KettleException {
		if (Const.isEmpty(key)) {
			throw new KettleException("KEY NO FOUND");
		}

		dimCacheMap.put(key.toUpperCase(),
				new Object[] { object, new Date().getTime(), timeout });
	}

	public void put(String key, Object object) throws KettleException {
		if (Const.isEmpty(key)) {
			throw new KettleException("KEY NO FOUND");
		}

		dimCacheMap
				.put(key.toUpperCase(),
						new Object[] { object, new Date().getTime(),
								FinalConst.cache });
	}
	public void remove(String key) throws Exception {
		dimCacheMap.remove(key.toUpperCase());
	}
	public Object get(String key) throws Exception {
		if (Const.isEmpty(key)) {
			return null;
		}
		Object[] obj = dimCacheMap.get(key.toUpperCase());
		if (Const.isEmpty(obj)) {
			return null;
		}
		// 判断是否失效
		if (obj.length != 3) {
			dimCacheMap.remove(key.toUpperCase());
			return null;
		}
		int timeout = (int) obj[2];
		Object data = obj[0];
		if (timeout == -1) {
			// 一直不失效 ，但是考虑系统内存问题，可以将最长时间设置为最长2小时
			timeout = MAXIDLE;
			// return data;
		}
		long startTime = (long) obj[1];

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		cal.add(Calendar.SECOND, timeout);
		Date now = new Date();
		long between = cal.getTimeInMillis() - now.getTime();
		if (between >= 0) {
			return data;
		}
		// 移除
		dimCacheMap.remove(key.toUpperCase());
		return null;
	}

	public boolean isTimeOut(String key) throws Exception {
		if (Const.isEmpty(key)) {
			return true;
		}
		Object[] obj = dimCacheMap.get(key.toUpperCase());
		if (Const.isEmpty(obj)) {
			return true;
		}
		// 判断是否失效
		if (obj.length != 3) {
			return true;
		}
		int timeout = (int) obj[2];
		if (timeout == -1) {
			// return false;
			timeout = MAXIDLE;
		}
		long startTime = (long) obj[1];
		Object data = obj[0];

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		cal.add(Calendar.SECOND, timeout);
		Date now = new Date();
		long between = cal.getTimeInMillis() - now.getTime();
		if (between >= 0) {
			return false;
		}
		 dimCacheMap.remove(key.toUpperCase());
		return true;
	}
	public boolean isTimeOutCheck(String key) throws Exception {
		if (Const.isEmpty(key)) {
			return false;
		}
		Object[] obj = dimCacheMap.get(key.toUpperCase());
		if (Const.isEmpty(obj)) {
			return false;
		}
		// 判断是否失效
		if (obj.length != 3) {
			return false;
		}
		int timeout = (int) obj[2];
		if (timeout == -1) { 
			timeout = MAXIDLE;
		}
		long startTime = (long) obj[1];
		Object data = obj[0];

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		cal.add(Calendar.SECOND, timeout);
		Date now = new Date();
		long between = cal.getTimeInMillis() - now.getTime();
		if (between <=0) {
			return true;
		} 
		return false;
	}
	public static void main(String[] args0) throws Exception {
		// 遍历 并修改  
		// 以后 改进到 使用oscache等  
		for (int i = 0; i < 100; i++) {
			instanceSingle.put(i + "", i, 1);
		}
		ConcurrentHashMap<String, Object[]> map = instanceSingle
				.getDimCacheMap();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object[] val = (Object[]) entry.getValue();
			System.out
					.println(key + "," + val[0] + "," + val[1] + "," + val[2]);
			map.remove("1");
		}
		Thread.sleep(2000);
		System.out.println(map.size() + "," + instanceSingle.get("2"));
		System.out.println(map.size() + "," + instanceSingle.get("2"));
	}

}

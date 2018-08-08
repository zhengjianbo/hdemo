package com.ram.kettle.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Properties;

import com.ram.kettle.util.Const;

public class ConnectionPoolUtil  {
	private static Class<?> PKG = ConnectionPoolUtil.class;

	public static final int defaultInitialNrOfConnections = 5;
	public static final int defaultMaximumNrOfConnections = 30;

	static DataSourceApplication dataSourceApplication = DataSourceApplication
			.getInstanceSingle(); 
	public static Connection getConnection(DatabaseMeta databaseMeta,
			String partitionId) throws Exception { 
		org.apache.tomcat.jdbc.pool.DataSource ds = null;
		Object dsObject = dataSourceApplication.get(databaseMeta.getName());
		if (dsObject == null) {

			String url = null;
			String userName = null;
			String password = null;

			try {
				url = databaseMeta.environmentSubstitute(databaseMeta
						.getURL(partitionId));
				userName = databaseMeta.environmentSubstitute(databaseMeta
						.getUsername());
				password = databaseMeta.environmentSubstitute(databaseMeta
						.getPassword());
			} catch (RuntimeException e) {
				url = databaseMeta.getURL(partitionId);
				userName = databaseMeta.getUsername();
				password = databaseMeta.getPassword();
			}
			// Get the list of pool properties
			Properties originalProperties = databaseMeta
					.getConnectionPoolingProperties();
			originalProperties.setProperty("username", Const.NVL(userName, ""));
			originalProperties.setProperty("password", Const.NVL(password, ""));

			org.apache.tomcat.jdbc.pool.PoolProperties p = new org.apache.tomcat.jdbc.pool.PoolProperties();
			p.setUrl(url);

			String clazz = databaseMeta.getDriverClass();
			p.setDriverClassName(clazz);

			p.setUsername(userName);
			p.setPassword(password);

			Iterator<Object> iterator = originalProperties.keySet().iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();

				String value = originalProperties.getProperty(key);
				try {
					Field field = p.getClass().getDeclaredField(key);
					if (field == null) {
						continue;
					}

					String name = field.getName();
					String upperName = name.substring(0, 1).toUpperCase()
							+ name.substring(1);
					String type = field.getGenericType().toString(); // 获取属性的类型
//					BaseLog.debug("upperName:" + upperName+",type:"+type);
					if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
						Method method = p.getClass().getMethod(
								"set" + upperName, String.class);
						method.invoke(p, value);
					} else if (type.equals("class java.lang.Integer")) {
						Method method = p.getClass().getMethod(
								"set" + upperName, Integer.class);
						method.invoke(p, Integer.parseInt(value));
					} else if (type.equals("int")) {
						Method method = p.getClass().getMethod(
								"set" + upperName, int.class);
						method.invoke(p, Integer.parseInt(value));
					} else if (type.equals("class java.lang.Boolean")) {
						Method method = p.getClass().getMethod(
								"set" + upperName, Boolean.class);
						method.invoke(p, Boolean.parseBoolean(value));
					} else if (type.equals("boolean")) {
						Method method = p.getClass().getMethod(
								"set" + upperName, boolean.class);
						method.invoke(p, Boolean.parseBoolean(value));
					} else if (type.equals("class java.lang.Long")) {
						Method method = p.getClass().getMethod(
								"set" + upperName, Long.class);
						method.invoke(p, Long.parseLong(value));
					} else if (type.equals("long")) {
						Method method = p.getClass().getMethod(
								"set" + upperName, long.class);
						method.invoke(p, Long.parseLong(value));
					}
					//只执行一次
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			// p.setJmxEnabled(false);
			// p.setTestWhileIdle(false);
			// p.setTestOnBorrow(true);
			// Properties testPro = databaseMeta.getDatabaseInterface()
			// .getAttributes();
			// Object sqlConnectionSql = testPro.get("SQL_CONNECT");
			// if (sqlConnectionSql != null) {
			// p.setValidationQuery(sqlConnectionSql + "");
			// }
			// p.setTestOnReturn(false);
			// p.setValidationInterval(30000);
			// p.setTimeBetweenEvictionRunsMillis(30000);
			// p.setMinEvictableIdleTimeMillis(30000);
			// p.setLogAbandoned(true);
			// p.setRemoveAbandoned(true);
			//
			// 因此在tomcat连接池中增加如下配置问题解决：
			// testWhileIdle='true'
			// timeBetweenEvictionRunsMillis='8000'
			// minEvictableIdleTimeMillis='10000' />
			//
			// 备注：1、validationQuery一定要配置
			// 2、 单位都是毫秒
			// 3、timeBetweenEvictionRunsMillis的值要小于minEvictableIdleTimeMillis

			p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
					+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
			ds = new org.apache.tomcat.jdbc.pool.DataSource();
 
			ds.setPoolProperties(p);

			dataSourceApplication.put(databaseMeta.getName(), ds);
		} else {
			ds = (org.apache.tomcat.jdbc.pool.DataSource) dsObject;
		}
		if (DSTransactionManager.inTrans()) {
			return DSTransactionManager.getCurrentThreadConnection(ds);
		} else {
			return ds.getConnection();
		}

	}

}

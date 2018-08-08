package com.ram.kettle.database;

import java.sql.Connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * HikariCP db pool
 * 
 */
public class HikariCPPoolUtil  {
	private static Class<?> PKG = HikariCPPoolUtil.class;

	public static final int defaultInitialNrOfConnections = 5;
	public static final int defaultMaximumNrOfConnections = 30;

	static DataSourceApplication dataSourceApplication = DataSourceApplication
			.getInstanceSingle();

	public static Connection getConnection(DatabaseMeta databaseMeta,
			String partitionId) throws Exception {
		HikariDataSource ds = null;
		Object dsObject = dataSourceApplication.get(databaseMeta.getName());
		if (dsObject == null) {
			HikariConfig config = new HikariConfig();
//			<!-- Hikari Datasource -->
//			 <bean id="dataSourceHikari" class="com.zaxxer.hikari.HikariDataSource"  destroy-method="shutdown">
//			  <!-- <property name="driverClassName" value="${db.driverClass}" /> --> <!-- 无需指定，除非系统无法自动识别 -->
//			  <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8" />
//			  <property name="username" value="${db.username}" />
//			  <property name="password" value="${db.password}" />
//			   <!-- 连接只读数据库时配置为true， 保证安全 -->
//			  <property name="readOnly" value="false" />
//			  <!-- 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒 -->
//			  <property name="connectionTimeout" value="30000" />
//			  <!-- 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟 -->
//			  <property name="idleTimeout" value="600000" />
//			  <!-- 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';） -->
//			  <property name="maxLifetime" value="1800000" />
//			  <!-- 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count) -->
//			  <property name="maximumPoolSize" value="15" />
//			 </bean>

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
			config.setJdbcUrl(url);
			config.setUsername(userName);
			config.setPassword(password);

			String clazz = databaseMeta.getDriverClass();
			config.setDriverClassName(clazz);
			
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

			  ds = new HikariDataSource(config);

			dataSourceApplication.put(databaseMeta.getName(), ds);
		} else {
			ds = (HikariDataSource) dsObject;
		}
		if (DSTransactionManager.inTrans()) {
			return DSTransactionManager.getCurrentThreadConnection(ds);
		} else {
			return ds.getConnection();
		}

	}

}

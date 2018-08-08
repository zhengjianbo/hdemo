package com.kettle.debug;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DbTest extends TestCase {

	org.apache.tomcat.jdbc.pool.DataSource ds = null;

	HikariDataSource dsx = null;
	ArrayList xz=new ArrayList();
	public void setUp() {
//		xz.size();
		org.apache.tomcat.jdbc.pool.PoolProperties p = new org.apache.tomcat.jdbc.pool.PoolProperties();
		p.setUrl("jdbc:mysql://localhost:3306/imkt");
		org.gjt.mm.mysql.Driver x=null;
		p.setDriverClassName("org.gjt.mm.mysql.Driver");

		p.setUsername("root");
		p.setPassword("root");

		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		ds = new org.apache.tomcat.jdbc.pool.DataSource();
		ds.setPoolProperties(p);
		ds.setInitialSize(10);

		HikariConfig config = new HikariConfig();
		config.setDriverClassName("org.gjt.mm.mysql.Driver");
		config.setJdbcUrl("jdbc:mysql://localhost:3306/imkt");
		config.setUsername("root");
		config.setPassword("root");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dsx = new HikariDataSource(config);
	}

	public void testDs() throws SQLException, InterruptedException {

		xtestload(ds);
	}

	public void testDsx() throws SQLException, InterruptedException {

		xtestload(dsx);
	}

	public static void main(String args[]) {

	}
	static int x=0;
	public void xtestload(final DataSource ds) throws SQLException, InterruptedException {

		long start = System.currentTimeMillis();
		
		for (int j = 0; j < 5; j++) {
			Runnable runable = new Runnable() {
				public void run() {
					Connection conn;
					try {
						conn = ds.getConnection();

						for (int i = 0; i < 3000; i++) {
							String sql = " select * from d_dim ";
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							while (rs.next()) {
								// String col1 = rs.getString(1);
								// System.out.println(col1);
							}
							rs.close();
							ps.close();
						}
					} catch (SQLException e) { 
						e.printStackTrace();
					}
					x=x+1;
				}
			};
			Thread t = new Thread(runable);
			t.start(); 
		}
		while(true){
			Thread.sleep(100);
			if(x>=5){
				break;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
 

}

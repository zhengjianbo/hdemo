package com.ram.server.jfinal.interceptor;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.ram.kettle.database.DSTransactionManager;
import com.ram.server.util.BaseLog;

public class DbTransInterceptor implements Interceptor {

	public static void start() throws SQLException {
		DSTransactionManager.start();
	}

	public static void commit() throws SQLException {
		DSTransactionManager.commit();
	}

	public static void rollback() throws SQLException {
		DSTransactionManager.rollback();
	}

	@Override
	public void intercept(Invocation inv) {
		try {
			DSTransactionManager.start();
			BaseLog.info("inTrans:" + DSTransactionManager.inTrans());

			inv.invoke();
			DSTransactionManager.commit();
		} catch (SQLException ex) {
			// try {
			// DSTransactionManager.rollback();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			throw new RuntimeException(ex);
		} catch (RuntimeException ex) {
			// try {
			// DSTransactionManager.rollback();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
			throw ex;
		} finally {
			DSTransactionManager.clear();
		}

	}

	static boolean inTrans() {
		return DSTransactionManager.inTrans();
	}

	static Connection getCurrentThreadConnection(DataSource ds)
			throws SQLException {
		return DSTransactionManager.getCurrentThreadConnection(ds);

	}

}

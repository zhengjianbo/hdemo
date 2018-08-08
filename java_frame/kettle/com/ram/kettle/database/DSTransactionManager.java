package com.ram.kettle.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import com.ram.kettle.log.ConstLog;

/**
 * 默认的事物管理器，可以管理单个，多个数据源事物，但并不是分布式事物管理器 不能保证事务统一，只能尽量。
 * 
 * @author xiandafu
 * 
 */
public class DSTransactionManager {
	static ThreadLocal<Boolean> inTrans = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return false;
		}
	};

	static DataSourceApplication dataSourceApplication = DataSourceApplication
			.getInstanceSingle();

	static ThreadLocal<Map<DataSource, Connection>> conns = new ThreadLocal<Map<DataSource, Connection>>();

	public static void start() {
		inTrans.set(true);
	}

	public static void commit() throws SQLException {
		Map<DataSource, Connection> map = conns.get();
		try {
			if (map == null)
				return;
			SQLException e = null;
			for (Connection conn : map.values()) {
				try {
					conn.commit();
				} catch (SQLException ex) {
					e = ex;
				} finally {
					try {
						conn.close();
					} catch (SQLException ex) {
						ConstLog.message("commit error of connection " + conn
								+ " " + ex.getMessage());
					}

				}

			}
			if (e != null)
				throw e;
		} finally {
			clear();
		}

	}

	public static void rollback() throws SQLException {
		Map<DataSource, Connection> map = conns.get();
		SQLException e = null;
		if (map == null)
			return;
		try {
			for (Connection conn : map.values()) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					e = ex;
				} finally {
					try {
						// BaseLog.info("CONNECTION[" + conn.toString()
						// + "] close");
						conn.close();
					} catch (SQLException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}

				}

			}
			if (e != null)
				throw e;
		} finally {
			clear();
		}

	}

	public static void clear() {
		conns.remove();
		inTrans.remove();
	}

	public static Connection getCurrentThreadConnection(DataSource ds)
			throws SQLException {
		Map<DataSource, Connection> map = conns.get();
		Connection conn = null;
		if (map == null) {
			map = new HashMap<DataSource, Connection>();
			conn = ds.getConnection();
			// 如果用户还有不需要事物，且每次都提交的操作，这个需求很怪，不管了
			conn.setAutoCommit(false);
			map.put(ds, conn);
			conns.set(map);
		} else {
			conn = map.get(ds);
			if (conn != null) {
				return conn;
			}
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			map.put(ds, conn);
		}
		return conn;

	}

	public static boolean inTrans() {
		boolean isInTrans = inTrans.get();
		return isInTrans;
	}

	public static void commitX(String dbName) throws Exception {
		Map<DataSource, Connection> map = conns.get();
		try {
			if (map == null)
				return;
			SQLException e = null;

			Object dbSource = dataSourceApplication.get(dbName);
			Iterator<Map.Entry<DataSource, Connection>> entries = map
					.entrySet().iterator();
			Connection conn = null;
			while (entries.hasNext()) {
				Map.Entry<DataSource, Connection> entry = entries.next();
				if (dbSource == entry.getKey()) {
					conn = entry.getValue();
					break;
				}
			}
			if (conn == null)
				throw e;
			try {
				conn.commit();
			} catch (SQLException ex) {
				e = ex;
			} finally {
				try {
					conn.close();
				} catch (SQLException ex) {
					ConstLog.message("commit error of connection " + conn + " "
							+ ex.getMessage());
				}

			}
			if (e != null)
				throw e;
		} finally {
			clear();
		}

	}
	
	public static void commit(String dbName) throws Exception {
		Map<DataSource, Connection> map = conns.get();
		try {
			if (map == null)
				return;
			SQLException e = null;

			Object dbSource = dataSourceApplication.get(dbName);
			Iterator<Map.Entry<DataSource, Connection>> entries = map
					.entrySet().iterator();
			Connection conn = null;
			while (entries.hasNext()) {
				Map.Entry<DataSource, Connection> entry = entries.next();
				if (dbSource == entry.getKey()) {
					conn = entry.getValue();
					break;
				}
			}
			if (conn == null)
				throw e;
			try {
				conn.commit();
			} catch (SQLException ex) {
				e = ex;
			} finally {
				 
			}
			if (e != null)
				throw e;
		} finally { 
		}

	}
	
}

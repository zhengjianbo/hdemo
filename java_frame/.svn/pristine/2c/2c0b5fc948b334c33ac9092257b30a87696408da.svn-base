package com.ram.beetl;

import com.ram.kettle.database.DSTransactionManager;
import com.ram.kettle.database.DataSourceApplication;
import com.ram.kettle.database.DatabaseMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepMeta;

/**
 * 由于beetl里面操作数据库信息等 生成sql
 * 
 */
public class DbUtilsFunction {

	static DataSourceApplication dataSourceApplication = DataSourceApplication
			.getInstanceSingle();

	// 主动commit用于 批量提交
	public boolean commit() throws Exception {
		if (DSTransactionManager.inTrans()) {
			DSTransactionManager.commit();
		}
		return true;
	}

	// 主动commit用于 批量提交
	public boolean commit(String dbName) throws Exception {
		if (DSTransactionManager.inTrans()) {
			DSTransactionManager.commit(dbName);
		}
		return true;
	}

	// 根据具体数据库 来处理 支持mysql oracle
	public Object getPageSql(Object db, Object sql,String orderby, int page, int size)
			throws Exception {

		String selectSql = sql + "";// 查询sql

		Object dbMeta = dataSourceApplication.getMeta(db);
		if (dbMeta == null) {
			throw new Exception("NO FOUND DB!");
		}
		if (dbMeta.getClass() != DatabaseMeta.class) {
			throw new Exception("NO FOUND DB CLASS!");
		}
		return ((DatabaseMeta) dbMeta).getPageSql(selectSql,orderby, page, size);

		// throw new Exception("NO FOUND PAGE SQL FUNCTION!");
	}

	/*
	 * 
	 * 数据库批量处理
	 * 
	 */
	public void batchUpdate(StepMeta stepMeta,RowMetaInterface rowMeta, Object[] r,
			int dataIndex){
		 
	}
	
  
	public static void main(String args[]) throws Exception {
		DbUtilsFunction fileUtils = new DbUtilsFunction();
//		int page = 1;
//		int size = 20;
//		String from = " select "
//				+ " eid,newscode  "
//				+ " from  newsadmin.NIF_NEWS_R  where 1=1 order by eid asc,newscode asc ";
//
//		String sqlx = fileUtils.getPageSql("DATATEST_ETL", from, page, size) + "";
//		System.out.println(sqlx);
		
//		System.out.println(fileUtils.decodeSpecialCharsWhenLikeUseBackslash("zhengjianbo''zheng'''jjk_%kjlk"));
//		System.out.println(fileUtils.decodeSpecialCharsWhenLikeUseSlash("zhengjianbo''zheng'''jjk_%kjlk"));
		
	}
}

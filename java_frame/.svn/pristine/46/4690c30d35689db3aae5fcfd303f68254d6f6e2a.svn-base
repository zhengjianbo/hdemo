package com.kettle.debug;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.ram.beetl.BeetlApplication;
import com.ram.beetl.DbUtilsFunction;
import com.ram.kettle.controller.CtrlDb;
import com.ram.kettle.database.CacheApplication;
import com.ram.kettle.database.DSTransactionManager;
import com.ram.kettle.database.KettleApplication;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.element.RowData;
import com.ram.kettle.element.RowMeta;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowChange;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.tran.SingleRowTransExecutor;
import com.ram.kettle.tran.Trans;
import com.ram.kettle.tran.TransMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.KettleEnvironment;
import com.ram.server.util.BaseLog;

public class KtrActionDebug extends TestCase {
	TransMeta transMeta = null, transMeta1 = null;

	String folder = null;
	String folderExt = null;

	@SuppressWarnings("unused")
	private Trans tran = null, innerTrans = null, tran1 = null;
	private Gson gson = new Gson();
	public static KettleApplication kApp = KettleApplication
			.getInstanceSingle();

	public static CtrlDb cApp = CtrlDb.getInstanceSingle();

	// String inner_trans = "D:/OLD/KTR/INNER.ktr";
	// TransMeta innerTransMeta = null;

	public void setUp() throws Exception {

		String pathx = KtrActionDebug.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		pathx = java.net.URLDecoder.decode(pathx, "UTF-8");
		folderExt = new File(pathx).getParentFile().getParentFile()
				.getCanonicalPath();
		folder = folderExt + "/ktr_bak/";
		KettleEnvironment.init(folderExt);
	}

	public Object getReturnRow(SingleRowTransExecutor executor,
			RowMeta inputMeta, RowData inputData) throws KettleException {
		RowMetaInterface rInter = RowChange.createRowMetaInterface(inputMeta);
		ProcessReturn pReturn = executor.oneIteration(rInter,
				inputData.getDatas());

		Object[] data = pReturn.getRow();

		String[] columnMetaNames = (String[]) RowChange
				.changeToReturnMeta(pReturn.getRowMeta());

		Map<Object, Object> mapData = new HashMap<Object, Object>();
		int columnIndex = 0;
		for (String columnMetaName : columnMetaNames) {
			mapData.put(columnMetaName.toUpperCase(), data[columnIndex]);
			columnIndex++;
		}

		return mapData;
	}

	public void testRun() throws Exception {
		String fileName = folder + "get_dbtrans.ktr";
		BaseLog.debug("testRun");
		transMeta = new TransMeta(fileName);
		tran = new Trans(transMeta);
		tran.prepareExcute();

		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(3);
		int index = 0;
		double md = Math.random();
		req.put(index++, "EIDX", md + "_122");
		req.put(index++, "URL", "http://www.baidu.com/index.html");
		req.put(index++, "NOTICETITLE", "百度一下_1");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	public void testGettemplate() throws Exception {

		TransMeta transMeta = new TransMeta(folder + "/getTemplate.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(3);
		int index = 0;
		double md = Math.random();
		req.put(index++, "fpath", "");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	// savetemplate.ktr
	public void testReplaceCal() throws Exception {

		TransMeta transMeta = new TransMeta(
				"D:/SVN/NEW_FRAME/WebContent/ktr/VIEW_SECURITY.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(4);
		int index = 0;
		double md = Math.random();
		req.put(index++, "controllerKey", "/zzz");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	CacheApplication dimApp = CacheApplication.getInstanceSingle();

	public void listCache() {
		ConcurrentHashMap<String, Object> dimCacheMap = dimApp.getDimCacheMap();
		for (Map.Entry<String, Object> e : dimCacheMap.entrySet()) {
			String key = Const.Lpad(e.getKey(), " ", 15);
			System.out.println("键:" + key + ", 值:" + e.getValue() + "。");
		}
	}

	/**
	 * 
	 * 加载数据至内存中
	 * 
	 * @throws Exception
	 */
	public void testLoadDim() throws Exception {

		TransMeta transMeta = new TransMeta(
				"D:/SVN/NEW_FRAME/WebContent/ktr/DIM_LOAD.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(4);
		int index = 0;
		double md = Math.random();
		req.put(index++, "controllerKey", "/zzz");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));

			listCache();

		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	public void testLoginCheck() throws Exception {

		TransMeta transMeta = new TransMeta(
				"D:/SVN/NEW_FRAME/WebContent/ktr/LOGIN_CHECK.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		for (int i = 0; i < 3; i++) {
			RequestLocal req = new RequestLocal(4);
			int index = 0;
			double md = Math.random();
			req.put(index++, "sid", "1");
			req.put(index++, "token", "/zzz");
			req.put(index++, "REFER", "/zzz");
			try {
				Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
						executor, req.getInputMeta(), req.getInputData());
				ConstLog.message(gson.toJson(mapData));

			} catch (KettleException e) {
				e.printStackTrace();
			}
			// Thread.sleep(40000);
		}
	}

	public void listBeetlApplication() {

		ConcurrentHashMap<String, Object[]> dimCacheMap = BeetlApplication
				.getDimCacheMap();
		for (Map.Entry<String, Object[]> e : dimCacheMap.entrySet()) {
			String key = Const.Lpad(e.getKey(), " ", 15);
			System.out.print("键:" + key + ", 值:" + e.getValue() + "。");

			System.out.println(" " + e.getValue()[0] + ",  " + e.getValue()[1]
					+ "," + e.getValue()[2]);
		}
	}

	public void testSecLoad() throws Exception {

		TransMeta transMeta = new TransMeta(folder + "SEC_LOADX.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		for (int i = 0; i < 112; i++) {
			RequestLocal req = new RequestLocal(4);
			int index = 0;
			double md = Math.random();
			req.put(index++, "sid", "1");
			try {
				Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
						executor, req.getInputMeta(), req.getInputData());
				ConstLog.message(gson.toJson(mapData));
				Thread.sleep(1000L);
			} catch (KettleException e) {
				e.printStackTrace();
			}
			// Thread.sleep(40000);
		}
		// listBeetlApplication();
		// testViewSec();
		// testMODAL_SECURITY();
	}

	public void testViewSec() throws Exception {

		TransMeta transMeta = new TransMeta(
				"D:/SVN/NEW_FRAME/WebContent/ktr/VIEW_SECURITY.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		for (int i = 0; i < 1; i++) {
			RequestLocal req = new RequestLocal(4);
			int index = 0;
			double md = Math.random();
			req.put(index++, "sid", "1");
			req.put(index++, "urlPara", "index");
			try {
				Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
						executor, req.getInputMeta(), req.getInputData());
				ConstLog.message(gson.toJson(mapData));

			} catch (KettleException e) {
				e.printStackTrace();
			}
			// Thread.sleep(40000);
		}
	}

	public void testMODAL_SECURITY() throws Exception {

		TransMeta transMeta = new TransMeta(
				"D:/SVN/NEW_FRAME/WebContent/ktr/MODAL_SECURITY.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(4);
		int index = 0;
		double md = Math.random();
		req.put(index++, "sid", "1");
		req.put(index++, "urlPara", "index");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void testlisttemplate() throws Exception {

		TransMeta transMeta = new TransMeta(
				"D:/SVN/NEW_FRAME/WebContent/ktr/listtemplate.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(4);
		int index = 0;
		double md = Math.random();
		req.put(index++, "sid", "1");
		req.put(index++, "fpath", "\\assets");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void testBeetl() throws Exception {
		TransMeta transMeta = new TransMeta(folder + "index.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		RequestLocal req = new RequestLocal(4);
		int index = 0;
		double md = Math.random();
		req.put(index++, "sid", "1");
		req.put(index++, "F", "2018");
		try {
			Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
					executor, req.getInputMeta(), req.getInputData());
			ConstLog.message(gson.toJson(mapData));
		} catch (KettleException e) {
			e.printStackTrace();
		}

	}

	public void testdebug_dbtrans() throws Exception {
		DSTransactionManager.start();

		TransMeta transMeta = new TransMeta(folder + "get_dbtrans.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		for (int j = 0; j < 50; j++) {
			final int page = j;
			Runnable runable = new Runnable() {
				public void run() {
					int x = 0;
					while (true) {
						try {
							RequestLocal req = new RequestLocal(9);
							int index = 0;

							double md = Math.random();
							double xmd = Math.random();

							if (x % 2 == 0) {
								req.put(index++, "page", md);
								req.put(index++, "NOTICETITLE",
										"建信信托-铁建蓝海集合资金信托计划（2号）提前终止的公告" + xmd);

							} else if (x % 2 == 1) {
								req.put(index++, "NOTICETITLE",
										"建信信托-铁建蓝海集合资金信托计划（2号）提前终止的公告" + md);
								req.put(index++, "page", xmd);
							}
 
							try {
								Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
										executor, req.getInputMeta(),
										req.getInputData());

								if (x % 2 == 0) {
									if (!(mapData.get("PAGE") + "").equals(md
											+ "")) {
										ConstLog.message("发生错误");
										return;
									}

								} else if (x % 2 == 1) {
									if (!(mapData.get("PAGE") + "").equals(xmd
											+ "")) {
										ConstLog.message("发生错误");
										return;
									}
								}
								ConstLog.message(page + "=="+x+"==" + md + "," + xmd
										+ ":" + gson.toJson(mapData));

							} catch (KettleException e) {
								e.printStackTrace();
							}
							x++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			Thread t = new Thread(runable);
			t.start();
			// Thread.sleep(1000L);
		}
		while (true) {
			Thread.sleep(1000L);
		}

		// DSTransactionManager.commit();
	}

	public void testdebug_dbtransx() throws Exception {
		DSTransactionManager.start();

		TransMeta transMeta = new TransMeta(folder + "get_dbtrans.ktr");
		Trans tran = new Trans(transMeta);
		tran.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				tran, SingleRowTransExecutor.INPUT,
				SingleRowTransExecutor.OUTPUT);

		for (int j = 0; j < 1; j++) {
			final int page = j;

			try {
				RequestLocal req = new RequestLocal(9);
				int index = 0;
				double md = Math.random();
				req.put(index++, "sid", "1" + md);
				req.put(index++, "NOTICETITLE",
						"建信信托-&铁建>\"蓝海集合资金信托计划（2号）提前终止的公告");
				req.put(index++, "EID", "187000002034&048560'sdfsdf%''sdfsdf");
				req.put(index++, "page", page + "");
				req.put(index++, "DB", "CPSS");
				req.put(index++, "SQL", "select * from base_user");
				req.put(index++, "orderby", " order by userid asc ");
				//
				try {
					Map<Object, Object> mapData = (Map<Object, Object>) getReturnRow(
							executor, req.getInputMeta(), req.getInputData());
					// ConstLog.message(gson.toJson(mapData));
					// Thread.sleep(1000L);
				} catch (KettleException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// DSTransactionManager.commit();
	}

	public void testF() throws Exception {
		DbUtilsFunction fileUtils = new DbUtilsFunction();
		int page = 1;
		int size = 20;
		String from = " select "
				+ " eid,newscode  "
				+ " from  newsadmin.NIF_NEWS_R  where 1=1 order by eid asc,newscode asc ";

		String sqlx = fileUtils.getPageSql("DATATEST_ETL_HQ", from, null, page,
				size) + "";
		System.out.println(sqlx);

	}

	public void testBatch_Save() throws Exception {
		DSTransactionManager.start();

	}

}

package com.ram.kettle.database;

import java.util.concurrent.ConcurrentHashMap;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.element.RowData;
import com.ram.kettle.element.RowMeta;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowChange;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.tran.SingleRowTransExecutor;
import com.ram.kettle.tran.Trans;
import com.ram.kettle.tran.TransMeta;
import com.ram.kettle.util.KettleEnvironment; 
import com.ram.server.util.BaseLog;

public class KettleApplication {
	private static final KettleApplication instanceSingle = new KettleApplication();
	public static final String INPUT = "INPUT";
	public static final String OUTPUT = "OUTPUT";

	private static ConcurrentHashMap<Object, Object> ktrMap = new ConcurrentHashMap<Object, Object>();

	public static KettleApplication getInstanceSingle() {
		return instanceSingle;
	}

	public static void init(String path) {
		try {
			// System.setProperty("user.dir", EmContentListener.path);
			// System.setProperty("KETTLE_HOME", EmContentListener.path);
			KettleEnvironment.init(path);
		} catch (KettleException e) {
			BaseLog.debug("KETTLE INIT ERROR:" + e.getMessage());
		} catch (Exception e) {
			BaseLog.debug("KETTLE INIT ERROR:" + e.getMessage());
		}
	}

	public void put(Object key, Object object) throws KettleException {
		ktrMap.put((key + "").toUpperCase(), object);
	}

	public Object get(Object key) throws KettleException {
		if (ktrMap.containsKey((key + "").toUpperCase())) {
			return ktrMap.get((key + "").toUpperCase());
		}
		return null;
	}

	public synchronized Object put(Object key, String filename, String input,
			String outPut) throws Exception {
		if (ktrMap.containsKey(key)) {
			ConstLog.message("KETTLE KTR:" + key + " HAD INPUT!");
			return ktrMap.get(key);
		}
		TransMeta transMeta = new TransMeta(filename);

		Trans trans = new Trans(transMeta);

		trans.prepareExcute();
		final SingleRowTransExecutor executor = new SingleRowTransExecutor(
				trans, input, outPut);
		executor.init();
		ktrMap.put(key, executor);
		return executor;
	}

	public Object getReturnRow(String key, RowMeta inputMeta, RowData inputData) {

		try {
			RowMetaInterface rInter = RowChange
					.createRowMetaInterface(inputMeta);
			SingleRowTransExecutor executor = (SingleRowTransExecutor) KettleApplication
					.getInstanceSingle().get(key);
			ProcessReturn pReturn = executor.oneIteration(rInter,
					inputData.getDatas());
			return pReturn;

		} catch (KettleException e) {
			ConstLog.message("Get KETTLE KTR:" + key + " LOAD ERROR!");
		}
		return null;
	}

	public Object getReturnRow(String key, RowMetaInterface rowMeta, Object[] r) {

		try {
			SingleRowTransExecutor executor = (SingleRowTransExecutor) instanceSingle
					.get(key);
			ProcessReturn pReturn = executor.oneIteration(rowMeta, r);
			return pReturn;

		} catch (KettleException e) {
			ConstLog.message("Get KETTLE KTR:" + key + " LOAD ERROR!");
		}
		return null;
	}
}

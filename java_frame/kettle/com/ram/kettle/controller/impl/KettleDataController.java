package com.ram.kettle.controller.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.database.KettleApplication;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.element.RowData;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowChange;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.tran.SingleRowTransExecutor;

public class KettleDataController extends DataController {
	SingleRowTransExecutor executor = null;
	private String key=null;
	 Gson gson = new Gson();
	 
	public void reset(String key) {
		init(key);
	}

	public void init(String key) {
		this.key=key;
		try {
			Object tranExecutor = KettleApplication.getInstanceSingle()
					.get(key);
			if (tranExecutor != null) {
				executor = (SingleRowTransExecutor) tranExecutor;
			}
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}

	public String getGateWayAction() {
		return "kettle";
	}

	public Object getReturnRow(String action,
			com.ram.kettle.element.RowMeta inputMeta, RowData inputData)
			throws KettleException {
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
		
		ConstLog.message(key+ "---data:"+gson.toJson(mapData));
		
		return mapData;
	}
}

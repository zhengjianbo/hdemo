package com.ram.kettle.controller.impl;

import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RowData;
import com.ram.kettle.element.RowMeta;
import com.ram.kettle.log.KettleException;

public class SpringDataController extends DataController {

	public void reset(String key) {

	}

	public void init(String key) {

	}

	public String getGateWayAction() {
		return "SpringDataController";
	}

	public Object getReturnRow(String action, RowMeta inputMeta,
			RowData inputData) throws KettleException {
		return null;
	}
}
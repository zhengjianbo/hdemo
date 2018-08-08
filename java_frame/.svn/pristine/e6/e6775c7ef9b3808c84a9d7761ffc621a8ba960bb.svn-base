package com.ram.kettle.controller;

import com.ram.kettle.element.RowData;
import com.ram.kettle.log.KettleException;

public abstract class DataController {
	protected boolean first = true;
	private String key;

	public void init(String key) {
		this.key=key;
	}
	public void reset(String key){
		this.key=key;
	}

	public void dispose() {
	}

	public String getGateWayAction() {
		return null;
	}

	public abstract Object getReturnRow(String action, com.ram.kettle.element.RowMeta inputMeta,
			RowData inputData) throws KettleException;

}

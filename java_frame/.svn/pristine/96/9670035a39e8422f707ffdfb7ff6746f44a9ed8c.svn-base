package com.ram.kettle.element;

import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

public class RequestLocalApp {
	RowMeta inputMeta;
	RowData inputData;
	int parameterSize = 0;
	int attrSize = 0;

	public RequestLocalApp(Object[] arguments) {
		int len = 10;
		inputMeta = new RowMeta(len);
		inputData = new RowData(len);

		String requestURI = arguments[0] + "";// 请求URL
		String requestMethod = arguments[1] + "";// 请求action
		Object param = arguments[2];// 请求参数 稽核

		put(0, "ACTIONKEY", requestURI);
		put(1, "METHODNAME", requestMethod);

		JSONObject jObject = JSONObject.fromObject(param);

		int index = 2;
		Iterator iterator = jObject.keys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object value = jObject.get(key);
			put(index, key, value);
			index++;
			if (index >= 10) {
				break;
			}
		}

	}
 
	public int getRow(String key) {
		return inputMeta.getRow(key);
	}

	public void put(int index, String key, Object value) {
		inputMeta.set(index, key);
		inputData.set(index, value);
	}

	@SuppressWarnings("rawtypes")
	public RequestLocalApp(Map requestMap, int ext) {

		int len = requestMap.size();
		inputMeta = new RowMeta(len + ext);
		inputData = new RowData(len + ext);

		int i = 0;
		for (Iterator iter = requestMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry element = (Map.Entry) iter.next();
			Object strKey = element.getKey();
			String[] value = (String[]) element.getValue();
			inputMeta.set(i, strKey.toString());
			inputData.set(i, value[0]);
			i++;
		}
	}

	public RequestLocalApp(int len) {
		inputMeta = new RowMeta(len);
		inputData = new RowData(len);
	}

	@SuppressWarnings("rawtypes")
	public RequestLocalApp(Map requestMap) {

		int len = requestMap.size();
		inputMeta = new RowMeta(len);
		inputData = new RowData(len);

		int i = 0;
		for (Iterator iter = requestMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry element = (Map.Entry) iter.next();
			Object strKey = element.getKey();
			String[] value = (String[]) element.getValue();
			inputMeta.set(i, strKey.toString());
			inputData.set(i, value[0]);
			i++;
		}
	}

	public RowMeta getInputMeta() {
		return inputMeta;
	}

	public void setInputMeta(RowMeta inputMeta) {
		this.inputMeta = inputMeta;
	}

	public RowData getInputData() {
		return inputData;
	}

	public void setInputData(RowData inputData) {
		this.inputData = inputData;
	}
}
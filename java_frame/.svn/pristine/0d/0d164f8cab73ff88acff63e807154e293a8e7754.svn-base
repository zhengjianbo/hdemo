package com.ram.kettle.element;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ram.server.jfinal.AppConfig;

public class RequestLocal {
	RowMeta inputMeta;
	RowData inputData;
	int parameterSize = 0;
	int attrSize = 0;

	public static final int EXTRA = AppConfig.EXTATTR;

	public int getExtIndexStart() {
		return parameterSize + attrSize;
	}

	public int getParameterSize() {
		return parameterSize;
	}

	public RequestLocal(HttpServletRequest request) {
		this(request, EXTRA, EXTRA);
	}

	// public RequestLocal(HttpServletRequest request) {
	// this(request, EXTRA, EXTRA + 3);
	// }
	// request +6个额外 不加attribute
	public RequestLocal(HttpServletRequest request, int extInput) {
		this(request, EXTRA, EXTRA + extInput);
	}

	public RequestLocal(HttpServletRequest request, int attrExt, int ext) {
		this(request.getParameterMap(), ext);
		Enumeration<String> attsNames = request.getAttributeNames();
		parameterSize = request.getParameterMap().size();
		attrSize = attrExt;

		int attrExtAddCount = 0;
		// 最多attrExt个
		while (attsNames.hasMoreElements()) {
			// 替换原有的内容
			String attrName = (String) attsNames.nextElement();
			int rowIndex = this.getRow(attrName);
			if (rowIndex > -1) {
				// 已经存在 更新
				this.put(rowIndex, attrName, request.getAttribute(attrName));

			} else {// 新的记录
				this.put(parameterSize + attrExtAddCount, attrName,
						request.getAttribute(attrName));
				attrExtAddCount++;
			}
			if (attrExtAddCount >= attrExt) {
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
	private RequestLocal(Map requestMap, int ext) {

		parameterSize = requestMap.size();

		inputMeta = new RowMeta(parameterSize + ext);
		inputData = new RowData(parameterSize + ext);

		int i = 0;
		for (Iterator iter = requestMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry element = (Map.Entry) iter.next();
			Object strKey = element.getKey();

			if (strKey.toString().equalsIgnoreCase(AppConfig.SID)) {
				continue;// sid不允许传入
			}

			String[] value = (String[]) element.getValue();
			inputMeta.set(i, strKey.toString());
			inputData.set(i, value[0]);
			i++;
		}
	}

	public RequestLocal(int len) {
		inputMeta = new RowMeta(len);
		inputData = new RowData(len);
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
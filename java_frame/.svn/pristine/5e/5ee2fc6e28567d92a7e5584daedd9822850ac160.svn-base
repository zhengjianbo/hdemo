package com.ram.kettle.element;

import com.ram.kettle.util.Const;

public class RowData {
	public Object[] datas;

	public RowData(int size) {
		datas = new Object[size];
	}

	public void set(int i, Object value) {
		datas[i] = value;
	}

	public Object[] getDatas() {
		return datas;
	}

	public void setDatas(Object[] datas) {
		this.datas = datas;
	}

	public Object getData(int i) {
		if (i < 0) {
			return null;
		}
		if (Const.isEmpty(datas)) {
			return null;
		}
		if (i >= datas.length) {
			return null;
		}
		return datas[i];
	}

}
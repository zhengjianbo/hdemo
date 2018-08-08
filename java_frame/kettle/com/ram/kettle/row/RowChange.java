package com.ram.kettle.row;

import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;

public class RowChange {
	public static RowMetaInterface createRowMetaInterface(
			com.ram.kettle.element.RowMeta inputMeta) {
		String[] rows = inputMeta.getRows();
		if (Const.isEmpty(rows)) {
			return null;
		}
		RowMetaInterface rm = new RowMeta();
		int rLen = rows.length;
		for (int i = 0; i < rLen; i++) {
			if (Const.isEmpty(rows[i])) {
				rm.addValueMeta(new ValueMeta(Math.random() + "",
						ValueMeta.TYPE_STRING));
				continue;
			}
			rm.addValueMeta(new ValueMeta(rows[i], ValueMeta.TYPE_SERIALIZABLE));
		}
		return rm;

	}

	public static Object[] changeToReturnMeta(RowMetaInterface inputMeta) {
		Object[] pReturn = inputMeta.getFieldNames();
		return pReturn;

	}
}
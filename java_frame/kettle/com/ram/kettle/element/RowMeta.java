package com.ram.kettle.element;
 

import com.ram.kettle.util.Const;

public class RowMeta {
	public String[] rows;

	public RowMeta(int size) {
		rows = new String[size];
	}

	public void set(int i, String value) {
		rows[i] = value;
	}

	public String[] getRows() {
		return rows;
	}

	public void setRows(String[] rows) {
		this.rows = rows;
	}

	public int getRow(String frow) {
		if (Const.isEmpty(rows)) {
			return -1;
		}
		int i = 0;
		for (String row : rows) {
		    if(Const.isEmpty(row)){
		        return -1;
		    }
			if (row.equalsIgnoreCase(frow)) {
				return i;
			}
			i++;
		}
		return -1; 
	}

}
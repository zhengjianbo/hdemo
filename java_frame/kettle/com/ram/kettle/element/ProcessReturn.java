package com.ram.kettle.element;

import com.ram.kettle.row.RowMetaInterface;

public class ProcessReturn implements java.io.Serializable {

	private static final long serialVersionUID = -3560812441955380316L;
	private RowMetaInterface rowMeta;
	private Object[] row;
	private String nextStream;
	private String thisStream;
	private String abortStream = "-1";

	public RowMetaInterface getRowMeta() {
		return rowMeta;
	}

	public void setRowMeta(RowMetaInterface rowMeta) {
		this.rowMeta = rowMeta;
	}

	public Object[] getRow() {
		return row;
	}

	public void setRow(Object[] row) {
		this.row = row;
	}

	public String getNextStream() {
		return nextStream;
	}

	public void setNextStream(String nextStream) {
		this.nextStream = nextStream;
	}

	public void setAbortStream(String abortStream) {
		this.abortStream = abortStream;
	}

	public String getAbortStream() {
		return abortStream;
	}

	public String getThisStream() {
		return thisStream;
	}

	public void setThisStream(String thisStream) {
		this.thisStream = thisStream;
	}
}

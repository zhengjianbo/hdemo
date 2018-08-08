package com.ram.kettle.row;

import java.math.BigDecimal;
import java.util.Date;

import com.ram.kettle.log.KettleException;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.sun.corba.se.spi.ior.ObjectId;

public class RowMetaAndData implements Cloneable {
	private RowMetaInterface rowMeta;

	private Object[] data;

	public RowMetaAndData() {
		clear();
	}

	/**
	 * @param rowMeta
	 * @param data
	 */
	public RowMetaAndData(RowMetaInterface rowMeta, Object... data) {
		this.rowMeta = rowMeta;
		this.data = data;
	}

	public RowMetaAndData clone() {
		RowMetaAndData c = new RowMetaAndData();
		c.rowMeta = rowMeta.clone();
		try {
			c.data = rowMeta.cloneRow(data);
		} catch (KettleException e) {
			throw new RuntimeException(
					"Problem with clone row detected in RowMetaAndData", e);
		}

		return c;
	}

	@Override
	public String toString() {
		try {
			return rowMeta.getString(data);
		} catch (KettleException e) {
			return rowMeta.toString() + ", error presenting data: "
					+ e.toString();
		}
	}

	/**
	 * @return the data
	 */
	public Object[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object[] data) {
		this.data = data;
	}

	/**
	 * @return the rowMeta
	 */
	public RowMetaInterface getRowMeta() {
		return rowMeta;
	}

	/**
	 * @param rowMeta
	 *            the rowMeta to set
	 */
	public void setRowMeta(RowMetaInterface rowMeta) {
		this.rowMeta = rowMeta;
	}

	public int hashCode() {
		try {
			return rowMeta.hashCode(data);
		} catch (KettleException e) {
			throw new RuntimeException(
					"Row metadata and data: unable to calculate hashcode because of a data conversion problem",
					e);
		}
	}

	public boolean equals(Object obj) {
		try {
			return rowMeta.compare(data, ((RowMetaAndData) obj).getData()) == 0;
		} catch (KettleException e) {
			throw new RuntimeException(
					"Row metadata and data: unable to compare rows because of a data conversion problem",
					e);
		}
	}

	public void addValue(ValueMeta valueMeta, Object valueData) {
		if (valueMeta.isInteger() && (valueData instanceof ObjectId)) {
			// valueData = new LongObjectId((ObjectId) valueData).longValue();
		}
		data = RowDataUtil.addValueData(data, rowMeta.size(), valueData);
		rowMeta.addValueMeta(valueMeta);
	}

	public void addValue(String valueName, int valueType, Object valueData) {
		addValue(new ValueMeta(valueName, valueType), valueData);
	}

	public void clear() {
		rowMeta = new RowMeta();
		data = new Object[] {};
	}

	public long getInteger(String valueName, long def) throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getInteger(idx, def);
	}

	public long getInteger(int index, long def) throws KettleException {
		Long number = rowMeta.getInteger(data, index);
		if (number == null)
			return def;
		return number.longValue();
	}

	public Long getInteger(String valueName) throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return rowMeta.getInteger(data, idx);
	}

	public Long getInteger(int index) throws KettleException {
		return rowMeta.getInteger(data, index);
	}

	public double getNumber(String valueName, double def)
			throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getNumber(idx, def);
	}

	public double getNumber(int index, double def) throws KettleException {
		Double number = rowMeta.getNumber(data, index);
		if (number == null)
			return def;
		return number.doubleValue();
	}

	public Date getDate(String valueName, Date def) throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getDate(idx, def);
	}

	public Date getDate(int index, Date def) throws KettleException {
		Date date = rowMeta.getDate(data, index);
		if (date == null)
			return def;
		return date;
	}

	public BigDecimal getBigNumber(String valueName, BigDecimal def)
			throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getBigNumber(idx, def);
	}

	public BigDecimal getBigNumber(int index, BigDecimal def)
			throws KettleException {
		BigDecimal number = rowMeta.getBigNumber(data, index);
		if (number == null)
			return def;
		return number;
	}

	public boolean getBoolean(String valueName, boolean def)
			throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getBoolean(idx, def);
	}

	public boolean getBoolean(int index, boolean def) throws KettleException {
		Boolean b = rowMeta.getBoolean(data, index);
		if (b == null)
			return def;
		return b.booleanValue();
	}

	public String getString(String valueName, String def)
			throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getString(idx, def);
	}

	public String getString(int index, String def) throws KettleException {
		String string = rowMeta.getString(data, index);
		if (string == null)
			return def;
		return string;
	}

	public byte[] getBinary(String valueName, byte[] def)
			throws KettleException {
		int idx = rowMeta.indexOfValue(valueName);
		if (idx < 0)
			throw new KettleException("Unknown column '" + valueName + "'");
		return getBinary(idx, def);
	}

	public byte[] getBinary(int index, byte[] def) throws KettleException {
		byte[] bin = rowMeta.getBinary(data, index);
		if (bin == null)
			return def;
		return bin;
	}

	public int compare(RowMetaAndData compare, int[] is, boolean[] bs)
			throws KettleException {
		return rowMeta.compare(data, compare.getData(), is);
	}

	public boolean isNumeric(int index) {
		return rowMeta.getValueMeta(index).isNumeric();
	}

	public int size() {
		return rowMeta.size();
	}

	public ValueMetaInterface getValueMeta(int index) {
		return rowMeta.getValueMeta(index);
	}

	public void removeValue(String valueName) throws KettleException {
		int index = rowMeta.indexOfValue(valueName);
		if (index < 0)
			throw new KettleException("Unable to find '" + valueName
					+ "' in the row");
		removeValue(index);
	}

	public synchronized void removeValue(int index) {
		rowMeta.removeValueMeta(index);
		data = RowDataUtil.removeItem(data, index);
	}
}

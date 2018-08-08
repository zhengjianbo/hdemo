package com.ram.kettle.steps.impl;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaAndData;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class RowGenerator extends StepMeta implements StepInterface {
	private static Class<?> PKG = RowGenerator.class;
	private final String typeid = "RowGenerator";

	private String xrowLimit = "1"; // 第几行

	private String preField = ""; // 前面字段

	private String value[];
	private String fieldName[];
	private String fieldType[];

	public RowMetaInterface outputRowMeta;
	public RowMetaInterface inputRowMeta;
	public Object[] outputRowData;

	public long rowLimit;
	public long rowsWritten;
	public int fieldIndex;

	public RowGenerator() {
		super();
	}

	public RowGenerator(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void getFields(RowMetaInterface row, String origin)
			throws KettleException {

		RowMetaAndData rowMetaAndData = buildRow(origin);

		for (ValueMetaInterface valueMeta : rowMetaAndData.getRowMeta()
				.getValueMetaList()) {
			valueMeta.setOrigin(origin);
		}

		row.mergeRowMeta(rowMetaAndData.getRowMeta());
	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	public void allocate(int nrfields) {
		fieldName = new String[nrfields];
		fieldType = new String[nrfields];

		value = new String[nrfields];
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields");
			int nrfields = XMLHandler.countNodes(fields, "field");

			allocate(nrfields);

			String slength, sprecision;

			for (int i = 0; i < nrfields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);

				fieldName[i] = XMLHandler.getTagValue(fnode, "name");
				fieldType[i] = XMLHandler.getTagValue(fnode, "type");
				value[i] = XMLHandler.getTagValue(fnode, "nullif");
				slength = XMLHandler.getTagValue(fnode, "length");
				sprecision = XMLHandler.getTagValue(fnode, "precision");

			}

			// Is there a limit on the number of rows we process?
			xrowLimit = XMLHandler.getTagValue(stepnode, "limit");
			preField = XMLHandler.getTagValue(stepnode, "field");
			
			rowLimit = Const.toLong(xrowLimit, 1L);
			
		} catch (Exception e) {
			throw new KettleException("Unable to load step info from XML", e);
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) { 
//			ConstLog.message("==" + this.getTypeId() + "=初始化"
//					+ this.getName());
		}
		return true;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta,
			Object[] rowData) throws KettleException {
		if (rowData == null) {
			throw new KettleException("Row Generator DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {

					inputRowMeta = rowMeta.clone();
					outputRowMeta = rowMeta.clone();

					getFields(outputRowMeta, getStepname());

					String field = this.preField;
					if (!Const.isEmpty(field)) {
						fieldIndex = rowMeta.indexOfValue(field);
						if (fieldIndex < 0) {
							throw new KettleException(
									"NO FOUND ROW SELECT FIELD"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						throw new KettleException("NO FOUND ROW SELECT FIELD"); //$NON-NLS-1$ //$NON-NLS-2$
					}

					first = false;
				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {

			Object[] extraData = new Object[this.fieldName.length];

			Object fieldValue = rowData[fieldIndex];

			// 有小类，改变值
			if (fieldValue instanceof List) {
				// list<Map<Object, Object> >
				List<Map<Object, Object>> fieldMapValueList = (List<Map<Object, Object>>) fieldValue;
				if (!Const.isEmpty(fieldMapValueList)) {

					Map<Object, Object> fieldData = fieldMapValueList
							.get((int) (rowLimit - 1));
					int valueIndex = 0;
					for (String xfieldName : fieldName) {
						Object kValue = fieldData.get(xfieldName.toUpperCase());// 值获取到了然后放入新值
						extraData[valueIndex++] = kValue;
					}
				}
			} else if (fieldValue instanceof Map) {
				// Map<Object, Object>
				Map<Object, Object> fieldMapValue = (Map<Object, Object>) fieldValue;
				int valueIndex = 0;
				for (String xfieldName : fieldName) {
					Object kValue = fieldMapValue.get(xfieldName.toUpperCase());// 值获取到了然后放入新值
					extraData[valueIndex++] = kValue;
				}

			} else {
				ConstLog.message("源字段值格式不符合要求");
			}

			rowData = RowDataUtil.addRowData(rowData, inputRowMeta.size(),
					extraData);

			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(rowData);
			pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;

		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException("ROW SELECT ERROR"); //$NON-NLS-1$
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, rowData, 1, errorMessage, null,
						"IS_E001");
			}
		}

		return null;
	}

	public RowMetaAndData buildRow(String origin) {
		RowMetaInterface rowMeta = new RowMeta();
		Object[] rowData = RowDataUtil.allocateRowData(this.fieldName.length);

		for (int i = 0; i < fieldName.length; i++) {
			int valtype = ValueMeta.getType(this.fieldType[i]);
			if (fieldName[i] != null) {
				ValueMetaInterface valueMeta = new ValueMeta(fieldName[i],
						valtype);
				valueMeta.setOrigin(origin);

				ValueMetaInterface stringMeta = valueMeta.clone();
				stringMeta.setType(ValueMetaInterface.TYPE_STRING);

				String stringValue = this.value[i];

				// If the value is empty: consider it to be NULL.
				if (Const.isEmpty(stringValue)) {
					rowData[i] = null;

					if (valueMeta.getType() == ValueMetaInterface.TYPE_NONE) {
						String message = BaseMessages.getString(PKG,
								"RowGenerator.CheckResult.SpecifyTypeError",
								valueMeta.getName(), stringValue);

					}
				} else {
					// Convert the data from String to the specified type ...
					//
					try {
						rowData[i] = valueMeta.convertData(stringMeta,
								stringValue);
					} catch (KettleException e) {
						switch (valueMeta.getType()) {
						case ValueMetaInterface.TYPE_NUMBER: {
							String message = BaseMessages
									.getString(
											PKG,
											"RowGenerator.BuildRow.Error.Parsing.Number",
											valueMeta.getName(), stringValue,
											e.toString());

						}
							break;
						case ValueMetaInterface.TYPE_DATE: {
							String message = BaseMessages.getString(PKG,
									"RowGenerator.BuildRow.Error.Parsing.Date",
									valueMeta.getName(), stringValue,
									e.toString());

						}
							break;
						case ValueMetaInterface.TYPE_INTEGER: {
							String message = BaseMessages
									.getString(
											PKG,
											"RowGenerator.BuildRow.Error.Parsing.Integer",
											valueMeta.getName(), stringValue,
											e.toString());

						}
							break;
						case ValueMetaInterface.TYPE_BIGNUMBER: {
							String message = BaseMessages
									.getString(
											PKG,
											"RowGenerator.BuildRow.Error.Parsing.BigNumber",
											valueMeta.getName(), stringValue,
											e.toString());

						}
							break;
						default:
						// Boolean and binary don't throw errors normally, so
						// it's probably an unspecified error problem...
						{
							String message = BaseMessages
									.getString(
											PKG,
											"RowGenerator.CheckResult.SpecifyTypeError",
											valueMeta.getName(), stringValue);

						}
							break;
						}
					}
				}
				// Now add value to the row!
				// This is in fact a copy from the fields row, but now with
				// data.
				rowMeta.addValueMeta(valueMeta);
			}
		}

		return new RowMetaAndData(rowMeta, rowData);
	}

	public String getTypeid() {
		return typeid;
	}

}

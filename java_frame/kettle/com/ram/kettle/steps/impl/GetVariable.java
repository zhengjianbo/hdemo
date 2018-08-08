package com.ram.kettle.steps.impl;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.ram.kettle.database.CacheApplication;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class GetVariable extends StepMeta implements StepInterface {
	private static Class<?> PKG = GetVariable.class;
	private final String typeid = "GetVariable";

	private CacheApplication dimApp = CacheApplication.getInstanceSingle();

	private String[] fieldName;
	private String[] variableString;
	private String[] secvariableString;
	private String[] variableType;

	private int[] fieldType;

	private String[] fieldFormat;
	private int[] fieldLength;
	private int[] fieldPrecision;

	private String[] currency;
	private String[] decimal;
	private String[] group;

	private int[] trimType;
	// ----------
	public boolean readsRows;
	public RowMetaInterface outputRowMeta;
	public RowMetaInterface inputRowMeta;
	public Object[] extraData;
	public RowMetaInterface conversionMeta;

	public int[] fromIndex = null;

	public GetVariable() {
		super();
	}

	public GetVariable(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int count) {
		fieldName = new String[count];
		variableString = new String[count];
		secvariableString = new String[count];
		variableType = new String[count];
		fieldType = new int[count];

		fieldFormat = new String[count];
		fieldLength = new int[count];
		fieldPrecision = new int[count];

		currency = new String[count];
		decimal = new String[count];
		group = new String[count];

		trimType = new int[count];
	}

	public Object clone() {
		GetVariable retval = (GetVariable) super.clone();

		int count = fieldName.length;

		retval.allocate(count);

		for (int i = 0; i < count; i++) {
			retval.fieldName[i] = fieldName[i];
			retval.variableString[i] = variableString[i];
			retval.secvariableString[i] = secvariableString[i];
			retval.variableType[i] = variableType[i];
		}

		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields");
			int count = XMLHandler.countNodes(fields, "field");

			allocate(count);

			for (int i = 0; i < count; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);

				fieldName[i] = XMLHandler.getTagValue(fnode, "name");
				variableString[i] = XMLHandler.getTagValue(fnode, "variable");
				secvariableString[i] = XMLHandler.getTagValue(fnode,
						"secvariable");
				variableType[i] = XMLHandler.getTagValue(fnode, "variabletype");
				fieldType[i] = ValueMeta.getType(XMLHandler.getTagValue(fnode,
						"type"));
				fieldFormat[i] = XMLHandler.getTagValue(fnode, "format");
				currency[i] = XMLHandler.getTagValue(fnode, "currency");
				decimal[i] = XMLHandler.getTagValue(fnode, "decimal");
				group[i] = XMLHandler.getTagValue(fnode, "group");
				fieldLength[i] = Const.toInt(
						XMLHandler.getTagValue(fnode, "length"), -1);
				fieldPrecision[i] = Const.toInt(
						XMLHandler.getTagValue(fnode, "precision"), -1);
				trimType[i] = ValueMeta.getTrimTypeByCode(XMLHandler
						.getTagValue(fnode, "trim_type"));

				if (fieldType[i] == ValueMetaInterface.TYPE_NONE) {
					fieldType[i] = ValueMetaInterface.TYPE_STRING;
				}
			}
		} catch (Exception e) {
			throw new KettleException(
					"Unable to read step information from XML", e);
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
			throw new KettleException("DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {

					inputRowMeta = rowMeta.clone();
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					conversionMeta = outputRowMeta.clone();
//					for (ValueMetaInterface valueMeta : conversionMeta
//							.getValueMetaList()) {
//						valueMeta.setType(ValueMetaInterface.TYPE_STRING);
//					}

					if (fromIndex == null) {
						fromIndex = new int[getFieldName().length];
						for (int i = 0; i < getFieldName().length; i++) {
							if (getVariableString() != null) {

								fromIndex[i] = inputRowMeta
										.indexOfValue(getVariableString()[i]);

								if (fromIndex[i] < 0) {
									throw new KettleException(
											"NO FOUND　From Column!");
								}
							} else {
								throw new KettleException("NO FOUND　Variables!");
							}
						}
					}

					first = false;
				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {

			Object[] extraData = new Object[getFieldName().length];
			for (int i = 0; i < getFieldName().length; i++) {
				String aimFieldName = getFieldName()[i];
				String varType = getVariableType()[i];// 参数类型值，如男，女的类型值为SEX

				String dimFront = getFieldFormat()[i];// 维度前缀值，用于获取 维度不同字段

				// 来源字段 字段可以是 （字段.小字段 ）这种格式的 用于对databasejoinlist这种进行处理
				String fromField = environmentSubstitute(getVariableString()[i]);
				// 来源小字段
				String fromSecField = environmentSubstitute(getSecvariableString()[i]);

				String defaultValue = environmentSubstitute(getVariableType()[i]);
				// 来源字段值 大类值
				Object fieldValue = rowData[fromIndex[i]];

				// 根据来源字段 fieldValue(值：1)
				if (!Const.isEmpty(fromSecField)) {
					// 有小类，改变值
					if (fieldValue instanceof List) {
						// list<Map<Object, Object> >
						List<Map<Object, Object>> fieldMapValueList = (List<Map<Object, Object>>) fieldValue;
						if (!Const.isEmpty(fieldMapValueList)) {
							for (Map<Object, Object> fieldMapValue : fieldMapValueList) {
								Object kValue = fieldMapValue.get(fromSecField);// 值获取到了然后放入新值
								// kValue varType
								if (!Const.isEmpty(dimFront)) {
									kValue = dimFront + "#" + kValue; // 加入前缀
								}
								Object keyValue = dimApp.get(kValue + "");
								if (keyValue == null) {
									keyValue = defaultValue;
								}
								fieldMapValue.put(aimFieldName, keyValue);
							}
							rowData[fromIndex[i]] = fieldMapValueList;
						}

					} else if (fieldValue instanceof Map) {
						// Map<Object, Object>
						Map<Object, Object> fieldMapValue = (Map<Object, Object>) fieldValue;
						// 先查找里面的数据，然后添加新数据
						Object kValue = fieldMapValue.get(fromSecField);// 值获取到了然后放入新值
						if (!Const.isEmpty(dimFront)) {
							kValue = dimFront + "#" + kValue; // 加入前缀
						}
						Object keyValue = dimApp.get(kValue + "");
						if (keyValue == null) {
							keyValue = defaultValue;
						}
						fieldMapValue.put(aimFieldName, keyValue);
						rowData[fromIndex[i]] = fieldMapValue;

					} else {
						ConstLog.message("源字段值格式不符合要求");
					}
				} else {
					// 没有小类
					// fieldValue 字段值 varType类型代码值
					ValueMetaInterface targetMeta = outputRowMeta
							.getValueMeta(inputRowMeta.size() + i);
					ValueMetaInterface sourceMeta = conversionMeta
							.getValueMeta(inputRowMeta.size() + i);
					if (!Const.isEmpty(dimFront)) {
						fieldValue = dimFront + "#" + fieldValue; // 加入前缀
					}
					Object keyValue = dimApp.get(fieldValue + "");
					if (keyValue == null) {
						keyValue = defaultValue;
					}

					extraData[i] = targetMeta.convertData(sourceMeta, keyValue);

				}
			}
			rowData = RowDataUtil.addRowData(rowData, inputRowMeta.size(),
					extraData);

			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(rowData);
			pReturn.setRowMeta(outputRowMeta.clone());
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;

		} catch (KettleException e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException("SET Variable Error", e); //$NON-NLS-1$
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, rowData, 1, errorMessage, null,
						"IS_E001");
			}
		}

		return null;
	}

	private String[] getFieldName() {
		return fieldName;
	}

	private String[] getVariableString() {
		return variableString;
	}

	private String[] getSecvariableString() {
		return secvariableString;
	}

	private String[] getVariableType() {
		return variableType;
	}

	private int[] getFieldType() {
		return fieldType;
	}

	private String[] getFieldFormat() {
		return fieldFormat;
	}

	private int[] getFieldLength() {
		return fieldLength;
	}

	private int[] getFieldPrecision() {
		return fieldPrecision;
	}

	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {
		int length = -1;
		for (int i = 0; i < fieldName.length; i++) {
			if (variableString[i] != null) {
				String string = environmentSubstitute(variableString[i]);
				if (string.length() > length)
					length = string.length();
			}
		}

		RowMetaInterface row = new RowMeta();
		for (int i = 0; i < fieldName.length; i++) {
			if (!Const.isEmpty(this.secvariableString[i])) {
				// 小类字段时 总字段不记录
				continue;
			}
			ValueMetaInterface v = new ValueMeta(fieldName[i], fieldType[i]);
			if (fieldLength[i] < 0)
				v.setLength(length);
			else
				v.setLength(fieldLength[i]);
			if (fieldPrecision[i] >= 0)
				v.setPrecision(fieldPrecision[i]);
			v.setConversionMask(fieldFormat[i]);
			v.setGroupingSymbol(group[i]);
			v.setDecimalSymbol(decimal[i]);
			v.setCurrencySymbol(currency[i]);
			v.setTrimType(trimType[i]);
			v.setOrigin(name);

			row.addValueMeta(v);
		}

		inputRowMeta.mergeRowMeta(row);
	}

	public String getTypeid() {
		return typeid;
	}
}

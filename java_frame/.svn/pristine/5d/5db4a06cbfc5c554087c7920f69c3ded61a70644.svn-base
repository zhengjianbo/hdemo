package com.ram.kettle.steps.impl;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class SetValue extends StepMeta implements StepInterface {
	private static Class<?> PKG = SetValue.class;
	private final String typeid = "SetRow";

	public RowMetaInterface outputRowMeta;
	public RowMetaInterface outputMeta;

	public int keynr;

	private static final String variableTypeCode[] = ValueMeta.getAllTypes();

	private String fieldName[]; // 输出字段
	private String variableName[]; // 来源字段
	private int variableType[]; // 输出字段类型
	private String outColumn;
	// 来源字段
	public int sourceIndexs[];

	public SetValue() {
		super();
	}

	public SetValue(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int count) {
		fieldName = new String[count];
		variableName = new String[count];
		variableType = new int[count];
		outColumn = "";
	}

	public Object clone() {
		SetValue retval = (SetValue) super.clone();

		int count = fieldName.length;

		retval.allocate(count);

		for (int i = 0; i < count; i++) {
			retval.fieldName[i] = fieldName[i];
			retval.variableName[i] = variableName[i];
			retval.variableType[i] = variableType[i];
		}
		retval.outColumn = outColumn;
		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);

		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields"); //$NON-NLS-1$
			int count = XMLHandler.countNodes(fields, "field"); //$NON-NLS-1$

			allocate(count);

			for (int i = 0; i < count; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i); //$NON-NLS-1$

				fieldName[i] = XMLHandler.getTagValue(fnode, "field_name"); //$NON-NLS-1$
				variableName[i] = XMLHandler
						.getTagValue(fnode, "variable_name"); //$NON-NLS-1$
				variableType[i] = getVariableType(XMLHandler.getTagValue(fnode,
						"variable_type"));
			}
			outColumn = XMLHandler.getTagValue(stepnode, "outColumn");
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages
							.getString(PKG,
									"SetVariableMeta.RuntimeError.UnableToReadXML.SETVARIABLE0004"), e); //$NON-NLS-1$
		}
	}

	public static final int getVariableType(String variableType) {
		return ValueMeta.getType(variableType);
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

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] row)
			throws KettleException {
		if (row == null) {
			throw new KettleException("DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {

					getSelectFields(rowMeta, null);

					outputRowMeta = (RowMetaInterface) rowMeta.clone();
					getFields(outputRowMeta, getStepname());

				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {
			// 获取字段值
			// 获取字段值
			Map<Object, Object> mapDataRow = new HashMap<Object, Object>();

			int outputIndex = 0;
			for (int idx : sourceIndexs) {
				Object valueData = row[idx];
				mapDataRow.put(getFieldName()[outputIndex++], valueData);
			}
			// 返回一个object
			row = RowDataUtil.addRowData(row, rowMeta.size(),
					new Object[] { mapDataRow });

			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(row);
			pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
			pReturn.setNextStream(getNextStepName());
			return pReturn;
		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException(BaseMessages.getString(PKG,
						"NUMBERRANGE ERROR"), e);
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, row, 1, errorMessage, null,
						"IS_E001");
			}
		}

		return null;
	}

	private String[] getFieldName() {
		return fieldName;
	}

	public String getTypeid() {
		return typeid;
	}

	public void getSelectFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {
		if (variableName != null && variableName.length > 0) // SELECT values
		{
			sourceIndexs = new int[variableName.length];
			for (int i = 0; i < variableName.length; i++) {
				int v = inputRowMeta.indexOfValue(variableName[i]);
				if (v > -1) {
					sourceIndexs[i] = v;
				} else {
					throw new KettleException("No found column "
							+ variableName[i]);
				}
			}
		}
	}

	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {

		ValueMetaInterface v = new ValueMeta(outColumn,
				ValueMeta.TYPE_SERIALIZABLE);
		v.setOrigin(name);
		inputRowMeta.addValueMeta(v);

	}

}

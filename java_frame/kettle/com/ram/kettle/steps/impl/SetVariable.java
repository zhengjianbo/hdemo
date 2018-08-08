package com.ram.kettle.steps.impl;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.ram.kettle.database.CacheApplication;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepErrorMeta;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.xml.XMLHandler;

public class SetVariable extends StepMeta implements StepInterface {
	private static Class<?> PKG = SetVariable.class;
	private final String typeid = "SetVariable";

	public static final int VARIABLE_TYPE_JVM = 0;
	public static final int VARIABLE_TYPE_PARENT_JOB = 1;
	public static final int VARIABLE_TYPE_GRAND_PARENT_JOB = 2;
	public static final int VARIABLE_TYPE_ROOT_JOB = 3;

	private static final String variableTypeCode[] = { "JVM", "PARENT_JOB",
			"GP_JOB", "ROOT_JOB" };
	private static final String variableTypeDesc[] = {
			"Valid in the Java Virtual Machine", "Valid in the parent job",
			"Valid in the grand-parent job", "Valid in the root job" };

	private String fieldName[];
	private String variableName[];
	private int variableType[];
	private String defaultValue[];

	private boolean usingFormatting;

	public int keynr;
	public int[] fromIndex = null;
	public RowMetaInterface outputMeta;
	public RowMetaInterface inputRowMeta;

	public final static String ACTIONCODE = "ECODE";
	public final static String ACTIONMSG = "EMSG";
	public String actionCode = ACTIONCODE;
	public String actionMessage = ACTIONMSG;

	private CacheApplication dimApp = CacheApplication.getInstanceSingle();

	public static final int getVariableType(String variableType) {
		for (int i = 0; i < variableTypeCode.length; i++) {
			if (variableTypeCode[i].equalsIgnoreCase(variableType))
				return i;
		}
		for (int i = 0; i < variableTypeDesc.length; i++) {
			if (variableTypeDesc[i].equalsIgnoreCase(variableType))
				return i;
		}
		return VARIABLE_TYPE_JVM;
	}

	public SetVariable() {
		super();
	}

	public SetVariable(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int count) {
		fieldName = new String[count];
		variableName = new String[count];
		variableType = new int[count];
		defaultValue = new String[count];
	}

	public Object clone() {
		SetVariable retval = (SetVariable) super.clone();

		int count = fieldName.length;

		retval.allocate(count);

		for (int i = 0; i < count; i++) {
			retval.fieldName[i] = fieldName[i];
			retval.variableName[i] = variableName[i];
			retval.variableType[i] = variableType[i];
			retval.defaultValue[i] = defaultValue[i];
		}

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
						"variable_type")); //$NON-NLS-1$
				defaultValue[i] = XMLHandler
						.getTagValue(fnode, "default_value"); //$NON-NLS-1$
			}

			// Default to "N" for backward compatibility
			//
			usingFormatting = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "use_formatting")); //$NON-NLS-1$
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages
							.getString(PKG,
									"SetVariableMeta.RuntimeError.UnableToReadXML.SETVARIABLE0004"), e); //$NON-NLS-1$
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
					outputMeta = rowMeta.clone();
					getFields(outputMeta, getStepname());

					StepErrorMeta errStepMeta = this.getStepMeta()
							.getStepErrorMeta();
					if (errStepMeta != null) {
						actionCode = errStepMeta.getErrorCodesValuename();
						actionMessage = errStepMeta
								.getErrorDescriptionsValuename();
					}

					if (fromIndex == null) {
						fromIndex = new int[getFieldName().length];
						for (int i = 0; i < getFieldName().length; i++) {

							fromIndex[i] = inputRowMeta
									.indexOfValue(getFieldName()[i]);
							if (fromIndex[i] < 0) {
								throw new KettleException(
										"NO FOUND　From Column!");
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

			for (int i = 0; i < getFieldName().length; i++) {
				// 大类字段
				// String fromField =
				// environmentSubstitute(meta.getFieldName()[i]);
				// 来源小字段KEY
				String secKeyField = environmentSubstitute(getVariableName()[i]);
				// 来源小字段Value
				String secValueField = environmentSubstitute(getDefaultValue()[i]);
				// 内存类型
				int cacheType = getVariableType()[i];

				// 来源字段值 大类值
				Object fieldValue = rowData[fromIndex[i]];

				if (fieldValue instanceof List) {
					// list<Map<Object, Object> >
					List<Map<Object, Object>> fieldMapValueList = (List<Map<Object, Object>>) fieldValue;
					if (!Const.isEmpty(fieldMapValueList)) {
						for (Map<Object, Object> fieldMapValue : fieldMapValueList) {
							Object sKey = fieldMapValue.get(secKeyField);

							try {
								// 设置值
								fieldMapValue.put(actionCode, 1000);
								fieldMapValue.put(actionMessage,
										"SET VALUE INTO CACHE");
								if (Const.isEmpty(secValueField)) {
									dimApp.put(sKey + "", fieldMapValue);
								} else {
									Object sValue = fieldMapValue
											.get(secValueField);
									dimApp.put(sKey + "", sValue);
								}
							} catch (Exception ex) {
								fieldMapValue.put(actionCode, 1001);
								fieldMapValue.put(actionMessage,
										ex.getMessage());
							}
						}
						rowData[fromIndex[i]] = fieldMapValueList;
					}

				} else if (fieldValue instanceof Map) {
					// Map<Object, Object>
					Map<Object, Object> fieldMapValue = (Map<Object, Object>) fieldValue;
					// 先查找里面的数据，然后添加新数据
					Object sKey = fieldMapValue.get(secKeyField);

					try {
						// 设置值
						fieldMapValue.put(actionCode, 1000);
						fieldMapValue.put(actionMessage, "SET VALUE NULL");
						if (Const.isEmpty(secValueField)) {
							// 入整行记录
							dimApp.put(sKey + "", fieldMapValue);
						} else {
							Object sValue = fieldMapValue.get(secValueField);
							dimApp.put(sKey + "", sValue);
						}
					} catch (Exception ex) {
						fieldMapValue.put(actionCode, 1001);
						fieldMapValue.put(actionMessage, ex.getMessage());
					}
				} else {
					ConstLog.message("源字段值格式不符合要求");
				}
			}
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(rowData);
			pReturn.setRowMeta(RowMeta.clone(outputMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;
		} catch (Exception e) {
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

	private String[] getDefaultValue() {
		return defaultValue;
	}

	private int[] getVariableType() {
		return variableType;
	}

	private String[] getVariableName() {
		return variableName;
	}

	private String[] getFieldName() {
		return fieldName;
	}

	public String getTypeid() {
		return typeid;
	}
}

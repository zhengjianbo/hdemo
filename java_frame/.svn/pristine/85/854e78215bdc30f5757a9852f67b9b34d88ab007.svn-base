package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class SetValueField extends StepMeta implements StepInterface {
	private static Class<?> PKG = SetValueField.class;
	private final String typeid = "SetValueField";

	private String fieldName[];
	private String replaceByFieldValue[];

	public RowMetaInterface outputRowMeta;
	public int inputColumnNr;

	public RowMetaInterface convertRowMeta;

	public int indexOfField[];
	public int indexOfReplaceByValue[];

	public SetValueField() {
		super();
	}

	public SetValueField(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void getFields(RowMetaInterface row, String name)
			throws KettleException {

	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	public void allocate(int count) {
		fieldName = new String[count];
		replaceByFieldValue = new String[count];
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields"); //$NON-NLS-1$
			int count = XMLHandler.countNodes(fields, "field"); //$NON-NLS-1$

			allocate(count);

			for (int i = 0; i < count; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i); //$NON-NLS-1$

				fieldName[i] = XMLHandler.getTagValue(fnode, "name"); //$NON-NLS-1$
				replaceByFieldValue[i] = XMLHandler.getTagValue(fnode,
						"replaceby"); //$NON-NLS-1$
			}

		} catch (Exception e) {
			throw new KettleException("Unable to read step info from XML node",
					e);
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) {
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
					outputRowMeta = (RowMetaInterface) rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					indexOfField = new int[getFieldName().length];
					indexOfReplaceByValue = new int[getFieldName().length];

					for (int i = 0; i < getFieldName().length; i++) {
						// Check if this field was specified only one time
						for (int j = 0; j < getFieldName().length; j++) {
							if (getFieldName()[j].equals(getFieldName()[i])) {
								if (j != i)
									throw new KettleException(
											BaseMessages
													.getString(
															PKG,
															"SetValueField.Log.FieldSpecifiedMoreThatOne",
															getFieldName()[i],
															"" + i, "" + j));
							}
						}

						indexOfField[i] = outputRowMeta
								.indexOfValue(environmentSubstitute(getFieldName()[i]));
						if (indexOfField[i] < 0) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"SetValueField.Log.CouldNotFindFieldInRow", getFieldName()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
						String sourceField = environmentSubstitute(getReplaceByFieldValue()[i]);
						if (Const.isEmpty(sourceField)) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"SetValueField.Log.ReplaceByValueFieldMissing",
													"" + i));
						}
						indexOfReplaceByValue[i] = outputRowMeta
								.indexOfValue(sourceField);
						if (indexOfReplaceByValue[i] < 0) {
							throw new KettleException(BaseMessages.getString(
									PKG,
									"SetValueField.Log.CouldNotFindFieldInRow",
									sourceField));
						}
						// Compare fields type
						ValueMetaInterface SourceValue = rowMeta
								.getValueMeta(indexOfField[i]);
						ValueMetaInterface ReplaceByValue = rowMeta
								.getValueMeta(indexOfReplaceByValue[i]);

						if (SourceValue.getType() != ReplaceByValue.getType()) {
							String err = BaseMessages.getString(
									PKG,
									"SetValueField.Log.FieldsTypeDifferent",
									SourceValue.getName() + " ("
											+ SourceValue.getTypeDesc() + ")",
									ReplaceByValue.getName() + " ("
											+ ReplaceByValue.getTypeDesc()
											+ ")");
							throw new KettleException(err);
						}
					}
					first = false;
				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {
			for (int i = 0; i < indexOfField.length; i++) {
				row[indexOfField[i]] = row[indexOfReplaceByValue[i]];
			}
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(RowDataUtil.createResizedCopy(row, row.length));
			pReturn.setRowMeta(outputRowMeta.clone());
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

	public String[] getFieldName() {
		return fieldName;
	}

	public void setFieldName(String[] fieldName) {
		this.fieldName = fieldName;
	}

	public String[] getReplaceByFieldValue() {
		return replaceByFieldValue;
	}

	public void setReplaceByFieldValue(String[] replaceByFieldValue) {
		this.replaceByFieldValue = replaceByFieldValue;
	}

	public String getTypeid() {
		return typeid;
	}

}

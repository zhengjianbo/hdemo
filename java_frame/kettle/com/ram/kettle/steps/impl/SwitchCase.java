package com.ram.kettle.steps.impl;

import java.util.HashMap;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class SwitchCase extends StepMeta implements StepInterface {
	private static Class<?> PKG = SwitchCase.class;
	private final String typeid = "SwitchCase";

	private static final String XML_TAG_CASE_VALUES = "cases";
	private static final String XML_TAG_CASE_VALUE = "case";

	/** The field to switch over */
	private String fieldname;

	/** The case value type to help parse numeric and date-time data */
	private int caseValueType;
	/** The case value format to help parse numeric and date-time data */
	private String caseValueFormat;
	/** The decimal symbol to help parse numeric data */
	private String caseValueDecimal;
	private String caseValueGroup;

	/** The default target step name (only used during serialization) */
	private String defaultTargetStepname;

	/** True if the comparison is a String.contains instead of equals */
	private boolean isContains;

	public RowMetaInterface outputRowMeta;
	public ValueMetaInterface valueMeta;
	public int fieldIndex;
	public ValueMetaInterface inputValueMeta;
	public ValueMetaInterface stringValueMeta;

	public String nullStep;

	protected HashMap<Object, String> map = new HashMap<Object, String>();

	public String get(Object key) {
		return map.get(key);
	}

	public void put(Object key, String rowSet) {
		map.put(key, rowSet);
	}

	public SwitchCase() {
		super();
	}

	public SwitchCase(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate() {
	}

	public Object clone() {
		SwitchCase retval = (SwitchCase) super.clone();

		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			fieldname = XMLHandler.getTagValue(stepnode, "fieldname"); //$NON-NLS-1$
			isContains = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "use_contains")); //$NON-NLS-1$
			caseValueType = ValueMeta.getType(XMLHandler.getTagValue(stepnode,
					"case_value_type")); //$NON-NLS-1$
			caseValueFormat = XMLHandler.getTagValue(stepnode,
					"case_value_format"); //$NON-NLS-1$
			caseValueDecimal = XMLHandler.getTagValue(stepnode,
					"case_value_decimal"); //$NON-NLS-1$
			caseValueGroup = XMLHandler.getTagValue(stepnode,
					"case_value_group"); //$NON-NLS-1$

			defaultTargetStepname = XMLHandler.getTagValue(stepnode,
					"default_target_step"); // $NON-NLS-1$

			Node casesNode = XMLHandler.getSubNode(stepnode,
					XML_TAG_CASE_VALUES);
			int nrCases = XMLHandler.countNodes(casesNode, XML_TAG_CASE_VALUE);
			allocate();
			for (int i = 0; i < nrCases; i++) {
				Node caseNode = XMLHandler.getSubNodeByNr(casesNode,
						XML_TAG_CASE_VALUE, i);
				String caseValue = XMLHandler.getTagValue(caseNode, "value");
				String caseTargetStepname = XMLHandler.getTagValue(caseNode,
						"target_step");
				this.put(caseValue, caseTargetStepname);
			}

			if (Const.isEmpty(getFieldname())) {
				throw new KettleException(
						BaseMessages
								.getString(PKG,
										"SwitchCaseMeta.Exception..UnableToLoadStepInfoFromXML"));
			}

			valueMeta = new ValueMeta(getFieldname(), getCaseValueType());
			valueMeta.setConversionMask(getCaseValueFormat());
			valueMeta.setGroupingSymbol(getCaseValueGroup());
			valueMeta.setDecimalSymbol(getCaseValueDecimal());

			stringValueMeta = valueMeta.clone();
			stringValueMeta.setType(ValueMetaInterface.TYPE_STRING);

		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"SwitchCaseMeta.Exception..UnableToLoadStepInfoFromXML"), e); //$NON-NLS-1$
		}
	}

	private int getCaseValueType() {
		return caseValueType;
	}

	private String getCaseValueFormat() {
		return caseValueFormat;
	}

	private String getCaseValueDecimal() {
		return caseValueDecimal;
	}

	private String getCaseValueGroup() {
		return caseValueGroup;
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

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {

		if (r == null) {
			throw new KettleException("DATA ERROR");
		}

		if (first) {
			synchronized (this) {
				if (first) {

					outputRowMeta = rowMeta.clone();

					getFields(rowMeta, getStepname());

					fieldIndex = rowMeta.indexOfValue(getFieldname());
					if (fieldIndex < 0) {
						throw new KettleException(
								BaseMessages
										.getString(
												PKG,
												"SwitchCase.Exception.UnableToFindFieldName", getFieldname())); //$NON-NLS-1$
					}

					inputValueMeta = rowMeta.getValueMeta(fieldIndex);

					first = false;
				}
			}
		}

		Object lookupData = valueMeta
				.convertData(inputValueMeta, r[fieldIndex]);

		String nextStep = null;

		if (inputValueMeta.isNull(lookupData)) {
			nextStep = nullStep;
		} else {
			nextStep = get(lookupData);
		}
		if (nextStep == null) {
			if (!Const.isEmpty(defaultTargetStepname)) {
				ProcessReturn pReturn = new ProcessReturn();
				pReturn.setRow(r);
				pReturn.setRowMeta(outputRowMeta.clone());
				pReturn.setNextStream(defaultTargetStepname);
				return pReturn;
			}
		} else {
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(r);
			pReturn.setRowMeta(outputRowMeta.clone());
			pReturn.setNextStream(nextStep);
			return pReturn;
		}
		return null;
	}

	private String getFieldname() {
		return fieldname;
	}

	public String getTypeid() {
		return typeid;
	}
}

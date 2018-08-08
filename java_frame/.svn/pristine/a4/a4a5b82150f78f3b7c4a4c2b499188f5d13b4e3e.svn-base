package com.ram.kettle.steps.impl;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.steps.NumberRangeRule;
import com.ram.kettle.steps.NumberRangeSet;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class NumberRange extends StepMeta implements StepInterface {
	private static Class<?> PKG = NumberRange.class;
	private final String typeid = "NumberRange";

	private String inputField;
	private String outputField;
	private String fallBackValue;

	private List<NumberRangeRule> rules;

	private NumberRangeSet numberRange;

	public RowMetaInterface outputRowMeta;
	public int inputColumnNr;

	public NumberRange() {
		super();
	}

	public NumberRange(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void getFields(RowMetaInterface row, String name)
			throws KettleException {
		ValueMetaInterface mcValue = new ValueMeta(outputField,
				ValueMetaInterface.TYPE_STRING);
		mcValue.setOrigin(name);
		mcValue.setLength(255);
		row.addValueMeta(mcValue);
	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	public void allocate(int nrFields) {

	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			inputField = XMLHandler.getTagValue(stepnode, "inputField");
			outputField = XMLHandler.getTagValue(stepnode, "outputField");

			emptyRules();
			String fallBackValue = XMLHandler.getTagValue(stepnode,
					"fallBackValue");
			setFallBackValue(fallBackValue);

			Node fields = XMLHandler.getSubNode(stepnode, "rules"); //$NON-NLS-1$
			int count = XMLHandler.countNodes(fields, "rule"); //$NON-NLS-1$
			for (int i = 0; i < count; i++) {

				Node fnode = XMLHandler.getSubNodeByNr(fields, "rule", i); //$NON-NLS-1$

				String lowerBoundStr = XMLHandler.getTagValue(fnode,
						"lower_bound"); //$NON-NLS-1$
				String upperBoundStr = XMLHandler.getTagValue(fnode,
						"upper_bound"); //$NON-NLS-1$
				String value = XMLHandler.getTagValue(fnode, "value"); //$NON-NLS-1$

				double lowerBound = Double.parseDouble(lowerBoundStr);
				double upperBound = Double.parseDouble(upperBoundStr);
				addRule(lowerBound, upperBound, value);
			}

		} catch (Exception e) {
			throw new KettleException("Unable to read step info from XML node",
					e);
		}
	}

	public void setFallBackValue(String fallBackValue) {
		this.fallBackValue = fallBackValue;
	}

	public void addRule(double lowerBound, double upperBound, String value) {
		NumberRangeRule rule = new NumberRangeRule(lowerBound, upperBound,
				value);
		rules.add(rule);
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

					numberRange = new NumberRangeSet(getRules(),
							getFallBackValue());
					outputRowMeta = (RowMetaInterface) rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					inputColumnNr = outputRowMeta.indexOfValue(getInputField());

					if (inputColumnNr < 0) {

						throw new KettleException(
								"Field for input could not be found: "
										+ getInputField());
					}
				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {
			Double value = rowMeta.getNumber(row, inputColumnNr);
			String ranges = numberRange.evaluate(value);

			row = RowDataUtil.addRowData(row, rowMeta.size(),
					new Object[] { ranges });

			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(row);
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

	private String getInputField() {
		return inputField;
	}

	private String getFallBackValue() {
		return fallBackValue;
	}

	private List<NumberRangeRule> getRules() {
		return rules;
	}

	public String getTypeid() {
		return typeid;
	}

	public void emptyRules() {
		rules = new LinkedList<NumberRangeRule>();
	}
}

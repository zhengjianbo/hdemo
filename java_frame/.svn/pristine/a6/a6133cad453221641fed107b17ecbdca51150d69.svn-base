package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Condition;
import com.ram.kettle.value.ValueMetaAndData;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class FilterRows extends StepMeta implements StepInterface {
	private static Class<?> PKG = FilterRows.class;
	private final String typeid = "FilterRows";

	private Condition condition;

	public RowMetaInterface outputRowMeta;
	public boolean chosesTargetSteps;
	public String trueStepname;
	public String falseStepname;

	public FilterRows() {
		super();
	}

	public FilterRows(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate() {
		condition = new Condition();
	}

	public Object clone() {
		FilterRows retval = (FilterRows) super.clone();

		if (condition != null) {
			retval.condition = (Condition) condition.clone();
		} else {
			retval.condition = null;
		}

		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			// List<StreamInterface> targetStreams = getStepIOMeta()
			// .getTargetStreams();
			trueStepname = XMLHandler.getTagValue(stepnode, "send_true_to");
			falseStepname = XMLHandler.getTagValue(stepnode, "send_false_to");
			//			targetStreams.get(0).setSubject(trueStepname); //$NON-NLS-1$
			//			targetStreams.get(1).setSubject(falseStepname); //$NON-NLS-1$

			chosesTargetSteps = trueStepname != null && falseStepname != null;

			Node compare = XMLHandler.getSubNode(stepnode, "compare"); //$NON-NLS-1$
			Node condnode = XMLHandler.getSubNode(compare, "condition"); //$NON-NLS-1$

			// The new situation...
			if (condnode != null) {
				condition = new Condition(condnode);
			} else {
				condition = new Condition();

				int nrkeys = XMLHandler.countNodes(compare, "key"); //$NON-NLS-1$
				if (nrkeys == 1) {
					Node knode = XMLHandler.getSubNodeByNr(compare, "key", 0); //$NON-NLS-1$

					String key = XMLHandler.getTagValue(knode, "name"); //$NON-NLS-1$
					String value = XMLHandler.getTagValue(knode, "value"); //$NON-NLS-1$
					String field = XMLHandler.getTagValue(knode, "field"); //$NON-NLS-1$
					String comparator = XMLHandler.getTagValue(knode,
							"condition"); //$NON-NLS-1$

					condition.setOperator(Condition.OPERATOR_NONE);
					condition.setLeftValuename(key);
					condition.setFunction(Condition.getFunction(comparator));
					condition.setRightValuename(field);
					condition
							.setRightExact(new ValueMetaAndData("value", value)); //$NON-NLS-1$
				} else {
					for (int i = 0; i < nrkeys; i++) {
						Node knode = XMLHandler.getSubNodeByNr(compare,
								"key", i); //$NON-NLS-1$

						String key = XMLHandler.getTagValue(knode, "name"); //$NON-NLS-1$
						String value = XMLHandler.getTagValue(knode, "value"); //$NON-NLS-1$
						String field = XMLHandler.getTagValue(knode, "field"); //$NON-NLS-1$
						String comparator = XMLHandler.getTagValue(knode,
								"condition"); //$NON-NLS-1$

						Condition subc = new Condition();
						if (i > 0)
							subc.setOperator(Condition.OPERATOR_OR);
						else
							subc.setOperator(Condition.OPERATOR_NONE);
						subc.setLeftValuename(key);
						subc.setFunction(Condition.getFunction(comparator));
						subc.setRightValuename(field);
						subc.setRightExact(new ValueMetaAndData("value", value)); //$NON-NLS-1$

						condition.addCondition(subc);
					}
				}
			}

		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG,
							"StepMeta.Exception.UnableToLoadStepInfo") + e.toString(), e); //$NON-NLS-1$
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) { 
//			ConstLog.message("==" + this.getTypeId() + "=初始化"
//					+ this.getName()); 
			return true;
		}
		return false;
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
					getFields(outputRowMeta, getStepname());

					if (chosesTargetSteps) {
						// List<StreamInterface> targetStreams = getStepIOMeta()
						// .getTargetStreams();
						// 查找true step ,false step
					}
					first = false;
				}
			}
		}

		boolean keep = keepRow(outputRowMeta, r);
		if (!chosesTargetSteps) {
			if (keep) {

				ProcessReturn pReturn = new ProcessReturn();
				pReturn.setRow(r);
				pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
				pReturn.setNextStream(this.getNextStepName());
				return pReturn;
			}
		} else {
			if (keep) {

				ProcessReturn pReturn = new ProcessReturn();
				pReturn.setRow(r);
				pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
				pReturn.setNextStream(trueStepname);
				return pReturn;
			} else {
				ProcessReturn pReturn = new ProcessReturn();
				pReturn.setRow(r);
				pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
				pReturn.setNextStream(falseStepname);
				return pReturn;
			}
		}
		return null;
	}

	private boolean keepRow(RowMetaInterface rowMeta, Object[] row)
			throws KettleException {
		try {
			return condition.evaluate(rowMeta, row);
		} catch (Exception e) {
			String message = BaseMessages
					.getString(PKG,
							"FilterRows.Exception.UnexpectedErrorFoundInEvaluationFuction"); //$NON-NLS-1$

			throw new KettleException(message, e);
		}
	}

	public void getFields(RowMetaInterface rowMeta, String name)
			throws KettleException {
		String conditionField[] = condition.getUsedFields();
		for (int i = 0; i < conditionField.length; i++) {
			int idx = rowMeta.indexOfValue(conditionField[i]);
			if (idx >= 0) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(idx);
				valueMeta.setSortedDescending(false);
			}
		}
	}

}

package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;

public class Abort extends StepMeta implements StepInterface {

	private final String typeid = "Abort";

	public RowMetaInterface outputRowMeta;

	public Abort() {
		super();
	}

	public Abort(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void getFields(RowMetaInterface rowMeta, String origin)
			throws KettleException {

	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	public void allocate(int nrparam) {

	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {

		} catch (Exception e) {
			throw new KettleException("Unable to load step info from XML", e);
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

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta,
			Object[] rowData) throws KettleException {
		if (rowData == null) {
			throw new KettleException("Abort DATA ERROR");
		}
		boolean sendToErrorRow = false;
		String errorMessage = null;
		try {
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(rowData);
			pReturn.setRowMeta(RowMeta.clone(rowMeta));
			pReturn.setNextStream(null);
			pReturn.setAbortStream(null);
			return pReturn;
		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException("Abort ERROR"); //$NON-NLS-1$
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, rowData, 1, errorMessage, null,
						"IS_E001");
			}
		}

		return null;
	}

	public String getTypeid() {
		return typeid;
	}

}

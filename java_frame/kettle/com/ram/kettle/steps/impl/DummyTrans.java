package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;

public class DummyTrans extends StepMeta implements StepInterface {
	private static Class<?> PKG = DummyTrans.class;
	private final String typeid = "Dummy";

	public DummyTrans() {
		super();
	}

	public DummyTrans(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrFields) {

	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);

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

		if (row == null) // no more input to be expected...
		{
			throw new KettleException("DATA ERROR(" + this.getStepname() + ")");
		}
		// 解析数据
		Object[] outputData = row;
		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(outputData);
		pReturn.setRowMeta(RowMeta.clone(rowMeta));
		pReturn.setNextStream(this.getNextStepName());
		return pReturn;
	}

	public String getTypeid() {
		return typeid;
	}
}

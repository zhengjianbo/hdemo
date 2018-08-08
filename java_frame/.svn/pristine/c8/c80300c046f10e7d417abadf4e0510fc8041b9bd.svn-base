package com.ram.kettle.step;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMetaInterface;

public interface StepInterface {

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] row)
			throws KettleException;
	public ProcessReturn processBatchRow(RowMetaInterface rowMeta, Object[] r,int dataIndex)
			throws KettleException ;
	 
	public boolean init();

	public String getStepname();

	public boolean isMapping(); 

	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException;
	 
	
}

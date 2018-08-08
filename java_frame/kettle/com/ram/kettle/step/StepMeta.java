package com.ram.kettle.step;

import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.tran.TransMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.xml.XMLHandler;

public class StepMeta implements Cloneable, Comparable<StepMeta>, StepInterface {
	private static Class<?> PKG = StepMeta.class;

	public static final String XML_TAG = "step";

	public static final String STRING_ID_MAPPING = "Mapping";
	public static final String STRING_ID_SINGLE_THREADER = "SingleThreader";
	public static final String STRING_ID_ETL_META_INJECT = "MetaInject";
	public static final String STRING_ID_MAPPING_INPUT = "MappingInput";
	public static final String STRING_ID_MAPPING_OUTPUT = "MappingOutput";

	private String stepid;
	private String stepname;
	private boolean distributes;
	private int copies;
	private String description;

	protected boolean first = true;

	private StepErrorMeta stepErrorMeta;

	private TransMeta parentTransMeta;

	public StepMeta(String stepid, String stepname) {
		this(stepname);
		if (this.stepid == null)
			this.stepid = stepid;
	}

	public StepMeta() {
		this("");
	}

	public StepMeta(String stepname) {

		this.stepname = stepname;

		distributes = true;
		copies = 1;
		description = null; 
	}
	public String getFolder(){
		return this.getParentTransMeta().getFolder();
	}
	public StepMeta(Node stepnode) throws KettleException {
		setNode(stepnode);
	}

	public void setNode(Node stepnode) throws KettleException {
		try {
			stepname = XMLHandler.getTagValue(stepnode, "name");
			stepid = XMLHandler.getTagValue(stepnode, "type");
			description = XMLHandler.getTagValue(stepnode, "description"); 
			
			copies = Const.toInt(XMLHandler.getTagValue(stepnode, "copies"), 1);
			String sdistri = XMLHandler.getTagValue(stepnode, "distribute");
			distributes = "Y".equalsIgnoreCase(sdistri); //$NON-NLS-1$
			if (sdistri == null)
				distributes = true;

		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG,
							"StepMeta.Exception.UnableToLoadStepInfo") + e.toString(), e); //$NON-NLS-1$
		}
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		StepMeta stepMeta = (StepMeta) obj;
		return getName().equalsIgnoreCase(stepMeta.getName());
	}

	public Object clone() {
		StepMeta stepMeta = new StepMeta();
		stepMeta.replaceMeta(this);
		return stepMeta;
	}

	public void replaceMeta(StepMeta stepMeta) {
		this.stepid = stepMeta.stepid;
		this.stepname = stepMeta.stepname;

		this.distributes = stepMeta.distributes;
		this.copies = stepMeta.copies;

		this.description = stepMeta.description;

		if (stepMeta.stepErrorMeta != null) {
			this.stepErrorMeta = stepMeta.stepErrorMeta.clone();
		}

	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] row)
			throws KettleException {
		return null;
	}
	public ProcessReturn processBatchRow(RowMetaInterface rowMeta, Object[] r,int dataIndex)
			throws KettleException {
		return null;
	}
	public void clearBatch() throws KettleException{
		
	}
	public boolean init() {
		return true;
	}

	public void dispose() {

	}

	@Override
	public String getStepname() {
		return stepname;
	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {

	}

	RowMetaInterface errorRowMetaSingle;
	private boolean isError = true;

	public ProcessReturn putErrorReturn(RowMetaInterface rowMeta, Object[] row,
			long nrErrors, String errorDescriptions, String fieldNames,
			String errorCodes) throws KettleException {
		if (isError) {
			// 只同步一次 只加载一次
			synchronized (this) {
				if (isError) {
					StepErrorMeta stepErrorMeta = this.getStepErrorMeta();

					if (errorRowMetaSingle == null) {
						errorRowMetaSingle = rowMeta.clone();
						RowMetaInterface add = stepErrorMeta.getErrorRowMeta(
								nrErrors, errorDescriptions, fieldNames,
								errorCodes);
						errorRowMetaSingle.addRowMeta(add);
					}
					isError = false;
				}
			}
		}
		Object[] errorRowData = RowDataUtil.allocateRowData(errorRowMetaSingle
				.size());
		if (row != null) {
			System.arraycopy(row, 0, errorRowData, 0, rowMeta.size());
		}
		stepErrorMeta.addErrorRowData(errorRowData, rowMeta.size(), nrErrors,
				errorDescriptions, fieldNames, errorCodes);

		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(errorRowData);
		pReturn.setRowMeta(errorRowMetaSingle.clone());
		pReturn.setNextStream(stepErrorMeta.getTargetStep().getName());
		return pReturn;

	}

	public String nextStep = null;

	public String getNextStepName() {
		try {
			if (!Const.isEmpty(nextStep)) {
				return nextStep;
			}
			String[] names = this.parentTransMeta.getNextStepNames(this);
			StepErrorMeta stepErrorMeta = this.getStepErrorMeta();
			String errStepName = "";
			if (stepErrorMeta != null && stepErrorMeta.getTargetStep() != null) {
				errStepName = stepErrorMeta.getTargetStep().getName();
			}
			for (String name : names) {
				if (name.equalsIgnoreCase(errStepName)) {
					continue;
				}
				nextStep = name;
				return nextStep;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	public static final StepMeta findStep(List<StepMeta> steps, String stepname) {
		if (steps == null)
			return null;

		for (StepMeta stepMeta : steps) {
			if (stepMeta.getName().equalsIgnoreCase(stepname))
				return stepMeta;
		}
		return null;
	}

	public StepMeta getStepMeta() {
		return this;
	}

	public int compareTo(StepMeta o) {
		return toString().compareTo(o.toString());
	}

	public String getStepID() {
		return stepid;
	}

	public String getName() {
		return stepname;
	}

	public void setName(String sname) {
		stepname = sname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		if (getName() == null)
			return getClass().getName();
		return getName();
	}

	public StepErrorMeta getStepErrorMeta() {
		return stepErrorMeta;
	}

	public void setStepErrorMeta(StepErrorMeta stepErrorMeta) {
		this.stepErrorMeta = stepErrorMeta;
	}

	public boolean supportsErrorHandling() {
		return true;
	}

	public boolean isDoingErrorHandling() {
		return true;
	}

	public String getTypeId() {
		return this.getStepID();
	}

	public boolean isMapping() {
		return STRING_ID_MAPPING.equals(stepid);
	}

	public boolean isSingleThreader() {
		return STRING_ID_SINGLE_THREADER.equals(stepid);
	}

	public boolean isEtlMetaInject() {
		return STRING_ID_ETL_META_INJECT.equals(stepid);
	}

	public boolean isMappingInput() {
		return STRING_ID_MAPPING_INPUT.equals(stepid);
	}

	public boolean isMappingOutput() {
		return STRING_ID_MAPPING_OUTPUT.equals(stepid);
	}

	public void setStepID(String stepid) {
		this.stepid = stepid;
	}

	public void setParentTransMeta(TransMeta parentTransMeta) { 
		this.parentTransMeta = parentTransMeta;
	}

	public TransMeta getParentTransMeta() {
		return parentTransMeta;
	}

	public String environmentSubstitute(String key) {
		return key;
	}
	
	public String getMakeCode() { 
		return "// "+ this.stepname +" 没有getMakeCode方法!\r\n";
	}

	// 返回代码 不走流程图 走代码
	public String getInitCode() { 
		return "// "+ this.stepname +" 没有getInitCode方法!\r\n";
	}

	public String getRunCode() {

		return "// "+ this.stepname +" 没有getRunCode方法!\r\n";
	}


	public  ProcessReturn process(StepInterface startCombi,
			RowMeta rowMeta, Object value[]) throws KettleException { 
		ProcessReturn pReturn = startCombi.processSingleRow(rowMeta, value);
		return pReturn;
	}
}

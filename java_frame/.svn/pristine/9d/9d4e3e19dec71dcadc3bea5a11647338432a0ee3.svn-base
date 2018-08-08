package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class Injector extends StepMeta implements StepInterface {
	private static Class<?> PKG = Injector.class;
	private final String typeid = "Injector";
	private String fieldname[];
	private int type[];

	private RowMetaInterface outRowMeta = new RowMeta();

	public Injector() {
		super();
	}

	public Injector(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrFields) {
		fieldname = new String[nrFields];
		type = new int[nrFields];
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields"); //$NON-NLS-1$
			int nrfields = XMLHandler.countNodes(fields, "field"); //$NON-NLS-1$

			allocate(nrfields);

			for (int i = 0; i < nrfields; i++) {
				Node line = XMLHandler.getSubNodeByNr(fields, "field", i); //$NON-NLS-1$
				fieldname[i] = XMLHandler.getTagValue(line, "name"); //$NON-NLS-1$
				type[i] = ValueMeta.getType(XMLHandler
						.getTagValue(line, "type")); //$NON-NLS-1$ 
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
			// ConstLog.message("=="+this.getTypeId()+"=初始化" + this.getName());
		}
		return true;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] row)
			throws KettleException {

		if (row == null) {
			throw new KettleException("DATA ERROR");
		}
		if (first) {
			// 初始化时候同步下
			synchronized (this) {
				if (first) {
					try {
						getFields(outRowMeta, getStepname());

						first = false;
					} catch (KettleException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// 解析数据
		Object[] outputData = row;
		outputData = selectValues(rowMeta, outputData);
		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(outputData);
		pReturn.setRowMeta(RowMeta.clone(outRowMeta));
		pReturn.setNextStream(this.getNextStepName());
		return pReturn;
	}

	private Object[] selectValues(RowMetaInterface rowMeta, Object[] rowData)
			throws KettleException {

		int[] fieldnrs = new int[getFieldname().length];
		for (int i = 0; i < fieldnrs.length; i++) {
			fieldnrs[i] = rowMeta.indexOfValue(getFieldname()[i]);
		}

		Object[] outputData = new Object[outRowMeta.size()];
		int outputIndex = 0;

		for (int idx : fieldnrs) {
			if (idx < 0) {
				outputData[outputIndex++] = null;
				continue;
			}
			if (idx < rowMeta.size()) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(idx);
				// 过滤特殊字符 用于过滤sql 
				outputData[outputIndex++] = (""+ valueMeta.cloneValueData(rowData[idx]));//Const.xssEncode
			} else {
				throw new KettleException("CouldNotFindFieldValue");
			}
		}
		return outputData;
	}

	public RowMetaInterface getProcessRowMeta() {
		return null;
	}

	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {
		for (int i = 0; i < this.fieldname.length; i++) {
			ValueMetaInterface v = new ValueMeta(this.fieldname[i], type[i],
					-1, -1);
			inputRowMeta.addValueMeta(v);
		}
	}

	public String[] getFieldname() {
		return fieldname;
	}

	public void setFieldname(String[] fieldname) {
		this.fieldname = fieldname;
	}

	public int[] getType() {
		return type;
	}

	public void setType(int[] type) {
		this.type = type;
	}

	public String getTypeid() {
		return typeid;
	}

	public String getMakeCode() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(" Injector injectMeta=new Injector();   ").append("\r\n");
		return buffer.toString();
	}

	// 返回代码 不走流程图 走代码
	public String getInitCode() {

		StringBuffer bufferx = new StringBuffer("");
		for (String field : fieldname) {
			bufferx.append("\"" + field + "\",");
		}

		StringBuffer buffery = new StringBuffer("");
		for (int fieldType : type) {
			buffery.append(fieldType + ",");
		}

		StringBuffer buffer = new StringBuffer("");
		buffer.append("  injectMeta.setFieldname(new String[]{")
				.append(bufferx).append(" });   ").append("\r\n");
		buffer.append("  injectMeta.setType(new int[]{ ").append(buffery)
				.append(" });   ").append("\r\n");

		buffer.append("  ").append("\r\n");
		return buffer.toString();
	}

	public String getRunCode() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(
				"  injectMeta.processSingleRow(p.getRowMeta(),p.getRow() );   ")
				.append("\r\n");
		return buffer.toString();
	}
}

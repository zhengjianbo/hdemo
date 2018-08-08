package com.ram.kettle.steps.impl;

import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class Beetl extends StepMeta implements StepInterface {
//	private static Class<?> PKG = Beetl.class;
	private final String typeid = "beetl";

	private String sql;

	private String parameterField[];

	private String parameterType[];

	private int parameterIndex[];

	private String parameterOutField[];
	private int parameterOutIndex[];

	private GroupTemplate gt;

	public RowMetaInterface outputRowMeta;

	public Beetl() {
		super();
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setParameterField(String[] p) {
		this.parameterField = p;
	}

	public void setParameterType(String[] p) {
		this.parameterType = p;
	}

	public void setParameterOutField(String[] p) {
		this.parameterOutField = p;
	}

	public Beetl(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void getFields(RowMetaInterface rowMeta, String origin)
			throws KettleException {
		for (int i = 0; i < this.parameterOutField.length; i++) {
			if (parameterOutField[i] != null
					&& parameterOutField[i].length() != 0) {
				int type = ValueMetaInterface.TYPE_SERIALIZABLE;
				if (type == ValueMetaInterface.TYPE_NONE)
					type = ValueMetaInterface.TYPE_STRING;
				ValueMetaInterface v = new ValueMeta(parameterOutField[i], type);
				rowMeta.addValueMeta(v);
			}
		}

	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}

	public void allocate(int nrparam) {
		parameterField = new String[nrparam];
		parameterType = new String[nrparam];
		parameterIndex = new int[nrparam];
	}

	public void allocateOut(int nrparam) {
		parameterOutField = new String[nrparam];
		parameterOutIndex = new int[nrparam];
	}

	public void codeInit() {
		gt = Const.gtStatic;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			sql = XMLHandler.getTagValue(stepnode, "sql");

			Node param = XMLHandler.getSubNode(stepnode, "parameter");
			int nrparam = XMLHandler.countNodes(param, "field");

			allocate(nrparam);

			for (int i = 0; i < nrparam; i++) {
				Node pnode = XMLHandler.getSubNodeByNr(param, "field", i); //$NON-NLS-1$
				parameterField[i] = XMLHandler.getTagValue(pnode, "name"); //$NON-NLS-1$
				String ptype = XMLHandler.getTagValue(pnode, "type"); //$NON-NLS-1$
				parameterType[i] = ptype;
			}

			Node xparam = XMLHandler.getSubNode(stepnode, "parameterout");
			int xnrparam = XMLHandler.countNodes(xparam, "field");

			allocateOut(xnrparam);
			for (int i = 0; i < xnrparam; i++) {
				Node pnode = XMLHandler.getSubNodeByNr(xparam, "field", i); //$NON-NLS-1$
				parameterOutField[i] = XMLHandler.getTagValue(pnode, "name");
			}

			codeInit();

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
			throw new KettleException("Row Generator DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {

					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					// 判断类型
					for (int i = 0; i < this.parameterType.length; i++) {
						if(Const.isEmpty(this.parameterType[i])){
							continue;
						}
						int pIndex = rowMeta
								.indexOfValue(this.parameterType[i]);
						if (pIndex < 0) {
							throw new KettleException("No found InputField:"
									+ this.parameterType[i]);
						}
						this.parameterIndex[i] = pIndex;
					}
					first = false;
				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {

			Object[] extraData = beetl(rowMeta, rowData);

			rowData = RowDataUtil
					.addRowData(rowData, rowMeta.size(), extraData);

			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(rowData);
			pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;

		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException("Beetl ERROR"); //$NON-NLS-1$
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, rowData, 1, errorMessage, null,
						"IS_E001");
			}
		}

		return null;
	}

	private boolean beetlFirst = true;

	public Object[] beetl(RowMetaInterface rowMeta, Object[] rowData) {

		StringBuilder builder = new StringBuilder(this.sql + "");

		Template t = gt.getTemplate(builder.toString());

		int ix = 0;
		for (String param : this.parameterField) {
			t.binding(param, rowData[this.parameterIndex[ix]]);
			ix++;
		}

		t.binding("this", this);
		t.binding("rowMeta", rowMeta);
		t.binding("rowData", rowData);
		t.render();

		if (beetlFirst) {
			synchronized (this) {
				if (beetlFirst) {
//					Context c = t.getCtx();

					Map<String, Integer> globMeta = t.program.metaData.globalIndexMap;
					Map<String, Integer> meta = t.program.metaData
							.getTemplateRootScopeIndexMap();

					String[] dataArray = new String[globMeta.size()
							+ meta.size()];

					for (Map.Entry<String, Integer> entry : globMeta.entrySet()) {
						String key = entry.getKey();
						Integer value = entry.getValue();
						dataArray[value] = key;
					}

					for (Map.Entry<String, Integer> entry : meta.entrySet()) {
						String key = entry.getKey();
						Integer value = entry.getValue();
						dataArray[value] = key;
					}
					for (int l = 0; l < dataArray.length; l++) {
						for (int j = 0; j < this.parameterOutField.length; j++) {
							if (Const.isEmpty(this.parameterOutField[j])) {
								continue;
							}
							if (this.parameterOutField[j]
									.equalsIgnoreCase(dataArray[l])) {
								this.parameterOutIndex[j] = l;
								break;
							}
						}
					}
					beetlFirst = false;
				}
			}
		}
		Context c = t.getCtx();
		// 获取数据
		Object[] o = c.vars;
		Object[] outData = new Object[this.parameterOutIndex.length];
		for (int i = 0; i < parameterOutIndex.length; i++) {
			outData[i] = o[this.parameterOutIndex[i]];
		}
		return outData;
	}

	public String getTypeid() {
		return typeid;
	}

	public   StepInterface findStep(StepMeta steps, String stepname) {
		if (steps == null)
			return null;
		List<StepMeta> stepsx = steps.getParentTransMeta().getSteps();

		for (StepInterface stepMeta : stepsx) {
			if (stepMeta.getStepname().equalsIgnoreCase(stepname))
				return stepMeta;
		}
		return null;
	}

//	public static ProcessReturn process(StepInterface startCombi,
//			String[] rowMeta, String value[]) throws KettleException {
//		RowMetaInterface rowMetaInter = new RowMeta();
//
//		ProcessReturn pReturn = startCombi
//				.processSingleRow(rowMetaInter, value);
//		return pReturn;
//	}

	//public  ProcessReturn process(StepInterface startCombi,
//			RowMeta rowMeta, Object value[]) throws KettleException { 
//		ProcessReturn pReturn = startCombi.processSingleRow(rowMeta, value);
//		return pReturn;
//	}
 
}

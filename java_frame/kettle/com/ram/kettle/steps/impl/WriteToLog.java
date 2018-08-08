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
import com.ram.kettle.util.Const;
import com.ram.kettle.xml.XMLHandler;

public class WriteToLog extends StepMeta implements StepInterface {
	private static Class<?> PKG = WriteToLog.class;
	private final String typeid = "WriteToLog";

	public int fieldnrs[];
	public int fieldnr;

	public WriteToLog() {
		super();
	}

	public WriteToLog(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			loglevel = XMLHandler.getTagValue(stepnode, "loglevel");
			displayHeader = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
					stepnode, "displayHeader"));

			logmessage = XMLHandler.getTagValue(stepnode, "logmessage");

			Node fields = XMLHandler.getSubNode(stepnode, "fields");
			int nrfields = XMLHandler.countNodes(fields, "field");

			allocate(nrfields);

			for (int i = 0; i < nrfields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);
				fieldName[i] = XMLHandler.getTagValue(fnode, "name");
			}
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

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {

		if (r == null) {
			throw new KettleException("DATA ERROR");
		}

		if (first) {
			// 初始化时候同步下
			synchronized (this) {
				if (first) {

					if (getFieldName() != null && getFieldName().length > 0) {
						fieldnrs = new int[getFieldName().length];

						for (int i = 0; i < fieldnrs.length; i++) {
							fieldnrs[i] = rowMeta
									.indexOfValue(getFieldName()[i]);
							if (fieldnrs[i] < 0) {
								throw new KettleException(
										BaseMessages
												.getString(
														PKG,
														"WriteToLog.Log.CanNotFindField",
														getFieldName()[i]));
							}
						}
					} else {
						fieldnrs = new int[rowMeta.size()];
						for (int i = 0; i < fieldnrs.length; i++) {
							fieldnrs[i] = i;
						}
					}

					fieldnr = fieldnrs.length;

					if (!Const.isEmpty(logmessage)) {
						logmessage += Const.CR + Const.CR;
					}

					first = false;
				}
			}
		}

		try {

			StringBuffer out = new StringBuffer();
			out.append(Const.CR + "------------> "
					+ BaseMessages.getString(PKG, "WriteToLog.Log.NLigne", "")
					+ "------------------------------" + Const.CR);

			out.append(Const.CR + "------------> " + Const.CR + logmessage + "------------------------------" + Const.CR);

			for (int i = 0; i < fieldnr; i++) {
				String fieldvalue = rowMeta.getString(r, fieldnrs[i]);
				if (isDisplayHeader()) {
					String fieldname = rowMeta.getFieldNames()[fieldnrs[i]];
					out.append(fieldname + " = " + fieldvalue + Const.CR);
				} else {
					out.append(fieldvalue + Const.CR);
				}
			}
			out.append(Const.CR + "====================");
			ConstLog.message(out.toString());
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(r);
			pReturn.setRowMeta(RowMeta.clone(rowMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;
		} catch (KettleException e) { 
			if (getStepMeta().isDoingErrorHandling()) {
				return putErrorReturn(rowMeta, r, 1, e.getMessage(), null, "");
			} else {
				throw new KettleException(e.getMessage(), e);
			}
		}
	}

	public String[] getFieldName() {
		return fieldName;
	}

	public boolean isDisplayHeader() {
		return displayHeader;
	}

	public String getTypeid() {
		return typeid;
	}

	/** by which fields to display? */
	private String fieldName[];

	public static String logLevelCodes[] = { "log_level_nothing",
			"log_level_error", "log_level_minimal", "log_level_basic",
			"log_level_detailed", "log_level_debug", "log_level_rowlevel" };

	private boolean displayHeader;

	private String logmessage;

	private String loglevel;

	public Object clone() {
		WriteToLog retval = (WriteToLog) super.clone();

		int nrfields = fieldName.length;

		retval.allocate(nrfields);

		for (int i = 0; i < nrfields; i++) {
			retval.fieldName[i] = fieldName[i];
		}
		return retval;
	}

	public void allocate(int nrfields) {
		fieldName = new String[nrfields];
	}
}

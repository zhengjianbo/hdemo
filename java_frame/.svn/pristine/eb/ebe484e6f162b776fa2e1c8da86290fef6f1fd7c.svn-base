package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.xml.XMLHandler;

public class SysLog extends StepMeta implements StepInterface {

	private final String typeid = "SyslogMessage";

	/** dynamic message fieldname */
	private String messagefieldname;
	private String serverName;
	private String port;
	private String facility;
	private String priority;
	private String datePattern;
	private boolean addTimestamp;
	private boolean addHostName;

	public RowMetaInterface outputRowMeta;

	public SysLog() {
		super();
	}

	public SysLog(Node stepnode) throws KettleException {
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
			messagefieldname = XMLHandler.getTagValue(stepnode,
					"messagefieldname");
			port = XMLHandler.getTagValue(stepnode, "port");
			serverName = XMLHandler.getTagValue(stepnode, "servername");
			facility = XMLHandler.getTagValue(stepnode, "facility");
			priority = XMLHandler.getTagValue(stepnode, "priority");
			datePattern = XMLHandler.getTagValue(stepnode, "datePattern");
			addTimestamp = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
					stepnode, "addTimestamp"));
			addHostName = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode,
					"addHostName"));
		} catch (Exception e) {
			throw new KettleException("Unable to load step info from XML", e);
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) {
			String servername = environmentSubstitute(serverName);

			// Check target server
			if (Const.isEmpty(servername)) {
				// logError("SyslogMessage.MissingServerName");
			}

			// Check if message field is provided
			if (Const.isEmpty(messagefieldname)) {
				// logError(BaseMessages.getString(PKG,
				// "SyslogMessage.Error.MessageFieldMissing"));
				return false;
			}

			// int nrPort = Const.toInt(environmentSubstitute(port),
			// SyslogDefs.DEFAULT_PORT);

			if (addTimestamp) {
				// add timestamp to message
				datePattern = environmentSubstitute(datePattern);
				if (Const.isEmpty(datePattern)) {
					// logError(BaseMessages.getString(PKG,
					// "SyslogMessage.DatePatternEmpty"));
					return false;
				}
			}

			try {

			} catch (Exception ex) {

				return false;
			}
		}
		return true;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta,
			Object[] rowData) throws KettleException {
		if (rowData == null) {
			throw new KettleException("DATA ERROR");
		}
		try {
			// 写入日志
			int i = 1 / 2;
			
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(rowData);
			pReturn.setRowMeta(RowMeta.clone(rowMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;
		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				return putErrorReturn(rowMeta, rowData, 1, e.getMessage(),
						null, "");
			} else {
				throw new KettleException(e.getMessage(), e);
			}
		}
	}

	public String getTypeid() {
		return typeid;
	}

}

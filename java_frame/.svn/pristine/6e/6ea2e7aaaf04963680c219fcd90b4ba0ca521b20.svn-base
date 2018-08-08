package com.ram.kettle.step;

import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;
import com.ram.kettle.util.Const;
 
public class StepErrorMeta implements Cloneable {
	public static final String XML_TAG = "error";

	/** The source step that can send the error rows */
	private StepMeta sourceStep;

	/** The target step to send the error rows to */
	private StepMeta targetStep;

	/** Is the error handling enabled? */
	private boolean enabled;

	/**
	 * the name of the field value to contain the number of errors (null or
	 * empty means it's not needed)
	 */
	private String nrErrorsValuename;

	/**
	 * the name of the field value to contain the error description(s) (null or
	 * empty means it's not needed)
	 */
	private String errorDescriptionsValuename;

	/**
	 * the name of the field value to contain the fields for which the error(s)
	 * occured (null or empty means it's not needed)
	 */
	private String errorFieldsValuename;

	/**
	 * the name of the field value to contain the error code(s) (null or empty
	 * means it's not needed)
	 */
	private String errorCodesValuename;

	/**
	 * The maximum number of errors allowed before we stop processing with a
	 * hard error
	 */
	private String maxErrors = "";

	/**
	 * The maximum percent of errors allowed before we stop processing with a
	 * hard error
	 */
	private String maxPercentErrors = "";

	/**
	 * The minimum number of rows to read before the percentage evaluation takes
	 * place
	 */
	private String minPercentRows = "";

	public StepErrorMeta(Node node, List<StepMeta> steps) { 
		sourceStep = StepMeta.findStep(steps,
				XMLHandler.getTagValue(node, "source_step"));
		targetStep = StepMeta.findStep(steps,
				XMLHandler.getTagValue(node, "target_step"));
		enabled = "Y".equals(XMLHandler.getTagValue(node, "is_enabled"));
		nrErrorsValuename = XMLHandler.getTagValue(node, "nr_valuename");
		errorDescriptionsValuename = XMLHandler.getTagValue(node,
				"descriptions_valuename");
		errorFieldsValuename = XMLHandler.getTagValue(node, "fields_valuename");
		errorCodesValuename = XMLHandler.getTagValue(node, "codes_valuename");
		maxErrors = XMLHandler.getTagValue(node, "max_errors");
		maxPercentErrors = XMLHandler.getTagValue(node, "max_pct_errors");
		minPercentRows = XMLHandler.getTagValue(node, "min_pct_rows");
	}

	public RowMetaInterface getErrorRowMeta(long nrErrors,
			String errorDescriptions, String fieldNames, String errorCodes) {
		RowMetaInterface row = new RowMeta();

		String nrErr = (getNrErrorsValuename());
		if (!Const.isEmpty(nrErr)) {
			ValueMetaInterface v = new ValueMeta(nrErr,
					ValueMetaInterface.TYPE_INTEGER);
			v.setLength(3);
			row.addValueMeta(v);
		}
		String errDesc = (getErrorDescriptionsValuename());
		if (!Const.isEmpty(errDesc)) {
			ValueMetaInterface v = new ValueMeta(errDesc,
					ValueMetaInterface.TYPE_STRING);
			row.addValueMeta(v);
		}
		String errFields = (getErrorFieldsValuename());
		if (!Const.isEmpty(errFields)) {
			ValueMetaInterface v = new ValueMeta(errFields,
					ValueMetaInterface.TYPE_STRING);
			row.addValueMeta(v);
		}
		String errCodes = (getErrorCodesValuename());
		if (!Const.isEmpty(errCodes)) {
			ValueMetaInterface v = new ValueMeta(errCodes,
					ValueMetaInterface.TYPE_STRING);
			row.addValueMeta(v);
		}

		return row;
	}

	public void addErrorRowData(Object[] row, int startIndex, long nrErrors,
			String errorDescriptions, String fieldNames, String errorCodes) {
		int index = startIndex;

		String nrErr = (getNrErrorsValuename());
		if (!Const.isEmpty(nrErr)) {
			row[index] = new Long(nrErrors);
			index++;
		}
		String errDesc = (getErrorDescriptionsValuename());
		if (!Const.isEmpty(errDesc)) {
			row[index] = errorDescriptions;
			index++;
		}
		String errFields = (getErrorFieldsValuename());
		if (!Const.isEmpty(errFields)) {
			row[index] = fieldNames;
			index++;
		}
		String errCodes = (getErrorCodesValuename());
		if (!Const.isEmpty(errCodes)) {
			row[index] = errorCodes;
			index++;
		}
	}
	public StepErrorMeta clone() {
		try {
			return (StepErrorMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
 
	public String getErrorCodesValuename() {
		return errorCodesValuename;
	}
 
	public void setErrorCodesValuename(String errorCodesValuename) {
		this.errorCodesValuename = errorCodesValuename;
	}

	/**
	 * @return the error descriptions valuename
	 */
	public String getErrorDescriptionsValuename() {
		return errorDescriptionsValuename;
	}

	/**
	 * @param errorDescriptionsValuename
	 *            the error descriptions valuename to set
	 */
	public void setErrorDescriptionsValuename(String errorDescriptionsValuename) {
		this.errorDescriptionsValuename = errorDescriptionsValuename;
	}

	/**
	 * @return the error fields valuename
	 */
	public String getErrorFieldsValuename() {
		return errorFieldsValuename;
	}

	/**
	 * @param errorFieldsValuename
	 *            the error fields valuename to set
	 */
	public void setErrorFieldsValuename(String errorFieldsValuename) {
		this.errorFieldsValuename = errorFieldsValuename;
	}

	/**
	 * @return the nr errors valuename
	 */
	public String getNrErrorsValuename() {
		return nrErrorsValuename;
	}

	/**
	 * @param nrErrorsValuename
	 *            the nr errors valuename to set
	 */
	public void setNrErrorsValuename(String nrErrorsValuename) {
		this.nrErrorsValuename = nrErrorsValuename;
	}

	/**
	 * @return the target step
	 */
	public StepMeta getTargetStep() {
		return targetStep;
	}

	/**
	 * @param targetStep
	 *            the target step to set
	 */
	public void setTargetStep(StepMeta targetStep) {
		this.targetStep = targetStep;
	}

	/**
	 * @return The source step can send the error rows
	 */
	public StepMeta getSourceStep() {
		return sourceStep;
	}

	/**
	 * @param sourceStep
	 *            The source step can send the error rows
	 */
	public void setSourceStep(StepMeta sourceStep) {
		this.sourceStep = sourceStep;
	}

	/**
	 * @return the enabled flag: Is the error handling enabled?
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled flag to set: Is the error handling enabled?
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public RowMetaInterface getErrorFields() {
		return getErrorRowMeta(0L, null, null, null);
	}


	/**
	 * @return the maxErrors
	 */
	public String getMaxErrors() {
		return maxErrors;
	}

	/**
	 * @param maxErrors
	 *            the maxErrors to set
	 */
	public void setMaxErrors(String maxErrors) {
		this.maxErrors = maxErrors;
	}

	/**
	 * @return the maxPercentErrors
	 */
	public String getMaxPercentErrors() {
		return maxPercentErrors;
	}

	/**
	 * @param maxPercentErrors
	 *            the maxPercentErrors to set
	 */
	public void setMaxPercentErrors(String maxPercentErrors) {
		this.maxPercentErrors = maxPercentErrors;
	}

	/**
	 * @return the minRowsForPercent
	 */
	public String getMinPercentRows() {
		return minPercentRows;
	}

	/**
	 * @param minRowsForPercent
	 *            the minRowsForPercent to set
	 */
	public void setMinPercentRows(String minRowsForPercent) {
		this.minPercentRows = minRowsForPercent;
	}

	public String getXML() throws KettleException {
		return null;
	}
}

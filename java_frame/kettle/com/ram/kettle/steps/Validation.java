package com.ram.kettle.steps;

import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.log.KettleException;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.xml.XMLHandler;

public class Validation implements Cloneable {
	public static final String XML_TAG = "validator_field";
	public static final String XML_TAG_ALLOWED = "allowed_value";

	private String name;
	private String fieldName;

	private String maximumLength;
	private String minimumLength;

	private boolean nullAllowed;
	private boolean onlyNullAllowed;
	private boolean onlyNumericAllowed;

	private int dataType;
	private boolean dataTypeVerified;
	private String conversionMask;
	private String decimalSymbol;
	private String groupingSymbol;

	private String minimumValue;
	private String maximumValue;
	private String[] allowedValues;
	private boolean sourcingValues;
	private String sourcingStepName;
	private StepMeta sourcingStep;
	private String sourcingField;

	private String startString;
	private String startStringNotAllowed;
	private String endString;
	private String endStringNotAllowed;

	private String regularExpression;
	private String regularExpressionNotAllowed;

	private String errorCode;
	private String errorDescription;

	public Validation() {
		maximumLength = "";
		minimumLength = "";
		nullAllowed = true;
		onlyNullAllowed = false;
		onlyNumericAllowed = false;
	}

	public Validation(String name) {
		this();
		this.fieldName = name;
	}

	@Override
	public Validation clone() {
		try {
			return (Validation) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean equals(Validation validation) {
		return validation.getName().equalsIgnoreCase(name);
	}
 
	public Validation(Node calcnode) throws KettleException {
		this();

		fieldName = XMLHandler.getTagValue(calcnode, "name");
		name = XMLHandler.getTagValue(calcnode, "validation_name");
		if (Const.isEmpty(name))
			name = fieldName; // remain backward compatible

		maximumLength = XMLHandler.getTagValue(calcnode, "max_length");
		minimumLength = XMLHandler.getTagValue(calcnode, "min_length");

		nullAllowed = "Y".equalsIgnoreCase(XMLHandler.getTagValue(calcnode,
				"null_allowed"));
		onlyNullAllowed = "Y".equalsIgnoreCase(XMLHandler.getTagValue(calcnode,
				"only_null_allowed"));
		onlyNumericAllowed = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
				calcnode, "only_numeric_allowed"));

		dataType = ValueMeta.getType(XMLHandler.getTagValue(calcnode,
				"data_type"));
		dataTypeVerified = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
				calcnode, "data_type_verified"));
		conversionMask = XMLHandler.getTagValue(calcnode, "conversion_mask");
		decimalSymbol = XMLHandler.getTagValue(calcnode, "decimal_symbol");
		groupingSymbol = XMLHandler.getTagValue(calcnode, "grouping_symbol");

		minimumValue = XMLHandler.getTagValue(calcnode, "min_value");
		maximumValue = XMLHandler.getTagValue(calcnode, "max_value");

		startString = XMLHandler.getTagValue(calcnode, "start_string");
		endString = XMLHandler.getTagValue(calcnode, "end_string");
		startStringNotAllowed = XMLHandler.getTagValue(calcnode,
				"start_string_not_allowed");
		endStringNotAllowed = XMLHandler.getTagValue(calcnode,
				"end_string_not_allowed");

		regularExpression = XMLHandler.getTagValue(calcnode,
				"regular_expression");
		regularExpressionNotAllowed = XMLHandler.getTagValue(calcnode,
				"regular_expression_not_allowed");

		errorCode = XMLHandler.getTagValue(calcnode, "error_code");
		errorDescription = XMLHandler
				.getTagValue(calcnode, "error_description");

		sourcingValues = "Y".equalsIgnoreCase(XMLHandler.getTagValue(calcnode,
				"is_sourcing_values"));
		sourcingStepName = XMLHandler.getTagValue(calcnode, "sourcing_step");
		sourcingField = XMLHandler.getTagValue(calcnode, "sourcing_field");

		Node allowedValuesNode = XMLHandler.getSubNode(calcnode,
				XML_TAG_ALLOWED);
		int nrValues = XMLHandler.countNodes(allowedValuesNode, "value");
		allowedValues = new String[nrValues];
		for (int i = 0; i < nrValues; i++) {
			Node allowedNode = XMLHandler.getSubNodeByNr(allowedValuesNode,
					"value", i);
			allowedValues[i] = XMLHandler.getNodeValue(allowedNode);
		}
	}

	/**
	 * @return the field name to validate
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the field name to validate
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the maximumLength
	 */
	public String getMaximumLength() {
		return maximumLength;
	}

	/**
	 * @param maximumLength
	 *            the maximumLength to set
	 */
	public void setMaximumLength(String maximumLength) {
		this.maximumLength = maximumLength;
	}

	/**
	 * @return the minimumLength
	 */
	public String getMinimumLength() {
		return minimumLength;
	}

	/**
	 * @param minimumLength
	 *            the minimumLength to set
	 */
	public void setMinimumLength(String minimumLength) {
		this.minimumLength = minimumLength;
	}

	/**
	 * @return the nullAllowed
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/**
	 * @param nullAllowed
	 *            the nullAllowed to set
	 */
	public void setNullAllowed(boolean nullAllowed) {
		this.nullAllowed = nullAllowed;
	}

	/**
	 * @return the dataType
	 */
	public int getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the conversionMask
	 */
	public String getConversionMask() {
		return conversionMask;
	}

	/**
	 * @param conversionMask
	 *            the conversionMask to set
	 */
	public void setConversionMask(String conversionMask) {
		this.conversionMask = conversionMask;
	}

	/**
	 * @return the decimalSymbol
	 */
	public String getDecimalSymbol() {
		return decimalSymbol;
	}

	/**
	 * @param decimalSymbol
	 *            the decimalSymbol to set
	 */
	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	/**
	 * @return the groupingSymbol
	 */
	public String getGroupingSymbol() {
		return groupingSymbol;
	}

	/**
	 * @param groupingSymbol
	 *            the groupingSymbol to set
	 */
	public void setGroupingSymbol(String groupingSymbol) {
		this.groupingSymbol = groupingSymbol;
	}

	/**
	 * @return the minimumValue
	 */
	public String getMinimumValue() {
		return minimumValue;
	}

	/**
	 * @param minimumValue
	 *            the minimumValue to set
	 */
	public void setMinimumValue(String minimumValue) {
		this.minimumValue = minimumValue;
	}

	/**
	 * @return the maximumValue
	 */
	public String getMaximumValue() {
		return maximumValue;
	}

	/**
	 * @param maximumValue
	 *            the maximumValue to set
	 */
	public void setMaximumValue(String maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * @return the allowedValues
	 */
	public String[] getAllowedValues() {
		return allowedValues;
	}

	/**
	 * @param allowedValues
	 *            the allowedValues to set
	 */
	public void setAllowedValues(String[] allowedValues) {
		this.allowedValues = allowedValues;
	}

	/**
	 * @return the dataTypeVerified
	 */
	public boolean isDataTypeVerified() {
		return dataTypeVerified;
	}

	/**
	 * @param dataTypeVerified
	 *            the dataTypeVerified to set
	 */
	public void setDataTypeVerified(boolean dataTypeVerified) {
		this.dataTypeVerified = dataTypeVerified;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * @param errorDescription
	 *            the errorDescription to set
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * @return true if only numeric values are allowed: A numeric data type, a
	 *         date or a String containing digits only
	 */
	public boolean isOnlyNumericAllowed() {
		return onlyNumericAllowed;
	}

	/**
	 * @return the startString
	 */
	public String getStartString() {
		return startString;
	}

	/**
	 * @param startString
	 *            the startString to set
	 */
	public void setStartString(String startString) {
		this.startString = startString;
	}

	/**
	 * @return the startStringNotAllowed
	 */
	public String getStartStringNotAllowed() {
		return startStringNotAllowed;
	}

	/**
	 * @param startStringNotAllowed
	 *            the startStringNotAllowed to set
	 */
	public void setStartStringNotAllowed(String startStringNotAllowed) {
		this.startStringNotAllowed = startStringNotAllowed;
	}

	/**
	 * @return the endString
	 */
	public String getEndString() {
		return endString;
	}

	/**
	 * @param endString
	 *            the endString to set
	 */
	public void setEndString(String endString) {
		this.endString = endString;
	}

	/**
	 * @return the endStringNotAllowed
	 */
	public String getEndStringNotAllowed() {
		return endStringNotAllowed;
	}

	/**
	 * @param endStringNotAllowed
	 *            the endStringNotAllowed to set
	 */
	public void setEndStringNotAllowed(String endStringNotAllowed) {
		this.endStringNotAllowed = endStringNotAllowed;
	}

	/**
	 * @param onlyNumericAllowed
	 *            the onlyNumericAllowed to set
	 */
	public void setOnlyNumericAllowed(boolean onlyNumericAllowed) {
		this.onlyNumericAllowed = onlyNumericAllowed;
	}

	/**
	 * @return the regularExpression
	 */
	public String getRegularExpression() {
		return regularExpression;
	}

	/**
	 * @param regularExpression
	 *            the regularExpression to set
	 */
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}

	/**
	 * @return the name of this validation
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the new name for this validation
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the regularExpressionNotAllowed
	 */
	public String getRegularExpressionNotAllowed() {
		return regularExpressionNotAllowed;
	}

	/**
	 * @param regularExpressionNotAllowed
	 *            the regularExpressionNotAllowed to set
	 */
	public void setRegularExpressionNotAllowed(
			String regularExpressionNotAllowed) {
		this.regularExpressionNotAllowed = regularExpressionNotAllowed;
	}

	/**
	 * Find a validation by name in a list of validations
	 * 
	 * @param validations
	 *            The list to search
	 * @param name
	 *            the name to search for
	 * @return the validation if one matches or null if none is found.
	 */
	public static Validation findValidation(List<Validation> validations,
			String name) {
		for (Validation validation : validations) {
			if (validation.getName().equalsIgnoreCase(name))
				return validation;
		}
		return null;
	}

	/**
	 * @return the onlyNullAllowed
	 */
	public boolean isOnlyNullAllowed() {
		return onlyNullAllowed;
	}

	/**
	 * @param onlyNullAllowed
	 *            the onlyNullAllowed to set
	 */
	public void setOnlyNullAllowed(boolean onlyNullAllowed) {
		this.onlyNullAllowed = onlyNullAllowed;
	}

	/**
	 * @return the sourcingValues
	 */
	public boolean isSourcingValues() {
		return sourcingValues;
	}

	/**
	 * @param sourcingValues
	 *            the sourcingValues to set
	 */
	public void setSourcingValues(boolean sourcingValues) {
		this.sourcingValues = sourcingValues;
	}

	/**
	 * @return the sourcingField
	 */
	public String getSourcingField() {
		return sourcingField;
	}

	/**
	 * @param sourcingField
	 *            the sourcingField to set
	 */
	public void setSourcingField(String sourcingField) {
		this.sourcingField = sourcingField;
	}

	/**
	 * @return the sourcingStepName
	 */
	public String getSourcingStepName() {
		return sourcingStepName;
	}

	/**
	 * @param sourcingStepName
	 *            the sourcingStepName to set
	 */
	public void setSourcingStepName(String sourcingStepName) {
		this.sourcingStepName = sourcingStepName;
	}

	/**
	 * @return the sourcingStep
	 */
	public StepMeta getSourcingStep() {
		return sourcingStep;
	}

	/**
	 * @param sourcingStep
	 *            the sourcingStep to set
	 */
	public void setSourcingStep(StepMeta sourcingStep) {
		this.sourcingStep = sourcingStep;
	}
}

package com.ram.kettle.steps.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.steps.KettleValidatorException;
import com.ram.kettle.steps.Validation;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class Validator extends StepMeta implements StepInterface {
	private static Class<?> PKG = Validator.class;
	private final String typeid = "Validator";
	/** The calculations to be performed */
	private List<Validation> validations;

	/** Checkbox to have all rules validated, with all the errors in the output */
	private boolean validatingAll;

	/**
	 * If enabled, it concatenates all encountered errors with the selected
	 * separator
	 */
	private boolean concatenatingErrors;

	/**
	 * The concatenation separator
	 */
	private String concatenationSeparator;

	public int[] fieldIndexes;

	public ValueMetaInterface[] constantsMeta;
	public String[] minimumValueAsString;
	public String[] maximumValueAsString;
	public int[] fieldsMinimumLengthAsInt;
	public int[] fieldsMaximumLengthAsInt;
	public Object[] listValues[];

	public Pattern[] patternExpected;

	public Pattern[] patternDisallowed;

	public String[] errorCode;
	public String[] errorDescription;
	public String[] conversionMask;
	public String[] decimalSymbol;
	public String[] groupingSymbol;
	public String[] maximumLength;
	public String[] minimumLength;
	public Object[] maximumValue;
	public Object[] minimumValue;
	public String[] startString;
	public String[] endString;
	public String[] startStringNotAllowed;
	public String[] endStringNotAllowed;
	public String[] regularExpression;
	public String[] regularExpressionNotAllowed;

	public Validator() {
		super();
	}

	public Validator(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrValidations) {
		validations = new ArrayList<Validation>(nrValidations);
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		int nrCalcs = XMLHandler.countNodes(stepnode, Validation.XML_TAG);
		allocate(nrCalcs);
		validatingAll = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode,
				"validate_all"));
		concatenatingErrors = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
				stepnode, "concat_errors"));
		concatenationSeparator = XMLHandler.getTagValue(stepnode,
				"concat_separator");

		for (int i = 0; i < nrCalcs; i++) {
			Node calcnode = XMLHandler.getSubNodeByNr(stepnode,
					Validation.XML_TAG, i);
			validations.add(new Validation(calcnode));
		}

		// init 生成

		constantsMeta = new ValueMetaInterface[getValidations().size()];
		minimumValueAsString = new String[getValidations().size()];
		maximumValueAsString = new String[getValidations().size()];
		fieldsMinimumLengthAsInt = new int[getValidations().size()];
		fieldsMaximumLengthAsInt = new int[getValidations().size()];
		minimumValue = new Object[getValidations().size()];
		maximumValue = new Object[getValidations().size()];
		listValues = new Object[getValidations().size()][];
		errorCode = new String[getValidations().size()];
		errorDescription = new String[getValidations().size()];
		conversionMask = new String[getValidations().size()];
		decimalSymbol = new String[getValidations().size()];
		groupingSymbol = new String[getValidations().size()];
		maximumLength = new String[getValidations().size()];
		minimumLength = new String[getValidations().size()];
		startString = new String[getValidations().size()];
		endString = new String[getValidations().size()];
		startStringNotAllowed = new String[getValidations().size()];
		endStringNotAllowed = new String[getValidations().size()];
		regularExpression = new String[getValidations().size()];
		regularExpressionNotAllowed = new String[getValidations().size()];
		patternExpected = new Pattern[getValidations().size()];
		patternDisallowed = new Pattern[getValidations().size()];

		for (int i = 0; i < getValidations().size(); i++) {

			Validation field = getValidations().get(i);
			constantsMeta[i] = new ValueMeta(field.getFieldName(),
					field.getDataType());
			constantsMeta[i].setConversionMask(field.getConversionMask());
			constantsMeta[i].setDecimalSymbol(field.getDecimalSymbol());
			constantsMeta[i].setGroupingSymbol(field.getGroupingSymbol());
			errorCode[i] = (Const.NVL(field.getErrorCode(), ""));
			errorDescription[i] = (Const.NVL(field.getErrorDescription(), ""));
			conversionMask[i] = (Const.NVL(field.getConversionMask(), ""));
			decimalSymbol[i] = (Const.NVL(field.getDecimalSymbol(), ""));
			groupingSymbol[i] = (Const.NVL(field.getGroupingSymbol(), ""));
			maximumLength[i] = (Const.NVL(field.getMaximumLength(), ""));
			minimumLength[i] = (Const.NVL(field.getMinimumLength(), ""));
			maximumValueAsString[i] = (Const.NVL(field.getMaximumValue(), ""));
			minimumValueAsString[i] = (Const.NVL(field.getMinimumValue(), ""));
			startString[i] = (Const.NVL(field.getStartString(), ""));
			endString[i] = (Const.NVL(field.getEndString(), ""));
			startStringNotAllowed[i] = (Const.NVL(
					field.getStartStringNotAllowed(), ""));
			endStringNotAllowed[i] = (Const.NVL(field.getEndStringNotAllowed(),
					""));
			regularExpression[i] = (Const.NVL(field.getRegularExpression(), ""));
			regularExpressionNotAllowed[i] = (Const.NVL(
					field.getRegularExpressionNotAllowed(), ""));

			ValueMetaInterface stringMeta = constantsMeta[i].clone();
			stringMeta.setType(ValueMetaInterface.TYPE_STRING);

			try {
				minimumValue[i] = Const.isEmpty(minimumValueAsString[i]) ? null
						: constantsMeta[i].convertData(stringMeta,
								minimumValueAsString[i]);
				maximumValue[i] = Const.isEmpty(maximumValueAsString[i]) ? null
						: constantsMeta[i].convertData(stringMeta,
								maximumValueAsString[i]);

				try {
					fieldsMinimumLengthAsInt[i] = Integer.valueOf(Const.NVL(
							minimumLength[i], "-1"));
				} catch (NumberFormatException nfe) {
					throw new KettleException(
							"Caught a number format exception converting minimum length with value "
									+ minimumLength[i] + " to an int.", nfe);
				}

				try {
					fieldsMaximumLengthAsInt[i] = Integer.valueOf(Const.NVL(
							maximumLength[i], "-1"));
				} catch (NumberFormatException nfe) {
					throw new KettleException(
							"Caught a number format exception converting minimum length with value "
									+ maximumLength[i] + " to an int.", nfe);
				}

				int listSize = field.getAllowedValues() != null ? field
						.getAllowedValues().length : 0;
				listValues[i] = new Object[listSize];
				for (int s = 0; s < listSize; s++) {
					listValues[i][s] = Const
							.isEmpty(field.getAllowedValues()[s]) ? null
							: constantsMeta[i].convertData(stringMeta,
									(field.getAllowedValues()[s]));
				}
			} catch (KettleException e) {
				e.printStackTrace();
				if (field.getDataType() == ValueMetaInterface.TYPE_NONE) {
					ConstLog.message(BaseMessages.getString(PKG,
							"Validator.Exception.SpecifyDataType"));
				} else {
					System.out
							.println(BaseMessages
									.getString(PKG,
											"Validator.Exception.DataConversionErrorEncountered"));
				}
				throw new KettleException(e);
			}

			if (!Const.isEmpty(regularExpression[i])) {
				patternExpected[i] = Pattern.compile(field
						.getRegularExpression());
			}
			if (!Const.isEmpty(regularExpressionNotAllowed[i])) {
				patternDisallowed[i] = Pattern.compile(field
						.getRegularExpressionNotAllowed());
			}

		}

	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) { 
//			ConstLog.message("==" + this.getTypeId() + "=初始化"
//					+ this.getName());

			return true;
		}
		return false;
	}

	public Object clone() {
		Validator retval = (Validator) super.clone();
		if (validations != null) {
			retval.allocate(validations.size());
			for (int i = 0; i < validations.size(); i++) {
				retval.validations.add(validations.get(i).clone());
			}
		} else {
			retval.allocate(0);
		}
		return retval;
	}

	public void setDefault() {
		validations = new ArrayList<Validation>();
		concatenationSeparator = "|";
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

					fieldIndexes = new int[getValidations().size()];
					for (int i = 0; i < getValidations().size(); i++) {
						Validation field = getValidations().get(i);

						if (!Const.isEmpty(field.getFieldName())) {
							fieldIndexes[i] = rowMeta.indexOfValue(field
									.getFieldName());
							if (fieldIndexes[i] < 0) {
								throw new KettleException(
										"Unable to find the specified fieldname '"
												+ field.getFieldName()
												+ "' for validation#" + (i + 1));
							}
						} else {
							throw new KettleException(
									"There is no name specified for validator field #"
											+ (i + 1));
						}
					}

					first = false;
				}
			}
		}

		try {
			validateFields(rowMeta, r);
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(r);
			pReturn.setRowMeta(RowMeta.clone(rowMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;

		} catch (KettleException e) {
			if (getStepMeta().isDoingErrorHandling()) {
				return putErrorReturn(RowMeta.clone(rowMeta), r, 1,
						e.getMessage(), e.getFieldName(), null);
			} else {
				throw new KettleException(e.getMessage(), e);
			}
		}
	}

	public boolean isConcatenatingErrors() {
		return concatenatingErrors;
	}

	public String getConcatenationSeparator() {
		return concatenationSeparator;
	}

	private List<KettleException> validateFields(RowMetaInterface inputRowMeta,
			Object[] r) throws KettleException {
		List<KettleException> exceptions = new ArrayList<KettleException>();

		for (int i = 0; i < getValidations().size(); i++) {
			Validation field = getValidations().get(i);

			int valueIndex = fieldIndexes[i];
			ValueMetaInterface validatorMeta = constantsMeta[i];

			ValueMetaInterface valueMeta = inputRowMeta
					.getValueMeta(valueIndex);
			Object valueData = r[valueIndex];

			boolean isNull = valueMeta.isNull(valueData);
			if (!field.isNullAllowed() && isNull) {
				KettleValidatorException exception = new KettleValidatorException(
						field,
						BaseMessages.getString(PKG,
								"Validator.Exception.NullNotAllowed",
								field.getFieldName(), inputRowMeta.getString(r)),
						field.getFieldName());
				if (isValidatingAll())
					exceptions.add(exception);
				else
					throw exception;
			}

			if (field.isOnlyNullAllowed() && !isNull) {
				KettleValidatorException exception = new KettleValidatorException(
						field,
						BaseMessages.getString(PKG,
								"Validator.Exception.OnlyNullAllowed",
								field.getFieldName(), inputRowMeta.getString(r)),
						field.getFieldName());
				if (isValidatingAll())
					exceptions.add(exception);
				else
					throw exception;
			}

			// Check the data type!
			//
			if (field.isDataTypeVerified()
					&& field.getDataType() != ValueMetaInterface.TYPE_NONE) {

				// Same data type?
				//
				if (field.getDataType() != valueMeta.getType()) {
					KettleValidatorException exception = new KettleValidatorException(
							field, BaseMessages.getString(PKG,
									"Validator.Exception.UnexpectedDataType",
									field.getFieldName(),
									valueMeta.toStringMeta(),
									validatorMeta.toStringMeta()),
							field.getFieldName());
					if (isValidatingAll())
						exceptions.add(exception);
					else
						throw exception;
				}
			}

			// Check various things if the value is not null..
			//
			if (!isNull) {

				if (fieldsMinimumLengthAsInt[i] >= 0
						|| fieldsMaximumLengthAsInt[i] >= 0
						|| minimumValue[i] != null || maximumValue[i] != null
						|| listValues[i].length > 0 || field.isSourcingValues()
						|| !Const.isEmpty(startString[i])
						|| !Const.isEmpty(endString[i])
						|| !Const.isEmpty(startStringNotAllowed[i])
						|| !Const.isEmpty(endStringNotAllowed[i])
						|| field.isOnlyNumericAllowed()
						|| patternExpected[i] != null
						|| patternDisallowed[i] != null) {

					String stringValue = valueMeta.getString(valueData);
					int stringLength = stringValue.length();

					if (fieldsMinimumLengthAsInt[i] >= 0
							&& stringLength < fieldsMinimumLengthAsInt[i]) {
						KettleValidatorException exception = new KettleValidatorException(
								field,
								BaseMessages
										.getString(
												PKG,
												"Validator.Exception.ShorterThanMininumLength",
												field.getFieldName(), valueMeta
														.getString(valueData),
												Integer.toString(stringValue
														.length()), field
														.getMinimumLength()));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// Maximum length
					//
					// if (field.getMaximumLength()>=0 &&
					// stringValue.length()>field.getMaximumLength() ) {
					if (fieldsMaximumLengthAsInt[i] >= 0
							&& stringLength > fieldsMaximumLengthAsInt[i]) {
						KettleValidatorException exception = new KettleValidatorException(
								field,
								BaseMessages
										.getString(
												PKG,
												"Validator.Exception.LongerThanMaximumLength",
												field.getFieldName(), valueMeta
														.getString(valueData),
												Integer.toString(stringValue
														.length()), field
														.getMaximumLength()));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// Minimal value
					//
					if (minimumValue[i] != null
							&& valueMeta.compare(valueData, validatorMeta,
									minimumValue[i]) < 0) {
						KettleValidatorException exception = new KettleValidatorException(
								field,
								BaseMessages
										.getString(
												PKG,
												"Validator.Exception.LowerThanMinimumValue",
												field.getFieldName(),
												valueMeta.getString(valueData),
												constantsMeta[i]
														.getString(minimumValue[i])));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// Maximum value
					//
					if (maximumValue[i] != null
							&& valueMeta.compare(valueData, validatorMeta,
									maximumValue[i]) > 0) {
						KettleValidatorException exception = new KettleValidatorException(
								field,
								BaseMessages
										.getString(
												PKG,
												"Validator.Exception.HigherThanMaximumValue",
												field.getFieldName(),
												valueMeta.getString(valueData),
												constantsMeta[i]
														.getString(maximumValue[i])));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// In list?
					//
					if (field.isSourcingValues() || listValues[i].length > 0) {
						boolean found = false;
						for (Object object : listValues[i]) {
							if (object != null
									&& listValues[i] != null
									&& valueMeta.compare(valueData,
											validatorMeta, object) == 0) {
								found = true;
							}
						}
						if (!found) {
							KettleValidatorException exception = new KettleValidatorException(
									field, BaseMessages.getString(PKG,
											"Validator.Exception.NotInList",
											field.getFieldName(),
											valueMeta.getString(valueData)));
							if (isValidatingAll())
								exceptions.add(exception);
							else
								throw exception;
						}
					}

					// Numeric data or strings with only
					if (field.isOnlyNumericAllowed()) {
						if (valueMeta.isNumeric()
								|| !containsOnlyDigits(valueMeta
										.getString(valueData))) {
							KettleValidatorException exception = new KettleValidatorException(
									field,
									BaseMessages
											.getString(
													PKG,
													"Validator.Exception.NonNumericDataNotAllowed",
													field.getFieldName(),
													valueMeta.toStringMeta()));
							if (isValidatingAll())
								exceptions.add(exception);
							else
								throw exception;
						}
					}

					// Does not start with string value
					//
					if (!Const.isEmpty(startString[i])
							&& !stringValue.startsWith(startString[i])) {
						KettleValidatorException exception = new KettleValidatorException(
								field,
								BaseMessages
										.getString(
												PKG,
												"Validator.Exception.DoesNotStartWithString",
												field.getFieldName(),
												valueMeta.getString(valueData),
												field.getStartString()));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					if (!Const.isEmpty(endString[i])
							&& !stringValue.endsWith(endString[i])) {
						KettleValidatorException exception = new KettleValidatorException(
								field,
								BaseMessages
										.getString(
												PKG,
												"Validator.Exception.DoesNotStartWithString",
												field.getFieldName(),
												valueMeta.getString(valueData),
												field.getEndString()));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// Starts with string value
					//
					if (!Const.isEmpty(startStringNotAllowed[i])
							&& stringValue.startsWith(startStringNotAllowed[i])) {
						KettleValidatorException exception = new KettleValidatorException(
								field, BaseMessages.getString(PKG,
										"Validator.Exception.StartsWithString",
										field.getFieldName(),
										valueMeta.getString(valueData),
										field.getStartStringNotAllowed()));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// Ends with string value
					//
					if (!Const.isEmpty(endStringNotAllowed[i])
							&& stringValue.endsWith(endStringNotAllowed[i])) {
						KettleValidatorException exception = new KettleValidatorException(
								field, BaseMessages.getString(PKG,
										"Validator.Exception.EndsWithString",
										field.getFieldName(),
										valueMeta.getString(valueData),
										field.getEndStringNotAllowed()));
						if (isValidatingAll())
							exceptions.add(exception);
						else
							throw exception;
					}

					// Matching regular expression allowed?
					//
					if (patternExpected[i] != null) {
						Matcher matcher = patternExpected[i]
								.matcher(stringValue);
						if (!matcher.matches()) {
							KettleValidatorException exception = new KettleValidatorException(
									field,
									BaseMessages
											.getString(
													PKG,
													"Validator.Exception.MatchingRegExpExpected",
													field.getFieldName(),
													valueMeta
															.getString(valueData),
													field.getRegularExpression()));
							if (isValidatingAll())
								exceptions.add(exception);
							else
								throw exception;
						}
					}

					// Matching regular expression NOT allowed?
					//
					if (patternDisallowed[i] != null) {
						Matcher matcher = patternDisallowed[i]
								.matcher(stringValue);
						if (matcher.matches()) {
							KettleValidatorException exception = new KettleValidatorException(
									field,
									BaseMessages
											.getString(
													PKG,
													"Validator.Exception.MatchingRegExpNotAllowed",
													field.getFieldName(),
													valueMeta
															.getString(valueData),
													field.getRegularExpressionNotAllowed()));
							if (isValidatingAll())
								exceptions.add(exception);
							else
								throw exception;
						}
					}

				}
			}
		}

		return exceptions;
	}

	private boolean containsOnlyDigits(String string) {
		for (char c : string.toCharArray()) {
			if (c < '0' || c > '9')
				return false;
		}
		return true;
	}

	public boolean isValidatingAll() {
		return validatingAll;
	}

	public List<Validation> getValidations() {
		return validations;
	}
}

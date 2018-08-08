package com.ram.kettle.steps.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.steps.CalculatorMetaFunction;
import com.ram.kettle.steps.FieldIndexes;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueDataUtil;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class Calculator extends StepMeta implements StepInterface {
	private static Class<?> PKG = Calculator.class;
	private final String typeid = "Calculator";

	public RowMetaInterface outputRowMeta;
	public RowMetaInterface calcRowMeta;

	private CalculatorMetaFunction[] calculation;
	public FieldIndexes[] fieldIndexes;

	public int[] tempIndexes;

	public Calculator() {
		super();
	}

	public Calculator(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrfields) {
		calculation = new CalculatorMetaFunction[nrfields];
	}

	public Object clone() {
		Calculator retval = (Calculator) super.clone();
		if (calculation != null) {
			retval.allocate(calculation.length);
			for (int i = 0; i < calculation.length; i++) {
				retval.getCalculation()[i] = (CalculatorMetaFunction) calculation[i]
						.clone();
			}
		} else {
			retval.allocate(0);
		}
		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			int nrCalcs = XMLHandler.countNodes(stepnode,
					CalculatorMetaFunction.XML_TAG);
			allocate(nrCalcs);
			for (int i = 0; i < nrCalcs; i++) {
				Node calcnode = XMLHandler.getSubNodeByNr(stepnode,
						CalculatorMetaFunction.XML_TAG, i);
				calculation[i] = new CalculatorMetaFunction(calcnode);
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
			return true;
		}
		return false;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {
		if (r == null) {
			throw new KettleException("DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					calcRowMeta = getAllFields(rowMeta);

					fieldIndexes = new FieldIndexes[getCalculation().length];
					List<Integer> tempIndexList = new ArrayList<Integer>();

					for (int i = 0; i < getCalculation().length; i++) {
						CalculatorMetaFunction function = getCalculation()[i];
						fieldIndexes[i] = new FieldIndexes();

						if (!Const.isEmpty(function.getFieldName())) {
							fieldIndexes[i].indexName = calcRowMeta
									.indexOfValue(function.getFieldName());
							if (fieldIndexes[i].indexName < 0) {
								throw new KettleException(
										BaseMessages
												.getString(
														PKG,
														"Calculator.Error.UnableFindField",
														function.getFieldName(),
														"" + (i + 1)));
							}
						} else {
							throw new KettleException(BaseMessages.getString(
									PKG, "Calculator.Error.NoNameField", ""
											+ (i + 1)));
						}

						if (!Const.isEmpty(function.getFieldA())) {
							if (function.getCalcType() != CalculatorMetaFunction.CALC_CONSTANT) {
								fieldIndexes[i].indexA = calcRowMeta
										.indexOfValue(function.getFieldA());
								if (fieldIndexes[i].indexA < 0) {
									// Nope: throw an exception
									throw new KettleException(
											"Unable to find the first argument field '"
													+ function.getFieldName()
													+ " for calculation #"
													+ (i + 1));
								}
							} else {
								fieldIndexes[i].indexA = -1;
							}
						} else {
							throw new KettleException(
									"There is no first argument specified for calculated field #"
											+ (i + 1));
						}

						if (!Const.isEmpty(function.getFieldB())) {
							fieldIndexes[i].indexB = calcRowMeta
									.indexOfValue(function.getFieldB());
							if (fieldIndexes[i].indexB < 0) {
								// Nope: throw an exception
								throw new KettleException(
										"Unable to find the second argument field '"
												+ function.getFieldName()
												+ " for calculation #"
												+ (i + 1));
							}
						}
						fieldIndexes[i].indexC = -1;
						if (!Const.isEmpty(function.getFieldC())) {
							fieldIndexes[i].indexC = calcRowMeta
									.indexOfValue(function.getFieldC());
							if (fieldIndexes[i].indexC < 0) {
								// Nope: throw an exception
								throw new KettleException(
										"Unable to find the third argument field '"
												+ function.getFieldName()
												+ " for calculation #"
												+ (i + 1));
							}
						}

						if (function.isRemovedFromResult()) {
							tempIndexList.add(Integer.valueOf(rowMeta.size()
									+ i));
						}
					}

					tempIndexes = new int[tempIndexList.size()];
					for (int i = 0; i < tempIndexes.length; i++) {
						tempIndexes[i] = ((Integer) tempIndexList.get(i))
								.intValue();
					}

					first = false;
				}
			}
		}

		boolean sendToErrorRow = false;
		String errorMessage = null;

		try {
			Object[] row = calcFields(rowMeta, r);
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(row);
			pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
			pReturn.setNextStream(this.getNextStepName());
			return pReturn;
		} catch (KettleException e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException(BaseMessages.getString(PKG,
						"InsertUpdate.Log.ErrorInStep"), e); //$NON-NLS-1$
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, r, 1, errorMessage, null,
						"IS_E001");
			}
		}

		return null;
	}

	public RowMetaInterface getOutputRowMeta() {
		return outputRowMeta;
	}

	public RowMetaInterface getCalcRowMeta() {
		return calcRowMeta;
	}

	public CalculatorMetaFunction[] getCalculation() {
		return calculation;
	}

	public FieldIndexes[] getFieldIndexes() {
		return fieldIndexes;
	}

	public int[] getTempIndexes() {
		return tempIndexes;
	}

	private Object[] calcFields(RowMetaInterface inputRowMeta, Object[] r)
			throws KettleException {
		Object[] calcData = RowDataUtil.resizeArray(r, calcRowMeta.size());

		for (int i = 0, index = inputRowMeta.size() + i; i < getCalculation().length; i++, index++) {
			CalculatorMetaFunction fn = getCalculation()[i];
			if (!Const.isEmpty(fn.getFieldName())) {
				ValueMetaInterface targetMeta = calcRowMeta.getValueMeta(index);

				ValueMetaInterface metaA = null;
				Object dataA = null;

				if (fieldIndexes[i].indexA >= 0) {
					metaA = calcRowMeta.getValueMeta(fieldIndexes[i].indexA);
					dataA = calcData[fieldIndexes[i].indexA];
				}

				ValueMetaInterface metaB = null;
				Object dataB = null;

				if (fieldIndexes[i].indexB >= 0) {
					metaB = calcRowMeta.getValueMeta(fieldIndexes[i].indexB);
					dataB = calcData[fieldIndexes[i].indexB];
				}

				ValueMetaInterface metaC = null;
				Object dataC = null;

				if (fieldIndexes[i].indexC >= 0) {
					metaC = calcRowMeta.getValueMeta(fieldIndexes[i].indexC);
					dataC = calcData[fieldIndexes[i].indexC];
				}

				int resultType;
				if (metaA != null) {
					resultType = metaA.getType();
				} else {
					resultType = ValueMetaInterface.TYPE_NONE;
				}

				switch (fn.getCalcType()) {
				case CalculatorMetaFunction.CALC_NONE:
					break;
				case CalculatorMetaFunction.CALC_COPY_OF_FIELD: {
					calcData[index] = dataA;
				}
					break;
				case CalculatorMetaFunction.CALC_ADD: // A + B
				{
					calcData[index] = ValueDataUtil.plus(metaA, dataA, metaB,
							dataB);
					if (metaA.isString() || metaB.isString())
						resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_SUBTRACT: // A - B
				{
					calcData[index] = ValueDataUtil.minus(metaA, dataA, metaB,
							dataB);
					if (metaA.isDate())
						resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_MULTIPLY: // A * B
				{
					calcData[index] = ValueDataUtil.multiply(metaA, dataA,
							metaB, dataB);
					if (metaA.isString() || metaB.isString())
						resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_DIVIDE: // A / B
				{
					calcData[index] = ValueDataUtil.divide(metaA, dataA, metaB,
							dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_SQUARE: // A * A
				{
					calcData[index] = ValueDataUtil.multiply(metaA, dataA,
							metaA, dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_SQUARE_ROOT: // SQRT( A )
				{
					calcData[index] = ValueDataUtil.sqrt(metaA, dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_PERCENT_1: // 100 * A / B
				{
					calcData[index] = ValueDataUtil.percent1(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_PERCENT_2: // A - ( A * B / 100
															// )
				{
					calcData[index] = ValueDataUtil.percent2(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_PERCENT_3: // A + ( A * B / 100
															// )
				{
					calcData[index] = ValueDataUtil.percent3(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_COMBINATION_1: // A + B * C
				{
					calcData[index] = ValueDataUtil.combination1(metaA, dataA,
							metaB, dataB, metaC, dataC);
				}
					break;
				case CalculatorMetaFunction.CALC_COMBINATION_2: // SQRT( A*A +
																// B*B )
				{
					calcData[index] = ValueDataUtil.combination2(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_ROUND_1: // ROUND( A )
				{
					calcData[index] = ValueDataUtil.round(metaA, dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_ROUND_2: // ROUND( A , B )
				{
					calcData[index] = ValueDataUtil.round(metaA, dataA, metaB,
							dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_CEIL: // CEIL( A )
				{
					calcData[index] = ValueDataUtil.ceil(metaA, dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_FLOOR: // FLOOR( A )
				{
					calcData[index] = ValueDataUtil.floor(metaA, dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_CONSTANT: // Set field to
															// constant value...
				{
					calcData[index] = fn.getFieldA(); // A string
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_NVL: // Replace null values
														// with another value
				{
					calcData[index] = ValueDataUtil.nvl(metaA, dataA, metaB,
							dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_ADD_DAYS: // Add B days to date
															// field A
				{
					calcData[index] = ValueDataUtil.addDays(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_ADD_HOURS: // Add B hours to
															// date field A
				{
					calcData[index] = ValueDataUtil.addHours(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_ADD_MINUTES: // Add B minutes
																// to date field
																// A
				{
					calcData[index] = ValueDataUtil.addMinutes(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_YEAR_OF_DATE: // What is the
																// year
																// (Integer) of
																// a date?
				{
					calcData[index] = ValueDataUtil.yearOfDate(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_MONTH_OF_DATE: // What is the
																// month
																// (Integer) of
																// a date?
				{
					calcData[index] = ValueDataUtil.monthOfDate(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_DAY_OF_YEAR: // What is the day
																// of year
																// (Integer) of
																// a date?
				{
					calcData[index] = ValueDataUtil.dayOfYear(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_DAY_OF_MONTH: // What is the
																// day of month
																// (Integer) of
																// a date?
				{
					calcData[index] = ValueDataUtil.dayOfMonth(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_DAY_OF_WEEK: // What is the day
																// of week
																// (Integer) of
																// a date?
				{
					calcData[index] = ValueDataUtil.dayOfWeek(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_WEEK_OF_YEAR: // What is the
																// week of year
																// (Integer) of
																// a date?
				{
					calcData[index] = ValueDataUtil.weekOfYear(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_WEEK_OF_YEAR_ISO8601: // What
																		// is
																		// the
																		// week
																		// of
																		// year
																		// (Integer)
																		// of a
																		// date
																		// ISO8601
																		// style?
				{
					calcData[index] = ValueDataUtil.weekOfYearISO8601(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_YEAR_OF_DATE_ISO8601: // What
																		// is
																		// the
																		// year
																		// (Integer)
																		// of a
																		// date
																		// ISO8601
																		// style?
				{
					calcData[index] = ValueDataUtil.yearOfDateISO8601(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_BYTE_TO_HEX_ENCODE: // Byte to
																		// Hex
																		// encode
																		// string
																		// field
																		// A
				{
					calcData[index] = ValueDataUtil.byteToHexEncode(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_HEX_TO_BYTE_DECODE: // Hex to
																		// Byte
																		// decode
																		// string
																		// field
																		// A
				{
					calcData[index] = ValueDataUtil.hexToByteDecode(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;

				case CalculatorMetaFunction.CALC_CHAR_TO_HEX_ENCODE: // Char to
																		// Hex
																		// encode
																		// string
																		// field
																		// A
				{
					calcData[index] = ValueDataUtil.charToHexEncode(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_HEX_TO_CHAR_DECODE: // Hex to
																		// Char
																		// decode
																		// string
																		// field
																		// A
				{
					calcData[index] = ValueDataUtil.hexToCharDecode(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_CRC32: // CRC32
				{
					calcData[index] = ValueDataUtil.ChecksumCRC32(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_ADLER32: // ADLER32
				{
					calcData[index] = ValueDataUtil.ChecksumAdler32(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_MD5: // MD5
				{
					calcData[index] = ValueDataUtil.createChecksum(metaA,
							dataA, "MD5");
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_SHA1: // SHA-1
				{
					calcData[index] = ValueDataUtil.createChecksum(metaA,
							dataA, "SHA-1");
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_LEVENSHTEIN_DISTANCE: // LEVENSHTEIN
																		// DISTANCE
				{
					calcData[index] = ValueDataUtil.getLevenshtein_Distance(
							metaA, dataA, metaB, dataB);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_METAPHONE: // METAPHONE
				{
					calcData[index] = ValueDataUtil.get_Metaphone(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_DOUBLE_METAPHONE: // Double
																	// METAPHONE
				{
					calcData[index] = ValueDataUtil.get_Double_Metaphone(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_ABS: // ABS( A )
				{
					calcData[index] = ValueDataUtil.abs(metaA, dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_REMOVE_TIME_FROM_DATE: // Remove
																		// Time
																		// from
																		// field
																		// A
				{
					calcData[index] = ValueDataUtil.removeTimeFromDate(metaA,
							dataA);
				}
					break;
				case CalculatorMetaFunction.CALC_DATE_DIFF: // DateA - DateB
				{
					calcData[index] = ValueDataUtil.DateDiff(metaA, dataA,
							metaB, dataB);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_ADD3: // A + B + C
				{
					calcData[index] = ValueDataUtil.plus3(metaA, dataA, metaB,
							dataB, metaC, dataC);
					if (metaA.isString() || metaB.isString()
							|| metaC.isString())
						resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_INITCAP: // InitCap( A )
				{
					calcData[index] = ValueDataUtil.initCap(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_UPPER_CASE: // UpperCase( A )
				{
					calcData[index] = ValueDataUtil.upperCase(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_LOWER_CASE: // UpperCase( A )
				{
					calcData[index] = ValueDataUtil.lowerCase(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_MASK_XML: // escapeXML( A )
				{
					calcData[index] = ValueDataUtil.escapeXML(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_USE_CDATA: // CDATA( A )
				{
					calcData[index] = ValueDataUtil.useCDATA(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_REMOVE_CR: // REMOVE CR FROM A
				{
					calcData[index] = ValueDataUtil.removeCR(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_REMOVE_LF: // REMOVE LF FROM A
				{
					calcData[index] = ValueDataUtil.removeLF(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_REMOVE_CRLF: // REMOVE CRLF
																// FROM A
				{
					calcData[index] = ValueDataUtil.removeCRLF(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_REMOVE_TAB: // REMOVE TAB FROM
																// A
				{
					calcData[index] = ValueDataUtil.removeTAB(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_GET_ONLY_DIGITS: // GET ONLY
																	// DIGITS
																	// FROM A
				{
					calcData[index] = ValueDataUtil.getDigits(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_REMOVE_DIGITS: // REMOVE DIGITS
																// FROM A
				{
					calcData[index] = ValueDataUtil.removeDigits(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_STRING_LEN: // RETURN THE
																// LENGTH OF A
				{
					calcData[index] = ValueDataUtil.stringLen(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_LOAD_FILE_CONTENT_BINARY: {
					calcData[index] = ValueDataUtil.loadFileContentInBinary(
							metaA, dataA);
					resultType = ValueMetaInterface.TYPE_BINARY;
				}
					break;
				case CalculatorMetaFunction.CALC_ADD_TIME_TO_DATE: // Add time B
																	// to a date
																	// A
				{
					calcData[index] = ValueDataUtil.addTimeToDate(metaA, dataA,
							metaB, dataB, metaC, dataC);
					resultType = ValueMetaInterface.TYPE_DATE;
				}
					break;
				case CalculatorMetaFunction.CALC_QUARTER_OF_DATE: // What is the
																	// quarter
																	// (Integer)
																	// of a
																	// date?
				{
					calcData[index] = ValueDataUtil.quarterOfDate(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_SUBSTITUTE_VARIABLE: // variable
																		// substitution
																		// in
																		// string
				{
					calcData[index] = (dataA.toString());
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_UNESCAPE_XML: // UnescapeXML( A
																// )
				{
					calcData[index] = ValueDataUtil.unEscapeXML(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_ESCAPE_HTML: // EscapeHTML( A )
				{
					calcData[index] = ValueDataUtil.escapeHTML(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_UNESCAPE_HTML: // UnescapeHTML(
																// A )
				{
					calcData[index] = ValueDataUtil.unEscapeHTML(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_ESCAPE_SQL: // EscapeSQL( A )
				{
					calcData[index] = ValueDataUtil.escapeSQL(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_DATE_WORKING_DIFF: // DateWorkingDiff(
																	// A , B)
				{
					calcData[index] = ValueDataUtil.DateWorkingDiff(metaA,
							dataA, metaB, dataB);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_ADD_MONTHS: // Add B months to
																// date field A
				{
					calcData[index] = ValueDataUtil.addMonths(metaA, dataA,
							metaB, dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_CHECK_XML_FILE_WELL_FORMED: // Check
																				// if
																				// file
																				// A
																				// is
																				// well
																				// formed
				{
					calcData[index] = ValueDataUtil.isXMLFileWellFormed(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_BOOLEAN;
				}
					break;
				case CalculatorMetaFunction.CALC_CHECK_XML_WELL_FORMED: // Check
																		// if
																		// xml A
																		// is
																		// well
																		// formed
				{
					calcData[index] = ValueDataUtil.isXMLWellFormed(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_BOOLEAN;
				}
					break;
				case CalculatorMetaFunction.CALC_GET_FILE_ENCODING: // Get file
																	// encoding
																	// from a
																	// file A
				{
					calcData[index] = ValueDataUtil.getFileEncoding(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_DAMERAU_LEVENSHTEIN: // DAMERAULEVENSHTEIN
																		// DISTANCE
				{
					calcData[index] = ValueDataUtil
							.getDamerauLevenshtein_Distance(metaA, dataA,
									metaB, dataB);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_NEEDLEMAN_WUNSH: // NEEDLEMANWUNSH
																	// DISTANCE
				{
					calcData[index] = ValueDataUtil
							.getNeedlemanWunsch_Distance(metaA, dataA, metaB,
									dataB);
					resultType = ValueMetaInterface.TYPE_INTEGER;
				}
					break;
				case CalculatorMetaFunction.CALC_JARO: // Jaro DISTANCE
				{
					calcData[index] = ValueDataUtil.getJaro_Similitude(metaA,
							dataA, metaB, dataB);
					resultType = ValueMetaInterface.TYPE_NUMBER;
				}
					break;
				case CalculatorMetaFunction.CALC_JARO_WINKLER: // Jaro DISTANCE
				{
					calcData[index] = ValueDataUtil.getJaroWinkler_Similitude(
							metaA, dataA, metaB, dataB);
					resultType = ValueMetaInterface.TYPE_NUMBER;
				}
					break;
				case CalculatorMetaFunction.CALC_SOUNDEX: // SOUNDEX
				{
					calcData[index] = ValueDataUtil.get_SoundEx(metaA, dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;
				case CalculatorMetaFunction.CALC_REFINED_SOUNDEX: // REFINEDSOUNDEX
				{
					calcData[index] = ValueDataUtil.get_RefinedSoundEx(metaA,
							dataA);
					resultType = ValueMetaInterface.TYPE_STRING;
				}
					break;

				case CalculatorMetaFunction.CALC_RELACE: {
					calcData[index] = ValueDataUtil.replace(metaA, dataA,
							metaB, dataB, metaC, dataC);
				}
					break;
				case CalculatorMetaFunction.CALC_TRIM: // A + B * C
				{
					calcData[index] = ValueDataUtil.trim(metaA, dataA, metaB,
							dataB);
				}
					break;
				case CalculatorMetaFunction.CALC_PADDING_LEFT: // A + B * C
				{
					calcData[index] = ValueDataUtil.paddingLeft(metaA, dataA,
							metaB, dataB, metaC, dataC);
				}
					break;
				case CalculatorMetaFunction.CALC_PADDING_RIGHT: // A + B * C
				{
					calcData[index] = ValueDataUtil.paddingRight(metaA, dataA,
							metaB, dataB, metaC, dataC);
				}
					break;

				default:
					throw new KettleException(BaseMessages.getString(PKG,
							"Calculator.Log.UnknownCalculationType")
							+ fn.getCalcType());
				}

				// If we don't have a target data type, throw an error.
				// Otherwise the result is non-deterministic.
				//
				if (targetMeta.getType() == ValueMetaInterface.TYPE_NONE) {
					throw new KettleException(BaseMessages.getString(PKG,
							"Calculator.Log.NoType")
							+ (i + 1)
							+ " : "
							+ fn.getFieldName()
							+ " = "
							+ fn.getCalcTypeDesc()
							+ " / " + fn.getCalcTypeLongDesc());
				}

				// Convert the data to the correct target data type.
				//
				if (calcData[index] != null) {
					if (targetMeta.getType() != resultType) {
						ValueMetaInterface resultMeta = new ValueMeta("result",
								resultType); // $NON-NLS-1$
						resultMeta.setConversionMask(fn.getConversionMask());
						resultMeta.setGroupingSymbol(fn.getGroupingSymbol());
						resultMeta.setDecimalSymbol(fn.getDecimalSymbol());
						resultMeta.setCurrencySymbol(fn.getCurrencySymbol());
						calcData[index] = targetMeta.convertData(resultMeta,
								calcData[index]);
					}
				}
			}
		}
		return RowDataUtil.removeItems(calcData, tempIndexes);
	}

	public String getTypeid() {
		return typeid;
	}

	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {
		for (int i = 0; i < calculation.length; i++) {
			CalculatorMetaFunction fn = calculation[i];
			if (!fn.isRemovedFromResult()) {
				if (!Const.isEmpty(fn.getFieldName())) // It's a new field!
				{
					ValueMetaInterface v = getValueMeta(fn, null);
					inputRowMeta.addValueMeta(v);
				}
			}
		}
	}

	private ValueMetaInterface getValueMeta(CalculatorMetaFunction fn,
			String origin) {
		ValueMetaInterface v = new ValueMeta(fn.getFieldName(),
				fn.getValueType());
		v.setLength(fn.getValueLength());
		v.setPrecision(fn.getValuePrecision());
		v.setOrigin(origin);
		v.setComments(fn.getCalcTypeDesc());
		v.setConversionMask(fn.getConversionMask());
		v.setDecimalSymbol(fn.getDecimalSymbol());
		v.setGroupingSymbol(fn.getGroupingSymbol());
		v.setCurrencySymbol(fn.getCurrencySymbol());

		// What if the user didn't specify a data type?
		// In that case we look for the default data type
		//
		if (fn.getValueType() == ValueMetaInterface.TYPE_NONE) {
			int defaultResultType = ValueMetaInterface.TYPE_NONE;

			switch (fn.getCalcType()) {
			case CalculatorMetaFunction.CALC_NONE:
				break;
			case CalculatorMetaFunction.CALC_ADD: // A + B
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_SUBTRACT: // A - B
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_MULTIPLY: // A * B
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_DIVIDE: // A / B
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_SQUARE: // A * A
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_SQUARE_ROOT: // SQRT( A )
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_PERCENT_1: // 100 * A / B
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_PERCENT_2: // A - ( A * B / 100 )
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_PERCENT_3: // A + ( A * B / 100 )
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_COMBINATION_1: // A + B * C
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_COMBINATION_2: // SQRT( A*A + B*B )
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_ROUND_1: // ROUND( A )
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_ROUND_2: // ROUND( A , B )
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_CONSTANT: // Set field to constant
														// value...
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_NVL: // Replace null values with
													// another value
				break;
			case CalculatorMetaFunction.CALC_ADD_DAYS: // Add B days to date
														// field A
				defaultResultType = ValueMetaInterface.TYPE_DATE;
				break;
			case CalculatorMetaFunction.CALC_YEAR_OF_DATE: // What is the year
															// (Integer) of a
															// date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_MONTH_OF_DATE: // What is the month
															// (Integer) of a
															// date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_DAY_OF_YEAR: // What is the day of
															// year (Integer) of
															// a date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_DAY_OF_MONTH: // What is the day of
															// month (Integer)
															// of a date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_DAY_OF_WEEK: // What is the day of
															// week (Integer) of
															// a date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_WEEK_OF_YEAR: // What is the week
															// of year (Integer)
															// of a date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_WEEK_OF_YEAR_ISO8601: // What is
																	// the week
																	// of year
																	// (Integer)
																	// of a date
																	// ISO8601
																	// style?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_YEAR_OF_DATE_ISO8601: // What is
																	// the year
																	// (Integer)
																	// of a date
																	// ISO8601
																	// style?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_BYTE_TO_HEX_ENCODE: // Byte to Hex
																	// encode
																	// string
																	// field A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_HEX_TO_BYTE_DECODE: // Hex to Byte
																	// decode
																	// string
																	// field A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_CHAR_TO_HEX_ENCODE: // Char to Hex
																	// encode
																	// string
																	// field A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_HEX_TO_CHAR_DECODE: // Hex to Char
																	// decode
																	// string
																	// field A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_CRC32: // CRC32 of a file A
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_ADLER32: // ADLER32 of a file A
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_MD5: // MD5 of a file A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_SHA1: // SHA1 of a file Al
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_LEVENSHTEIN_DISTANCE: // LEVENSHTEIN_DISTANCE
																	// of string
																	// A and
																	// string B
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_METAPHONE: // METAPHONE of string A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_DOUBLE_METAPHONE: // Double
																// METAPHONE of
																// string A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_ABS: // ABS( A )
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_REMOVE_TIME_FROM_DATE: // Remove
																	// time from
																	// field A
				defaultResultType = ValueMetaInterface.TYPE_DATE;
				break;
			case CalculatorMetaFunction.CALC_DATE_DIFF: // DateA - DateB
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_ADD3: // A + B +C
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_INITCAP: // InitCap(A)
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_UPPER_CASE: // UpperCase(A)
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_LOWER_CASE: // LowerCase(A)
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_MASK_XML: // MaskXML(A)
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_USE_CDATA: // CDATA(A)
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_REMOVE_CR: // REMOVE CR FROM string
														// A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_REMOVE_LF: // REMOVE LF FROM string
														// A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_REMOVE_CRLF: // REMOVE CRLF FROM
															// string A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_REMOVE_TAB: // REMOVE TAB FROM
															// string A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_GET_ONLY_DIGITS: // GET ONLY DIGITS
																// FROM string A
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_REMOVE_DIGITS: // REMOVE DIGITS
															// FROM string A
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_STRING_LEN: // LENGTH OF string A
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_LOAD_FILE_CONTENT_BINARY: // LOAD
																		// FILE
																		// CONTENT
																		// IN
																		// BLOB
				defaultResultType = ValueMetaInterface.TYPE_BINARY;
				break;
			case CalculatorMetaFunction.CALC_ADD_TIME_TO_DATE: // ADD TIME TO A
																// DATE
				defaultResultType = ValueMetaInterface.TYPE_DATE;
				break;
			case CalculatorMetaFunction.CALC_QUARTER_OF_DATE: // What is the
																// quarter
																// (Integer) of
																// a date?
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_SUBSTITUTE_VARIABLE: // variable
																	// substitution
																	// in string
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_ESCAPE_HTML: // escape HTML
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_ESCAPE_SQL: // escape SQL
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_UNESCAPE_HTML: // unEscape HTML
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_UNESCAPE_XML: // unEscape XML
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_DATE_WORKING_DIFF: // Date A - Date
																// B
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_ADD_MONTHS: // Date A - B Months
				defaultResultType = ValueMetaInterface.TYPE_DATE;
				break;
			case CalculatorMetaFunction.CALC_CHECK_XML_FILE_WELL_FORMED: // XML
																			// file
																			// A
																			// well
																			// formed
				defaultResultType = ValueMetaInterface.TYPE_BOOLEAN;
				break;
			case CalculatorMetaFunction.CALC_CHECK_XML_WELL_FORMED: // XML
																	// string A
																	// well
																	// formed
				defaultResultType = ValueMetaInterface.TYPE_BOOLEAN;
				break;
			case CalculatorMetaFunction.CALC_GET_FILE_ENCODING: // get file
																// encoding
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_DAMERAU_LEVENSHTEIN:
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_NEEDLEMAN_WUNSH:
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_JARO:
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_JARO_WINKLER:
				defaultResultType = ValueMetaInterface.TYPE_NUMBER;
				break;
			case CalculatorMetaFunction.CALC_SOUNDEX:
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_REFINED_SOUNDEX:
				defaultResultType = ValueMetaInterface.TYPE_STRING;
				break;
			case CalculatorMetaFunction.CALC_CEIL: // ROUND( A )
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			case CalculatorMetaFunction.CALC_FLOOR: // ROUND( A )
				defaultResultType = ValueMetaInterface.TYPE_INTEGER;
				break;
			default:
				break;
			}

			v.setType(defaultResultType);
		}

		return v;
	}

	public RowMetaInterface getAllFields(RowMetaInterface inputRowMeta) {
		RowMetaInterface rowMeta = inputRowMeta.clone();

		for (int i = 0; i < calculation.length; i++) {
			CalculatorMetaFunction fn = calculation[i];
			if (!Const.isEmpty(fn.getFieldName())) // It's a new field!
			{
				ValueMetaInterface v = getValueMeta(fn, null);
				rowMeta.addValueMeta(v);
			}
		}
		return rowMeta;
	}
 
}

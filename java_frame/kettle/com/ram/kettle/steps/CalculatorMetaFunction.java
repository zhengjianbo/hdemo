package com.ram.kettle.steps;

import org.w3c.dom.Node;

import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.ior.ObjectId;

public class CalculatorMetaFunction implements Cloneable {
	private static Class<?> PKG = CalculatorMetaFunction.class;

	public static final String XML_TAG = "calculation";

	public static final int CALC_NONE = 0;
	public static final int CALC_CONSTANT = 1;
	public static final int CALC_COPY_OF_FIELD = 2;
	public static final int CALC_ADD = 3;
	public static final int CALC_SUBTRACT = 4;
	public static final int CALC_MULTIPLY = 5;
	public static final int CALC_DIVIDE = 6;
	public static final int CALC_SQUARE = 7;
	public static final int CALC_SQUARE_ROOT = 8;
	public static final int CALC_PERCENT_1 = 9;
	public static final int CALC_PERCENT_2 = 10;
	public static final int CALC_PERCENT_3 = 11;
	public static final int CALC_COMBINATION_1 = 12;
	public static final int CALC_COMBINATION_2 = 13;
	public static final int CALC_ROUND_1 = 14;
	public static final int CALC_ROUND_2 = 15;
	public static final int CALC_CEIL = 16;
	public static final int CALC_FLOOR = 17;
	public static final int CALC_NVL = 18;
	public static final int CALC_ADD_DAYS = 19;
	public static final int CALC_YEAR_OF_DATE = 20;
	public static final int CALC_MONTH_OF_DATE = 21;
	public static final int CALC_DAY_OF_YEAR = 22;
	public static final int CALC_DAY_OF_MONTH = 23;
	public static final int CALC_DAY_OF_WEEK = 24;
	public static final int CALC_WEEK_OF_YEAR = 25;
	public static final int CALC_WEEK_OF_YEAR_ISO8601 = 26;
	public static final int CALC_YEAR_OF_DATE_ISO8601 = 27;
	public static final int CALC_BYTE_TO_HEX_ENCODE = 28;
	public static final int CALC_HEX_TO_BYTE_DECODE = 29;
	public static final int CALC_CHAR_TO_HEX_ENCODE = 30;
	public static final int CALC_HEX_TO_CHAR_DECODE = 31;
	public static final int CALC_CRC32 = 32;
	public static final int CALC_ADLER32 = 33;
	public static final int CALC_MD5 = 34;
	public static final int CALC_SHA1 = 35;
	public static final int CALC_LEVENSHTEIN_DISTANCE = 36;
	public static final int CALC_METAPHONE = 37;
	public static final int CALC_DOUBLE_METAPHONE = 38;
	public static final int CALC_ABS = 39;
	public static final int CALC_REMOVE_TIME_FROM_DATE = 40;
	public static final int CALC_DATE_DIFF = 41;
	public static final int CALC_ADD3 = 42;
	public static final int CALC_INITCAP = 43;
	public static final int CALC_UPPER_CASE = 44;
	public static final int CALC_LOWER_CASE = 45;
	public static final int CALC_MASK_XML = 46;
	public static final int CALC_USE_CDATA = 47;
	public static final int CALC_REMOVE_CR = 48;
	public static final int CALC_REMOVE_LF = 49;
	public static final int CALC_REMOVE_CRLF = 50;
	public static final int CALC_REMOVE_TAB = 51;
	public static final int CALC_GET_ONLY_DIGITS = 52;
	public static final int CALC_REMOVE_DIGITS = 53;
	public static final int CALC_STRING_LEN = 54;
	public static final int CALC_LOAD_FILE_CONTENT_BINARY = 55;
	public static final int CALC_ADD_TIME_TO_DATE = 56;
	public static final int CALC_QUARTER_OF_DATE = 57;
	public static final int CALC_SUBSTITUTE_VARIABLE = 58;
	public static final int CALC_UNESCAPE_XML = 59;
	public static final int CALC_ESCAPE_HTML = 60;
	public static final int CALC_UNESCAPE_HTML = 61;
	public static final int CALC_ESCAPE_SQL = 62;
	public static final int CALC_DATE_WORKING_DIFF = 63;
	public static final int CALC_ADD_MONTHS = 64;
	public static final int CALC_CHECK_XML_FILE_WELL_FORMED = 65;
	public static final int CALC_CHECK_XML_WELL_FORMED = 66;
	public static final int CALC_GET_FILE_ENCODING = 67;
	public static final int CALC_DAMERAU_LEVENSHTEIN = 68;
	public static final int CALC_NEEDLEMAN_WUNSH = 69;
	public static final int CALC_JARO = 70;
	public static final int CALC_JARO_WINKLER = 71;
	public static final int CALC_SOUNDEX = 72;
	public static final int CALC_REFINED_SOUNDEX = 73;
	public static final int CALC_ADD_HOURS = 74;
	public static final int CALC_ADD_MINUTES = 75;
	  public static final int    CALC_RELACE                = 76;
	  public static final int    CALC_TRIM               = 77;
	  public static final int    CALC_PADDING_LEFT                = 78;
	  public static final int    CALC_PADDING_RIGHT               = 79;
	  
	public static final String calc_desc[] = { "-", "CONSTANT", "COPY_FIELD",
			"ADD", "SUBTRACT", "MULTIPLY", "DIVIDE", "SQUARE", "SQUARE_ROOT",
			"PERCENT_1", "PERCENT_2", "PERCENT_3", "COMBINATION_1",
			"COMBINATION_2", "ROUND_1", "ROUND_2", "CEIL", "FLOOR", "NVL",
			"ADD_DAYS", "YEAR_OF_DATE", "MONTH_OF_DATE", "DAY_OF_YEAR",
			"DAY_OF_MONTH", "DAY_OF_WEEK", "WEEK_OF_YEAR",
			"WEEK_OF_YEAR_ISO8601", "YEAR_OF_DATE_ISO8601",
			"BYTE_TO_HEX_ENCODE", "HEX_TO_BYTE_DECODE", "CHAR_TO_HEX_ENCODE",
			"HEX_TO_CHAR_DECODE", "CRC32", "ADLER32", "MD5", "SHA1",
			"LEVENSHTEIN_DISTANCE", "METAPHONE", "DOUBLE_METAPHONE", "ABS",
			"REMOVE_TIME_FROM_DATE", "DATE_DIFF", "ADD3", "INIT_CAP",
			"UPPER_CASE", "LOWER_CASE", "MASK_XML", "USE_CDATA", "REMOVE_CR",
			"REMOVE_LF", "REMOVE_CRLF", "REMOVE_TAB", "GET_ONLY_DIGITS",
			"REMOVE_DIGITS", "STRING_LEN", "LOAD_FILE_CONTENT_BINARY",
			"ADD_TIME_TO_DATE", "QUARTER_OF_DATE", "SUBSTITUTE_VARIABLE",
			"UNESCAPE_XML", "ESCAPE_HTML", "UNESCAPE_HTML", "ESCAPE_SQL",
			"DATE_WORKING_DIFF", "ADD_MONTHS", "CHECK_XML_FILE_WELL_FORMED",
			"CHECK_XML_WELL_FORMED", "GET_FILE_ENCODING",
			"DAMERAU_LEVENSHTEIN", "NEEDLEMAN_WUNSH", "JARO", "JARO_WINKLER",
			"SOUNDEX", "REFINED_SOUNDEX", "ADD_HOURS", "ADD_MINUTES" ,	"STR_REPLACE",
        	"STR_TRIM",
        	"STR_PADDING_LEFT",
        	"STR_PADDING_RIGHT"};

	public static final String calcLongDesc[] = {
			"-",
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.SetFieldToConstant"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.CreateCopyOfField"),
			"A + B",
			"A - B",
			"A * B",
			"A / B",
			"A * A",
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.SQRT"),
			"100 * A / B",
			"A - ( A * B / 100 )",
			"A + ( A * B / 100 )",
			"A + B * C",
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Hypotenuse"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Round"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Round2"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Ceil"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Floor"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.NVL"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DatePlusDays"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.YearOfDate"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.MonthOfDate"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DayOfYear"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DayOfMonth"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DayOfWeek"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.WeekOfYear"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.WeekOfYearISO8601"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.YearOfDateISO8601"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.ByteToHexEncode"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.HexToByteDecode"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.CharToHexEncode"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.HexToCharDecode"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.CRC32"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Adler32"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.MD5"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.SHA1"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.LevenshteinDistance"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Metaphone"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DoubleMetaphone"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Abs"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RemoveTimeFromDate"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DateDiff"),
			"A + B + C",
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.InitCap"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.UpperCase"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.LowerCase"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.MaskXML"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.UseCDATA"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RemoveCR"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RemoveLF"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RemoveCRLF"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RemoveTAB"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.GetOnlyDigits"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RemoveDigits"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.StringLen"),
			BaseMessages
					.getString(PKG,
							"CalculatorMetaFunction.CalcFunctions.LoadFileContentInBinary"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.AddTimeToDate"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.QuarterOfDate"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.SubstituteVariable"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.UnescapeXML"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.EscapeHTML"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.UnescapeHTML"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.EscapeSQL"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DateDiffWorking"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DatePlusMonths"),
			BaseMessages
					.getString(PKG,
							"CalculatorMetaFunction.CalcFunctions.CheckXmlFileWellFormed"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.CheckXmlWellFormed"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.GetFileEncoding"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DamerauLevenshtein"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.NeedlemanWunsch"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.Jaro"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.JaroWinkler"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.SoundEx"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.RefinedSoundEx"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DatePlusHours"),
			BaseMessages.getString(PKG,
					"CalculatorMetaFunction.CalcFunctions.DatePlusMinutes")  ,"REPLACESTRING(A is string,B regex/string,C replace Field)"
		            ,"TRIM(A is string ,B is trimtype)"
		            ,"PADDING_LEFT(A is string,B is pchar, C is plen)"
		            ,"PADDING_RIGHT(A is string,B is pchar, C is plen)" };

	private String fieldName;
	private int calcType;
	private String fieldA;
	private String fieldB;
	private String fieldC;

	private int valueType;
	private int valueLength;
	private int valuePrecision;

	private String conversionMask;
	private String decimalSymbol;
	private String groupingSymbol;
	private String currencySymbol;

	private boolean removedFromResult;

	/**
	 * @param fieldName
	 * @param calcType
	 * @param fieldA
	 * @param fieldB
	 * @param fieldC
	 * @param valueType
	 * @param valueLength
	 * @param valuePrecision
	 * @param conversionMask
	 * @param decimalSymbol
	 * @param groupingSymbol
	 * @param currencySymbol
	 */
	public CalculatorMetaFunction(String fieldName, int calcType,
			String fieldA, String fieldB, String fieldC, int valueType,
			int valueLength, int valuePrecision, boolean removedFromResult,
			String conversionMask, String decimalSymbol, String groupingSymbol,
			String currencySymbol) {
		this.fieldName = fieldName;
		this.calcType = calcType;
		this.fieldA = fieldA;
		this.fieldB = fieldB;
		this.fieldC = fieldC;
		this.valueType = valueType;
		this.valueLength = valueLength;
		this.valuePrecision = valuePrecision;
		this.removedFromResult = removedFromResult;
		this.conversionMask = conversionMask;
		this.decimalSymbol = decimalSymbol;
		this.groupingSymbol = groupingSymbol;
		this.currencySymbol = currencySymbol;
	}
 

	public Object clone() {
		try {
			CalculatorMetaFunction retval = (CalculatorMetaFunction) super
					.clone();
			return retval;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
 
	public CalculatorMetaFunction(Node calcnode) {
		fieldName = XMLHandler.getTagValue(calcnode, "field_name");
		calcType = getCalcFunctionType(XMLHandler.getTagValue(calcnode,
				"calc_type"));
		fieldA = XMLHandler.getTagValue(calcnode, "field_a");
		fieldB = XMLHandler.getTagValue(calcnode, "field_b");
		fieldC = XMLHandler.getTagValue(calcnode, "field_c");
		valueType = ValueMeta.getType(XMLHandler.getTagValue(calcnode,
				"value_type"));
		valueLength = Const.toInt(
				XMLHandler.getTagValue(calcnode, "value_length"), -1);
		valuePrecision = Const.toInt(
				XMLHandler.getTagValue(calcnode, "value_precision"), -1);
		removedFromResult = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
				calcnode, "remove"));
		conversionMask = XMLHandler.getTagValue(calcnode, "conversion_mask");
		decimalSymbol = XMLHandler.getTagValue(calcnode, "decimal_symbol");
		groupingSymbol = XMLHandler.getTagValue(calcnode, "grouping_symbol");
		currencySymbol = XMLHandler.getTagValue(calcnode, "currency_symbol");

		if (XMLHandler.getSubNode(calcnode, "conversion_mask") == null) {
			fixBackwardCompatibility();
		}
	}

	private void fixBackwardCompatibility() {
		if (valueType == ValueMetaInterface.TYPE_INTEGER) {
			if (Const.isEmpty(conversionMask))
				conversionMask = "0";
			if (Const.isEmpty(decimalSymbol))
				decimalSymbol = ".";
			if (Const.isEmpty(groupingSymbol))
				groupingSymbol = ",";
		}
		if (valueType == ValueMetaInterface.TYPE_NUMBER) {
			if (Const.isEmpty(conversionMask))
				conversionMask = "0.0";
			if (Const.isEmpty(decimalSymbol))
				decimalSymbol = ".";
			if (Const.isEmpty(groupingSymbol))
				groupingSymbol = ",";
		}
	}

	public static final int getCalcFunctionType(String desc) {
		for (int i = 1; i < calc_desc.length; i++)
			if (calc_desc[i].equalsIgnoreCase(desc))
				return i;
		for (int i = 1; i < calcLongDesc.length; i++)
			if (calcLongDesc[i].equalsIgnoreCase(desc))
				return i;

		return CALC_NONE;
	}

	public static final String getCalcFunctionDesc(int type) {
		if (type < 0 || type >= calc_desc.length)
			return null;
		return calc_desc[type];
	}

	public static final String getCalcFunctionLongDesc(int type) {
		if (type < 0 || type >= calcLongDesc.length)
			return null;
		return calcLongDesc[type];
	}

	/**
	 * @return Returns the calcType.
	 */
	public int getCalcType() {
		return calcType;
	}

	/**
	 * @param calcType
	 *            The calcType to set.
	 */
	public void setCalcType(int calcType) {
		this.calcType = calcType;
	}

	public String getCalcTypeDesc() {
		return getCalcFunctionDesc(calcType);
	}

	public String getCalcTypeLongDesc() {
		return getCalcFunctionLongDesc(calcType);
	}

	/**
	 * @return Returns the fieldA.
	 */
	public String getFieldA() {
		return fieldA;
	}

	/**
	 * @param fieldA
	 *            The fieldA to set.
	 */
	public void setFieldA(String fieldA) {
		this.fieldA = fieldA;
	}

	/**
	 * @return Returns the fieldB.
	 */
	public String getFieldB() {
		return fieldB;
	}

	/**
	 * @param fieldB
	 *            The fieldB to set.
	 */
	public void setFieldB(String fieldB) {
		this.fieldB = fieldB;
	}

	/**
	 * @return Returns the fieldC.
	 */
	public String getFieldC() {
		return fieldC;
	}

	/**
	 * @param fieldC
	 *            The fieldC to set.
	 */
	public void setFieldC(String fieldC) {
		this.fieldC = fieldC;
	}

	/**
	 * @return Returns the fieldName.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            The fieldName to set.
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return Returns the valueLength.
	 */
	public int getValueLength() {
		return valueLength;
	}

	/**
	 * @param valueLength
	 *            The valueLength to set.
	 */
	public void setValueLength(int valueLength) {
		this.valueLength = valueLength;
	}

	/**
	 * @return Returns the valuePrecision.
	 */
	public int getValuePrecision() {
		return valuePrecision;
	}

	/**
	 * @param valuePrecision
	 *            The valuePrecision to set.
	 */
	public void setValuePrecision(int valuePrecision) {
		this.valuePrecision = valuePrecision;
	}

	/**
	 * @return Returns the valueType.
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * @param valueType
	 *            The valueType to set.
	 */
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return Returns the removedFromResult.
	 */
	public boolean isRemovedFromResult() {
		return removedFromResult;
	}

	/**
	 * @param removedFromResult
	 *            The removedFromResult to set.
	 */
	public void setRemovedFromResult(boolean removedFromResult) {
		this.removedFromResult = removedFromResult;
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
	 * @return the currencySymbol
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}

	/**
	 * @param currencySymbol
	 *            the currencySymbol to set
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
}

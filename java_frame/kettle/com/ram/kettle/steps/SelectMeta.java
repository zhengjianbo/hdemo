package com.ram.kettle.steps;

import org.w3c.dom.Node;

import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.xml.XMLHandler;

public class SelectMeta {
	// META-DATA mode
	/** Fields of which we want to change the meta data */
	private String name;
	/** Meta: new name of field */
	private String rename;
	/** Meta: new Value type for this field or TYPE_NONE if no change needed! */
	private int type;
	/** Meta: new length of field */
	private int length;
	/** Meta: new precision of field (for numbers) */
	private int precision;
	/** Meta: the storage type, NORMAL or BINARY_STRING */
	private int storageType;
	/** The conversion metadata if any conversion needs to take place */
	private String conversionMask;
	/** Treat the date format as lenient */
	private boolean dateFormatLenient;
	/** The decimal symbol for number conversions */
	private String decimalSymbol;
	/** The grouping symbol for number conversions */
	private String groupingSymbol;
	/** The currency symbol for number conversions */
	private String currencySymbol;
	/** The encoding to use when decoding binary data to Strings */
	private String encoding;

	public void setNode(Node metaNode) {
		name = XMLHandler.getTagValue(metaNode, "name"); //$NON-NLS-1$
		rename = XMLHandler.getTagValue(metaNode, ("rename")); //$NON-NLS-1$
		type = ValueMeta.getType(XMLHandler.getTagValue(metaNode, ("type"))); //$NON-NLS-1$
		length = Const.toInt(XMLHandler.getTagValue(metaNode, ("length")), -2); //$NON-NLS-1$
		precision = Const.toInt(
				XMLHandler.getTagValue(metaNode, ("precision")), -2); //$NON-NLS-1$
		storageType = ValueMeta.getStorageType(XMLHandler.getTagValue(metaNode,
				("storageType"))); //$NON-NLS-1$
		conversionMask = XMLHandler.getTagValue(metaNode, ("conversionMask")); //$NON-NLS-1$
		dateFormatLenient = Boolean.parseBoolean(XMLHandler.getTagValue(
				metaNode, ("dateFormatLenient"))); //$NON-NLS-1$
		encoding = XMLHandler.getTagValue(metaNode, ("encoding")); //$NON-NLS-1$
		decimalSymbol = XMLHandler.getTagValue(metaNode, ("decimalSymbol")); //$NON-NLS-1$
		groupingSymbol = XMLHandler.getTagValue(metaNode, ("groupingSymbol")); //$NON-NLS-1$
		currencySymbol = XMLHandler.getTagValue(metaNode, ("currencySymbol")); //$NON-NLS-1$
	}

	public SelectMeta(Node node) {
		this.setNode(node);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRename() {
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getStorageType() {
		return storageType;
	}

	public void setStorageType(int storageType) {
		this.storageType = storageType;
	}

	public String getConversionMask() {
		return conversionMask;
	}

	public void setConversionMask(String conversionMask) {
		this.conversionMask = conversionMask;
	}

	public boolean isDateFormatLenient() {
		return dateFormatLenient;
	}

	public void setDateFormatLenient(boolean dateFormatLenient) {
		this.dateFormatLenient = dateFormatLenient;
	}

	public String getDecimalSymbol() {
		return decimalSymbol;
	}

	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}

	public String getGroupingSymbol() {
		return groupingSymbol;
	}

	public void setGroupingSymbol(String groupingSymbol) {
		this.groupingSymbol = groupingSymbol;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}

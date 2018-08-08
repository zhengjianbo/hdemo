package com.ram.kettle.steps.impl;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaAndData;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class Constant extends StepMeta implements StepInterface {
	private static Class<?> PKG = Constant.class;
	private final String typeid = "Constant";

	private String currency[];
	private String decimal[];
	private String group[];
	private String value[];

	private String fieldName[];
	private String fieldType[];
	private String fieldFormat[];

	private int fieldLength[];
	private int fieldPrecision[];

	public NumberFormat nf;
	public DecimalFormat df;
	public DecimalFormatSymbols dfs;
	public SimpleDateFormat daf;
	public DateFormatSymbols dafs;
	public RowMetaAndData constants;
	RowMetaInterface outputMeta;

	public boolean firstRow;

	public Constant() {
		super();
	}

	public Constant(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrfields) {
		fieldName = new String[nrfields];
		fieldType = new String[nrfields];
		fieldFormat = new String[nrfields];
		fieldLength = new int[nrfields];
		fieldPrecision = new int[nrfields];
		currency = new String[nrfields];
		decimal = new String[nrfields];
		group = new String[nrfields];
		value = new String[nrfields];
	}

	public Object clone() {
		Constant retval = (Constant) super.clone();

		int nrfields = fieldName.length;

		retval.allocate(nrfields);

		for (int i = 0; i < nrfields; i++) {
			retval.fieldName[i] = fieldName[i];
			retval.fieldType[i] = fieldType[i];
			retval.fieldFormat[i] = fieldFormat[i];
			retval.currency[i] = currency[i];
			retval.decimal[i] = decimal[i];
			retval.group[i] = group[i];
			retval.value[i] = value[i];
			fieldLength[i] = fieldLength[i];
			fieldPrecision[i] = fieldPrecision[i];
		}

		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields");
			int nrfields = XMLHandler.countNodes(fields, "field");

			allocate(nrfields);

			String slength, sprecision;

			for (int i = 0; i < nrfields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);

				fieldName[i] = XMLHandler.getTagValue(fnode, "name");
				fieldType[i] = XMLHandler.getTagValue(fnode, "type");
				fieldFormat[i] = XMLHandler.getTagValue(fnode, "format");
				currency[i] = XMLHandler.getTagValue(fnode, "currency");
				decimal[i] = XMLHandler.getTagValue(fnode, "decimal");
				group[i] = XMLHandler.getTagValue(fnode, "group");
				value[i] = XMLHandler.getTagValue(fnode, "nullif");
				slength = XMLHandler.getTagValue(fnode, "length");
				sprecision = XMLHandler.getTagValue(fnode, "precision");

				fieldLength[i] = Const.toInt(slength, -1);
				fieldPrecision[i] = Const.toInt(sprecision, -1);
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
			nf = NumberFormat.getInstance();
			df = (DecimalFormat) nf;
			dfs = new DecimalFormatSymbols();
			daf = new SimpleDateFormat();
			dafs = new DateFormatSymbols();
			firstRow = true;

			constants = buildRow();
			return true;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public RowMetaAndData buildRow() {
		RowMetaInterface rowMeta = new RowMeta();
		Object[] rowData = new Object[getFieldName().length];

		for (int i = 0; i < getFieldName().length; i++) {
			int valtype = ValueMeta.getType(getFieldType()[i]);
			if (getFieldName()[i] != null) {
				ValueMetaInterface value = new ValueMeta(getFieldName()[i],
						valtype);
				value.setLength(getFieldLength()[i]);
				value.setPrecision(getFieldPrecision()[i]);
				String stringValue = getValue()[i];

				// If the value is empty: consider it to be NULL.
				if (stringValue == null || stringValue.length() == 0) {
					rowData[i] = null;

					if (value.getType() == ValueMetaInterface.TYPE_NONE) {

					}
				} else {
					switch (value.getType()) {
					case ValueMetaInterface.TYPE_NUMBER:
						try {
							if (getFieldFormat()[i] != null
									|| getDecimal()[i] != null
									|| getGroup()[i] != null
									|| getCurrency()[i] != null) {
								if (getFieldFormat()[i] != null
										&& getFieldFormat()[i].length() >= 1)
									df.applyPattern(getFieldFormat()[i]);
								if (getDecimal()[i] != null
										&& getDecimal()[i].length() >= 1)
									dfs.setDecimalSeparator(getDecimal()[i]
											.charAt(0));
								if (getGroup()[i] != null
										&& getGroup()[i].length() >= 1)
									dfs.setGroupingSeparator(getGroup()[i]
											.charAt(0));
								if (getCurrency()[i] != null
										&& getCurrency()[i].length() >= 1)
									dfs.setCurrencySymbol(getCurrency()[i]);

								df.setDecimalFormatSymbols(dfs);
							}

							rowData[i] = new Double(nf.parse(stringValue)
									.doubleValue());
						} catch (Exception e) {
							String message = BaseMessages.getString(PKG,
									"Constant.BuildRow.Error.Parsing.Number",
									value.getName(), stringValue, e.toString());

						}
						break;

					case ValueMetaInterface.TYPE_STRING:
						rowData[i] = stringValue;
						break;

					case ValueMetaInterface.TYPE_DATE:
						try {
							if (getFieldFormat()[i] != null) {
								daf.applyPattern(getFieldFormat()[i]);
								daf.setDateFormatSymbols(dafs);
							}

							rowData[i] = daf.parse(stringValue);
						} catch (Exception e) {
							String message = BaseMessages.getString(PKG,
									"Constant.BuildRow.Error.Parsing.Date",
									value.getName(), stringValue, e.toString());

						}
						break;

					case ValueMetaInterface.TYPE_INTEGER:
						try {
							rowData[i] = new Long(Long.parseLong(stringValue));
						} catch (Exception e) {
							String message = BaseMessages.getString(PKG,
									"Constant.BuildRow.Error.Parsing.Integer",
									value.getName(), stringValue, e.toString());

						}
						break;

					case ValueMetaInterface.TYPE_BIGNUMBER:
						try {
							rowData[i] = new BigDecimal(stringValue);
						} catch (Exception e) {
							String message = BaseMessages
									.getString(
											PKG,
											"Constant.BuildRow.Error.Parsing.BigNumber",
											value.getName(), stringValue,
											e.toString());

						}
						break;

					case ValueMetaInterface.TYPE_BOOLEAN:
						rowData[i] = Boolean.valueOf("Y"
								.equalsIgnoreCase(stringValue)
								|| "TRUE".equalsIgnoreCase(stringValue));
						break;

					case ValueMetaInterface.TYPE_BINARY:
						rowData[i] = stringValue.getBytes();
						break;

					default:
						String message = BaseMessages.getString(PKG,
								"Constant.CheckResult.SpecifyTypeError",
								value.getName(), stringValue);

					}
				}
				rowMeta.addValueMeta(value);
			}
		}

		return new RowMetaAndData(rowMeta, rowData);
	}

	public String[] getGroup() {
		return group;
	}

	public String[] getCurrency() {
		return currency;
	}

	public String[] getDecimal() {
		return decimal;
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
					if (firstRow) {
						outputMeta = rowMeta.clone();
						RowMetaInterface addRowMeta = constants.getRowMeta();
						outputMeta.mergeRowMeta(addRowMeta);
						firstRow = false;
					}
					first = false;
				}
			}
		}

		r = RowDataUtil.addRowData(r, rowMeta.size(), constants.getData());

		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(r);
		pReturn.setRowMeta(RowMeta.clone(outputMeta));
		pReturn.setNextStream(this.getNextStepName());
		return pReturn;
	}

	public String getTypeid() {
		return typeid;
	}

	public String[] getFieldName() {
		return fieldName;
	}

	public String[] getFieldType() {
		return fieldType;
	}

	public String[] getFieldFormat() {
		return fieldFormat;
	}

	public int[] getFieldLength() {
		return fieldLength;
	}

	public int[] getFieldPrecision() {
		return fieldPrecision;
	}

	public String[] getValue() {
		return value;
	}
	
	public String getMakeCode() {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(" Constant constMeta=new fieldFormat();   ").append("\r\n");
		return buffer.toString();
	}

	// 返回代码 不走流程图 走代码
	public String getInitCode() {
		
		StringBuffer bufferx=new StringBuffer("");
		for(String field:this.fieldName){
			bufferx.append("\""+field+"\",");
		}
		
		StringBuffer buffery=new StringBuffer("");
		for(String fieldType:this.fieldType){
			buffery.append("\""+fieldType+"\",");
		}
		 
		StringBuffer buffer = new StringBuffer("");
		buffer.append(
				"  injectMeta.setFieldname(new String[]{").append(bufferx).append(" });   ")
				.append("\r\n");
		buffer.append("  injectMeta.setType(new int[]{ ").append(buffery).append(" });   ")
				.append("\r\n");
	 
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

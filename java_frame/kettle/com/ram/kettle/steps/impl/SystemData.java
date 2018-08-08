package com.ram.kettle.steps.impl;

import java.util.Date;
import java.util.Random;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.steps.SystemDataMetaFunction;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.UUIDUtil;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class SystemData extends StepMeta implements StepInterface {
	private static Class<?> PKG = SystemData.class;
	private final String typeid = "SystemInfo";
	private String fieldName[];
	private int fieldType[];

	public boolean readsRows;
	public RowMetaInterface outputRowMeta;
	public final Random randomgen = new Random();

	public final static int TYPE_SYSTEM_INFO_NONE = 0;

	public final static int TYPE_SYSTEM_INFO_SYSTEM_DATE = 1;

	public final static int TYPE_SYSTEM_INFO_SYSTEM_START = 2;

	public final static int TYPE_SYSTEM_INFO_HOSTNAME = 3;

	public final static int TYPE_SYSTEM_INFO_IP_ADDRESS = 4;

	public final static int TYPE_RANDOM_NUMBER = 5;

	public final static int TYPE_RANDOM_INTEGER = 6;

	public final static int TYPE_RANDOM_STRING = 7;

	public final static int TYPE_RANDOM_UUID = 8;

	public static final SystemDataMetaFunction functions[] = new SystemDataMetaFunction[] {
			null,
			new SystemDataMetaFunction(TYPE_SYSTEM_INFO_SYSTEM_DATE,
					"system date (variable)", BaseMessages.getString(PKG,
							"TypeDesc.SystemDateVariable")),
			new SystemDataMetaFunction(TYPE_SYSTEM_INFO_SYSTEM_START,
					"system date (fixed)", BaseMessages.getString(PKG,
							"TypeDesc.SystemDateFixed")),
			new SystemDataMetaFunction(TYPE_SYSTEM_INFO_HOSTNAME, "Hostname",
					BaseMessages.getString(PKG, "TypeDesc.Hostname")),
			new SystemDataMetaFunction(TYPE_SYSTEM_INFO_IP_ADDRESS,
					"IP address", BaseMessages.getString(PKG,
							"TypeDesc.IPAddress")),

			new SystemDataMetaFunction(TYPE_RANDOM_NUMBER, "random number",
					BaseMessages.getString(PKG,
							"RandomValueMeta.TypeDesc.RandomNumber")),
			new SystemDataMetaFunction(TYPE_RANDOM_INTEGER, "random integer",
					BaseMessages.getString(PKG,
							"RandomValueMeta.TypeDesc.RandomInteger")),
			new SystemDataMetaFunction(TYPE_RANDOM_STRING, "random string",
					BaseMessages.getString(PKG,
							"RandomValueMeta.TypeDesc.RandomString")),
			new SystemDataMetaFunction(TYPE_RANDOM_UUID, "random uuid",
					BaseMessages.getString(PKG,
							"RandomValueMeta.TypeDesc.RandomUUID")),

	};

	public SystemData() {
		super();
	}

	public SystemData(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public static final int getFileType(String type) {
		for (int i = 1; i < functions.length; i++) {
			if (functions[i].getCode().equalsIgnoreCase(type))
				return i;
			if (functions[i].getDescription().equalsIgnoreCase(type))
				return i;
		}
		return 0;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			Node fields = XMLHandler.getSubNode(stepnode, "fields");
			int count = XMLHandler.countNodes(fields, "field");
			String type;

			allocate(count);

			for (int i = 0; i < count; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);

				fieldName[i] = XMLHandler.getTagValue(fnode, "name");
				type = XMLHandler.getTagValue(fnode, "type");
				fieldType[i] = getFileType(type);
			}
		} catch (Exception e) {
			throw new KettleException(
					"Unable to read step information from XML", e);
		}
	}

	public String[] getFieldName() {
		return fieldName;
	}

	public int[] getFieldType() {
		return fieldType;
	}

	public String getTypeId() {
		return typeid;
	}

	public void allocate(int count) {
		fieldName = new String[count];
		fieldType = new int[count];
	}

	public Object clone() {
		SystemData retval = (SystemData) super.clone();

		int count = fieldName.length;

		retval.allocate(count);

		for (int i = 0; i < count; i++) {
			retval.fieldName[i] = fieldName[i];
			retval.fieldType[i] = fieldType[i];
		}

		return retval;
	}

	public boolean init() {
		if (super.init()) { 
//			ConstLog.message("==" + this.getTypeId() + "=初始化"
//					+ this.getName());
		}
		return true;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {

		if (r == null) // no more input to be expected...
		{
			throw new KettleException("DATA ERROR");
		}

		if (first) {
			synchronized (this) {
				if (first) {
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());
					first = false;
				}
			}
		}

		r = getSystemData(rowMeta, r);
		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(r);
		pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
		pReturn.setNextStream(this.getNextStepName());
		return pReturn;

	}

	public void getFields(RowMetaInterface row, String name)
			throws KettleException {
		for (int i = 0; i < fieldName.length; i++) {
			ValueMetaInterface v;

			switch (fieldType[i]) {
			case TYPE_SYSTEM_INFO_SYSTEM_START:
			case TYPE_SYSTEM_INFO_SYSTEM_DATE:

				v = new ValueMeta(fieldName[i], ValueMetaInterface.TYPE_DATE);
				break;

			case TYPE_SYSTEM_INFO_HOSTNAME:
			case TYPE_SYSTEM_INFO_IP_ADDRESS:

				v = new ValueMeta(fieldName[i], ValueMetaInterface.TYPE_STRING);
				break;
			case TYPE_RANDOM_NUMBER:
				v = new ValueMeta(fieldName[i], ValueMetaInterface.TYPE_NUMBER,
						10, 5);
				break;
			case TYPE_RANDOM_INTEGER:
				v = new ValueMeta(fieldName[i],
						ValueMetaInterface.TYPE_INTEGER, 10, 0);
				break;
			case TYPE_RANDOM_STRING:
				v = new ValueMeta(fieldName[i], ValueMetaInterface.TYPE_STRING,
						13, 0);
				break;
			case TYPE_RANDOM_UUID:
				v = new ValueMeta(fieldName[i], ValueMetaInterface.TYPE_STRING,
						36, 0);
				break;

			default:
				v = new ValueMeta(fieldName[i], ValueMetaInterface.TYPE_NONE);
				break;
			}
			v.setOrigin(name);
			row.addValueMeta(v);
		}
	}

	private Object[] getSystemData(RowMetaInterface inputRowMeta,
			Object[] inputRowData) throws KettleException {
		Object[] row = new Object[outputRowMeta.size()];
		for (int i = 0; i < inputRowMeta.size(); i++) {
			row[i] = inputRowData[i];
		}
		for (int i = 0, index = inputRowMeta.size(); i < getFieldName().length; i++, index++) {

			switch (getFieldType()[i]) {
			case TYPE_SYSTEM_INFO_SYSTEM_START:
				row[index] = new Date();
				break;
			case TYPE_SYSTEM_INFO_SYSTEM_DATE:
				row[index] = new Date();
				break;

			case TYPE_SYSTEM_INFO_HOSTNAME:
				row[index] = Const.getHostname();
				break;
			case TYPE_SYSTEM_INFO_IP_ADDRESS:
				try {
					row[index] = Const.getIPAddress();
				} catch (Exception e) {
					throw new KettleException(e);
				}
				break;
			case TYPE_RANDOM_NUMBER:
				row[index] = randomgen.nextDouble();
				break;
			case TYPE_RANDOM_INTEGER:
				row[index] = new Long(randomgen.nextInt()); // nextInt() already
															// returns all 2^32
															// numbers.
				break;
			case TYPE_RANDOM_STRING:
				row[index] = Long.toString(Math.abs(randomgen.nextLong()), 32);
				break;
			case TYPE_RANDOM_UUID:
				row[index] = UUIDUtil.getUUIDAsString();
				break;

			default:
				row[index] = null;
				break;
			}
		}

		return row;
	}

	public String getTypeid() {
		return typeid;
	}
}

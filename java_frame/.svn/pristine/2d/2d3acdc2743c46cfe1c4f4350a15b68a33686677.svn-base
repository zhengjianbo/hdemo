package com.ram.kettle.steps.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.steps.SelectMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class SelectValues extends StepMeta implements StepInterface {
	private static Class<?> PKG = SelectValues.class;
	private final String typeid = "SelectValues";

	private final String meta_XML_TAG = "meta";

	// SELECT mode
	/** Select: Name of the selected field */
	private String selectName[];

	/** Select: Rename to ... */
	private String selectRename[];

	/** Select: length of field */
	private int selectLength[];

	/** Select: Precision of field (for numbers) */
	private int selectPrecision[];

	private boolean selectingAndSortingUnspecifiedFields;

	private String deleteName[];

	private SelectMeta[] meta;

	public int[] fieldnrs;
	public int[] extraFieldnrs;
	public int[] removenrs;
	public int[] metanrs;

	public boolean firstselect;
	public boolean firstdeselect;
	public boolean firstmetadata;

	public RowMetaInterface selectRowMeta;
	public RowMetaInterface deselectRowMeta;
	public RowMetaInterface metadataRowMeta;

	public RowMetaInterface outputRowMeta;

	// The MODE, default = select...
	public boolean select; // "normal" selection of fields.
	public boolean deselect; // de-select mode
	public boolean metadata; // change meta-data (rename & change
								// length/precision)

	public void allocate(int nrFields, int nrRemove, int nrMeta) {
		allocateSelect(nrFields);
		allocateRemove(nrRemove);
		allocateMeta(nrMeta);
	}

	private void allocateSelect(int nrFields) {
		selectName = new String[nrFields];
		selectRename = new String[nrFields];
		selectLength = new int[nrFields];
		selectPrecision = new int[nrFields];
	}

	private void allocateRemove(int nrRemove) {
		deleteName = new String[nrRemove];
	}

	private void allocateMeta(int nrMeta) {
		meta = new SelectMeta[nrMeta];
	}

	public Object clone() {
		SelectValues retval = (SelectValues) super.clone();

		int nrfields = selectName.length;
		int nrremove = deleteName.length;
		int nrmeta = meta.length;

		retval.allocate(nrfields, nrremove, nrmeta);

		for (int i = 0; i < nrfields; i++) {
			retval.selectName[i] = selectName[i];
			retval.selectRename[i] = selectRename[i];
			retval.selectLength[i] = selectLength[i];
			retval.selectPrecision[i] = selectPrecision[i];
		}

		for (int i = 0; i < nrremove; i++) {
			retval.deleteName[i] = deleteName[i];
		}

		for (int i = 0; i < nrmeta; i++) {
			retval.getMeta()[i] = meta[i];
		}

		return retval;
	}

	public SelectMeta[] getMeta() {
		return meta;
	}

	public SelectValues() {
		super();
	}

	public SelectValues(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrFields) {

	}

	public void setNode(Node step) throws KettleException {
		super.setNode(step);
		try {
			Node fields = XMLHandler.getSubNode(step, "fields"); //$NON-NLS-1$

			int nrfields = XMLHandler.countNodes(fields, "field"); //$NON-NLS-1$
			int nrremove = XMLHandler.countNodes(fields, "remove"); //$NON-NLS-1$
			int nrmeta = XMLHandler.countNodes(fields, meta_XML_TAG); //$NON-NLS-1$
			allocate(nrfields, nrremove, nrmeta);

			for (int i = 0; i < nrfields; i++) {
				Node line = XMLHandler.getSubNodeByNr(fields, "field", i); //$NON-NLS-1$
				selectName[i] = XMLHandler.getTagValue(line, "name"); //$NON-NLS-1$
				selectRename[i] = XMLHandler.getTagValue(line, "rename"); //$NON-NLS-1$
				selectLength[i] = Const.toInt(
						XMLHandler.getTagValue(line, "length"), -2); // $NON-NtagLS-1$
				selectPrecision[i] = Const.toInt(
						XMLHandler.getTagValue(line, "precision"), -2); //$NON-NLS-1$
			}
			selectingAndSortingUnspecifiedFields = "Y"
					.equalsIgnoreCase(XMLHandler.getTagValue(fields,
							"select_unspecified"));

			for (int i = 0; i < nrremove; i++) {
				Node line = XMLHandler.getSubNodeByNr(fields, "remove", i); //$NON-NLS-1$
				deleteName[i] = XMLHandler.getTagValue(line, "name"); //$NON-NLS-1$
			}

			for (int i = 0; i < nrmeta; i++) {
				Node metaNode = XMLHandler.getSubNodeByNr(fields, meta_XML_TAG,
						i);
				meta[i] = new SelectMeta(metaNode);
			}

			firstselect = true;
			firstdeselect = true;
			firstmetadata = true;

			select = false;
			deselect = false;
			metadata = false;

			if (!Const.isEmpty(getSelectName()))
				select = true;
			if (!Const.isEmpty(getDeleteName()))
				deselect = true;
			if (!Const.isEmpty(getMeta()))
				metadata = true;

			boolean atLeastOne = select || deselect || metadata;
			if (!atLeastOne) {
				throw new KettleException(
						"SelectValuesMeta.Exception.UnableToReadStepInfoFromXML");
			}
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"SelectValuesMeta.Exception.UnableToReadStepInfoFromXML"),
					e);
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) {
//			ConstLog.message("==" + this.getTypeId() + "=初始化"
//					+ this.getName());
		}
		return true;
	}

	private void removeValuesFirst(RowMetaInterface rowMeta)
			throws KettleException {
		if (firstdeselect) {

			removenrs = new int[getDeleteName().length];
			for (int i = 0; i < removenrs.length; i++) {
				removenrs[i] = rowMeta.indexOfValue(getDeleteName()[i]);
				if (removenrs[i] < 0) {
					throw new KettleException(
							"SelectValues.Log.CouldNotFindField:"
									+ getDeleteName()[i]);
				}
			}

			// Check for doubles in the selected fields...
			int cnt[] = new int[getDeleteName().length];
			for (int i = 0; i < getDeleteName().length; i++) {
				cnt[i] = 0;
				for (int j = 0; j < getDeleteName().length; j++) {
					if (getDeleteName()[i].equals(getDeleteName()[j]))
						cnt[i]++;

					if (cnt[i] > 1) {
						throw new KettleException(
								"SelectValues.Log.FieldCouldNotSpecifiedMoreThanTwice2:"
										+ getDeleteName()[i]);
					}
				}
			}
			Arrays.sort(removenrs);
			firstdeselect = false;
		}

	}

	public int[] getSelectLength() {
		return selectLength;
	}

	public String[] getDeleteName() {
		return deleteName;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] row)
			throws KettleException {

		if (row == null) {
			throw new KettleException("DATA ERROR");
		}
		boolean sendToErrorRow = false;
		String errorMessage = null;

		if (first) {
			// 初始化时候同步下
			synchronized (this) {
				if (first) {

					selectRowMeta = rowMeta.clone();
					getSelectFields(selectRowMeta, getStepname());
					selectValuesFirst(rowMeta);

					deselectRowMeta = selectRowMeta.clone();
					getDeleteFields(deselectRowMeta);
					removeValuesFirst(selectRowMeta);

					metadataRowMeta = deselectRowMeta.clone();
					getMetadataFields(metadataRowMeta, getStepname());
					metadataValuesFirst(deselectRowMeta);

					first = false;
				}
			}
		}

		try {
			Object[] outputData = row;

			if (select)
				outputData = selectValues(rowMeta, outputData);
			if (deselect)
				outputData = removeValues(selectRowMeta, outputData);
			if (metadata)
				outputData = metadataValues(deselectRowMeta, outputData);

			if (outputData == null) {
				throw new KettleException("DATA ERROR");
			}
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(outputData);
			pReturn.setRowMeta(RowMeta.clone(metadataRowMeta));
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
				return putErrorReturn(rowMeta, row, 1, errorMessage, null,
						"IS_E001");
			}
		}
		return null;
	}

	private Object[] removeValues(RowMetaInterface rowMeta, Object[] rowData) {

		return RowDataUtil.removeItems(rowData, removenrs);
	}

	private Object[] metadataValues(RowMetaInterface rowMeta, Object[] rowData)
			throws KettleException {

		for (int i = 0; i < metanrs.length; i++) {
			int index = metanrs[i];
			ValueMetaInterface fromMeta = rowMeta.getValueMeta(index);
			ValueMetaInterface toMeta = metadataRowMeta.getValueMeta(index);

			if (fromMeta.isStorageBinaryString()
					&& getMeta()[i].getStorageType() == ValueMetaInterface.STORAGE_TYPE_NORMAL) {
				// 延时加载的从前面获取的
				rowData[index] = fromMeta
						.convertBinaryStringToNativeType((byte[]) rowData[index]);
			}
			if (getMeta()[i].getType() != ValueMetaInterface.TYPE_NONE
					&& fromMeta.getType() != toMeta.getType()) {
				switch (toMeta.getType()) {
				// case ValueMetaInterface.TYPE_NSTRING :
				case ValueMetaInterface.TYPE_STRING:
					rowData[index] = fromMeta.getString(rowData[index]);
					break;
				case ValueMetaInterface.TYPE_NUMBER:
					rowData[index] = fromMeta.getNumber(rowData[index]);
					break;
				case ValueMetaInterface.TYPE_INTEGER:
					rowData[index] = fromMeta.getInteger(rowData[index]);
					break;
				case ValueMetaInterface.TYPE_DATE:
					rowData[index] = fromMeta.getDate(rowData[index]);
					break;
				case ValueMetaInterface.TYPE_BIGNUMBER:
					rowData[index] = fromMeta.getBigNumber(rowData[index]);
					break;
				case ValueMetaInterface.TYPE_BOOLEAN:
					rowData[index] = fromMeta.getBoolean(rowData[index]);
					break;
				case ValueMetaInterface.TYPE_BINARY:
					rowData[index] = fromMeta.getBinary(rowData[index]);
					break;
				default:
					throw new KettleException(
							"Unable to convert data type of value '" + fromMeta
									+ "' to data type " + toMeta.getType());
				}
			}
		}

		return rowData;
	}

	private Object[] selectValues(RowMetaInterface rowMeta, Object[] rowData)
			throws KettleException {
		// Create a new output row
		Object[] outputData = new Object[selectRowMeta.size()];
		int outputIndex = 0;
		for (int idx : fieldnrs) {
			if (idx < rowMeta.size()) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(idx);

				outputData[outputIndex++] = valueMeta
						.cloneValueData(rowData[idx]);
			} else {

			}
		}
		for (int idx : extraFieldnrs) {
			outputData[outputIndex++] = rowData[idx];
		}

		return outputData;
	}

	private void metadataValuesFirst(RowMetaInterface rowMeta)
			throws KettleException {
		if (firstmetadata) {

			metanrs = new int[getMeta().length];
			for (int i = 0; i < metanrs.length; i++) {
				metanrs[i] = rowMeta.indexOfValue(getMeta()[i].getName());
				if (metanrs[i] < 0) {
					throw new KettleException("SelectValues.Log.MetaValues:"
							+ getMeta()[i].getName());
				}
			}

			// Check for doubles in the selected fields...
			int cnt[] = new int[getMeta().length];
			for (int i = 0; i < getMeta().length; i++) {
				cnt[i] = 0;
				for (int j = 0; j < getMeta().length; j++) {
					if (getMeta()[i].getName().equals(getMeta()[j].getName()))
						cnt[i]++;

					if (cnt[i] > 1) {
						throw new KettleException(
								"SelectValues.Log.Meta.FieldCouldNotSpecifiedMoreThanTwice2:"
										+ getMeta()[i].getName());
					}
				}
			}

			for (int i = 0; i < metanrs.length; i++) {
				SelectMeta change = getMeta()[i];
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(metanrs[i]);
				if (!Const.isEmpty(change.getConversionMask())) {
					valueMeta.setConversionMask(change.getConversionMask());
				}

				valueMeta.setDateFormatLenient(change.isDateFormatLenient());

				if (!Const.isEmpty(change.getEncoding())) {
					valueMeta.setStringEncoding(change.getEncoding());
				}
				if (!Const.isEmpty(change.getDecimalSymbol())) {
					valueMeta.setDecimalSymbol(change.getDecimalSymbol());
				}
				if (!Const.isEmpty(change.getGroupingSymbol())) {
					valueMeta.setGroupingSymbol(change.getGroupingSymbol());
				}
				if (!Const.isEmpty(change.getCurrencySymbol())) {
					valueMeta.setCurrencySymbol(change.getCurrencySymbol());
				}
			}
			firstmetadata = false;
		}
	}

	public void getSelectFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {
		RowMetaInterface row;

		if (selectName != null && selectName.length > 0) // SELECT values
		{
			row = new RowMeta();
			for (int i = 0; i < selectName.length; i++) {
				ValueMetaInterface v = inputRowMeta
						.searchValueMeta(selectName[i]);

				if (v != null) {
					v = v.clone();
					if (!v.getName().equals(selectRename[i])
							&& selectRename[i] != null
							&& selectRename[i].length() > 0) {
						v.setName(selectRename[i]);
						v.setOrigin(name);
					}
					if (selectLength[i] != -2) {
						v.setLength(selectLength[i]);
						v.setOrigin(name);
					}
					if (selectPrecision[i] != -2) {
						v.setPrecision(selectPrecision[i]);
						v.setOrigin(name);
					}
					row.addValueMeta(v);
				}
			}

			if (selectingAndSortingUnspecifiedFields) {
				List<String> extra = new ArrayList<String>();
				for (int i = 0; i < inputRowMeta.size(); i++) {
					String fieldName = inputRowMeta.getValueMeta(i).getName();
					if (Const.indexOfString(fieldName, selectName) < 0) {
						extra.add(fieldName);
					}
				}
				Collections.sort(extra);
				for (String fieldName : extra) {
					ValueMetaInterface extraValue = inputRowMeta
							.searchValueMeta(fieldName);
					row.addValueMeta(extraValue);
				}
			}

			inputRowMeta.clear();
			inputRowMeta.addRowMeta(row);
		}
	}

	public void getDeleteFields(RowMetaInterface inputRowMeta)
			throws KettleException {
		if (deleteName != null && deleteName.length > 0) // DESELECT values from
															// the stream...
		{
			for (int i = 0; i < deleteName.length; i++) {
				try {
					inputRowMeta.removeValueMeta(deleteName[i]);
				} catch (KettleException e) {
					throw new KettleException(e);
				}
			}
		}
	}

	public void getMetadataFields(RowMetaInterface inputRowMeta, String name) {
		if (meta != null && meta.length > 0) // METADATA mode: change the
												// meta-data of the values
												// mentioned...
		{
			for (int i = 0; i < meta.length; i++) {
				SelectMeta metaChange = meta[i];

				int idx = inputRowMeta.indexOfValue(metaChange.getName());
				if (idx >= 0) // We found the value
				{
					// This is the value we need to change:
					ValueMetaInterface v = inputRowMeta.getValueMeta(idx);

					// Do we need to rename ?
					if (!v.getName().equals(metaChange.getRename())
							&& !Const.isEmpty(metaChange.getRename())) {
						v.setName(metaChange.getRename());
						v.setOrigin(name);
					}
					// Change the type?
					if (metaChange.getType() != ValueMetaInterface.TYPE_NONE
							&& v.getType() != metaChange.getType()) {
						v.setType(metaChange.getType());

						// This also moves the data to normal storage type
						//
						v.setStorageType(ValueMetaInterface.STORAGE_TYPE_NORMAL);
					}
					if (metaChange.getLength() != -2) {
						v.setLength(metaChange.getLength());
						v.setOrigin(name);
					}
					if (metaChange.getPrecision() != -2) {
						v.setPrecision(metaChange.getPrecision());
						v.setOrigin(name);
					}
					if (metaChange.getStorageType() >= 0) {
						v.setStorageType(metaChange.getStorageType());
						v.setOrigin(name);
					}
					if (!Const.isEmpty(metaChange.getConversionMask())) {
						v.setConversionMask(metaChange.getConversionMask());
						v.setOrigin(name);
					}

					v.setDateFormatLenient(metaChange.isDateFormatLenient());

					if (!Const.isEmpty(metaChange.getEncoding())) {
						v.setStringEncoding(metaChange.getEncoding());
						v.setOrigin(name);
					}
					if (!Const.isEmpty(metaChange.getDecimalSymbol())) {
						v.setDecimalSymbol(metaChange.getDecimalSymbol());
						v.setOrigin(name);
					}
					if (!Const.isEmpty(metaChange.getGroupingSymbol())) {
						v.setGroupingSymbol(metaChange.getGroupingSymbol());
						v.setOrigin(name);
					}
					if (!Const.isEmpty(metaChange.getCurrencySymbol())) {
						v.setCurrencySymbol(metaChange.getCurrencySymbol());
						v.setOrigin(name);
					}
				}
			}
		}
	}

	public void getFields(RowMetaInterface inputRowMeta, String name)
			throws KettleException {
		RowMetaInterface rowMeta = inputRowMeta.clone();
		inputRowMeta.clear();
		inputRowMeta.addRowMeta(rowMeta);

		getSelectFields(inputRowMeta, name);
		getDeleteFields(inputRowMeta);
		getMetadataFields(inputRowMeta, name);
	}

	private void selectValuesFirst(RowMetaInterface rowMeta)
			throws KettleException {
		if (firstselect) {

			fieldnrs = new int[getSelectName().length];
			for (int i = 0; i < fieldnrs.length; i++) {
				fieldnrs[i] = rowMeta.indexOfValue(getSelectName()[i]);
				if (fieldnrs[i] < 0) {
					throw new KettleException("SELECT NO FOUND:"
							+ getSelectName()[i]);
				}
			}
			int cnt[] = new int[getSelectName().length];
			for (int i = 0; i < getSelectName().length; i++) {
				cnt[i] = 0;
				for (int j = 0; j < getSelectName().length; j++) {
					String one = Const.NVL(getSelectRename()[i],
							getSelectName()[i]);
					String two = Const.NVL(getSelectRename()[j],
							getSelectName()[j]);
					if (one.equals(two))
						cnt[i]++;

					if (cnt[i] > 1) {
						throw new KettleException("COPY COLUMN:" + one);
					}
				}
			}

			if (isSelectingAndSortingUnspecifiedFields()) {

				List<String> extra = new ArrayList<String>();
				ArrayList<Integer> unspecifiedKeyNrs = new ArrayList<Integer>();
				for (int i = 0; i < rowMeta.size(); i++) {
					String fieldName = rowMeta.getValueMeta(i).getName();
					if (Const.indexOfString(fieldName, getSelectName()) < 0) {
						extra.add(fieldName);
					}
				}
				Collections.sort(extra);
				for (String fieldName : extra) {
					int index = rowMeta.indexOfValue(fieldName);
					unspecifiedKeyNrs.add(index);
				}
				extraFieldnrs = new int[unspecifiedKeyNrs.size()];
				for (int i = 0; i < extraFieldnrs.length; i++)
					extraFieldnrs[i] = unspecifiedKeyNrs.get(i);
			} else {
				extraFieldnrs = new int[] {};
			}
			firstselect = false;
		}

	}

	public String[] getSelectRename() {
		return selectRename;
	}

	public String[] getSelectName() {
		return selectName;
	}

	public boolean isSelectingAndSortingUnspecifiedFields() {
		return selectingAndSortingUnspecifiedFields;
	}

	public String getTypeid() {
		return typeid;
	}
}

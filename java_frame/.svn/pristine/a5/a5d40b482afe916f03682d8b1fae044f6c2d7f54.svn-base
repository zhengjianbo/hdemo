package com.ram.kettle.steps.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Node;

import com.ram.kettle.database.DatabaseMeta;
import com.ram.kettle.database.WEB_Database;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

@SuppressWarnings("unused")
public class Update extends StepMeta implements StepInterface {
	private static Class<?> PKG = Update.class;
	private final String typeid = "Update";

	/** The lookup table name */
	private String schemaName;

	/** The lookup table name */
	private String tableName;

	/** database connection */
	private DatabaseMeta databaseMeta;

	/** which field in input stream to compare with? */
	private String keyStream[];

	/** field in table */
	private String keyLookup[];

	/** Comparator: =, <>, BETWEEN, ... */
	private String keyCondition[];

	/** Extra field for between... */
	private String keyStream2[];

	/** Field value to update after lookup */
	private String updateLookup[];

	/** Stream name to update value with */
	private String updateStream[];

	/** Commit size for inserts/updates */
	private int commitSize;

	/** update errors are ignored if this flag is set to true */
	private boolean errorIgnored;

	/** adds a boolean field to the output indicating success of the update */
	private String ignoreFlagField;

	/** adds a boolean field to skip lookup and directly update selected fields */
	private boolean skipLookup;

	/**
	 * Flag to indicate the use of batch updates, enabled by default but
	 * disabled for backward compatibility
	 */
	private boolean useBatchUpdate;
	/*******
	 * zhengjianbo 2014-04-23* 强制使用索引
	 * *************/
	public String index;
	private String INDEXFLAG = "sindex";

	public String returnrow = null;
	private String RETURNROW = "returnrow";

	// ---------------
	public WEB_Database web_db;

	public int keynrs[]; // nr of keylookup -value in row...
	public int keynrs2[]; // nr of keylookup2-value in row...
	public int valuenrs[]; // Stream valuename nrs to prevent searches.

	public String stringErrorKeyNotFound;

	public String stringFieldnames;

	public RowMetaInterface outputRowMeta;

	public String schemaTable;
 

	public RowMetaInterface lookupParameterRowMeta;
	public RowMetaInterface lookupReturnRowMeta;
	public RowMetaInterface updateParameterRowMeta;

	public boolean hadInit = false;

	public String selectSql, updateSql;

	public Update() {
		super();
	}

	public Update(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrkeys, int nrvalues) {
		keyStream = new String[nrkeys];
		keyLookup = new String[nrkeys];
		keyCondition = new String[nrkeys];
		keyStream2 = new String[nrkeys];
		updateLookup = new String[nrvalues];
		updateStream = new String[nrvalues];
	}

	public Object clone() {
		Update retval = (Update) super.clone();
		int nrkeys = keyStream.length;
		int nrvalues = updateLookup.length;

		retval.allocate(nrkeys, nrvalues);

		for (int i = 0; i < nrkeys; i++) {
			retval.keyStream[i] = keyStream[i];
			retval.keyLookup[i] = keyLookup[i];
			retval.keyCondition[i] = keyCondition[i];
			retval.keyStream2[i] = keyStream2[i];
		}

		for (int i = 0; i < nrvalues; i++) {
			retval.updateLookup[i] = updateLookup[i];
			retval.updateStream[i] = updateStream[i];
		}
		return retval;
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			String csize;
			int nrkeys, nrvalues;
			index = XMLHandler.getTagValue(stepnode, this.INDEXFLAG); //$NON-NLS-1$ //$NON-NLS-2$

			this.returnrow = XMLHandler.getTagValue(stepnode, this.RETURNROW);

			String con = XMLHandler.getTagValue(stepnode, "connection"); //$NON-NLS-1$
			databaseMeta = DatabaseMeta.findDatabase(con);
			csize = XMLHandler.getTagValue(stepnode, "commit"); //$NON-NLS-1$
			commitSize = Const.toInt(csize, 0);
			useBatchUpdate = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
					stepnode, "use_batch"));
			skipLookup = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode,
					"skip_lookup"));
			errorIgnored = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "error_ignored")); //$NON-NLS-1$ //$NON-NLS-2$
			ignoreFlagField = XMLHandler.getTagValue(stepnode,
					"ignore_flag_field"); //$NON-NLS-1$
			schemaName = XMLHandler.getTagValue(stepnode, "lookup", "schema"); //$NON-NLS-1$ //$NON-NLS-2$
			tableName = XMLHandler.getTagValue(stepnode, "lookup", "table"); //$NON-NLS-1$ //$NON-NLS-2$

			Node lookup = XMLHandler.getSubNode(stepnode, "lookup"); //$NON-NLS-1$
			nrkeys = XMLHandler.countNodes(lookup, "key"); //$NON-NLS-1$
			nrvalues = XMLHandler.countNodes(lookup, "value"); //$NON-NLS-1$

			allocate(nrkeys, nrvalues);

			for (int i = 0; i < nrkeys; i++) {
				Node knode = XMLHandler.getSubNodeByNr(lookup, "key", i); //$NON-NLS-1$

				keyStream[i] = XMLHandler.getTagValue(knode, "name"); //$NON-NLS-1$
				keyLookup[i] = XMLHandler.getTagValue(knode, "field"); //$NON-NLS-1$
				keyCondition[i] = XMLHandler.getTagValue(knode, "condition"); //$NON-NLS-1$
				if (keyCondition[i] == null)
					keyCondition[i] = "="; //$NON-NLS-1$
				keyStream2[i] = XMLHandler.getTagValue(knode, "name2"); //$NON-NLS-1$
			}

			for (int i = 0; i < nrvalues; i++) {
				Node vnode = XMLHandler.getSubNodeByNr(lookup, "value", i); //$NON-NLS-1$

				updateLookup[i] = XMLHandler.getTagValue(vnode, "name"); //$NON-NLS-1$
				updateStream[i] = XMLHandler.getTagValue(vnode, "rename"); //$NON-NLS-1$
				if (updateStream[i] == null)
					updateStream[i] = updateLookup[i]; // default: the same
														// name!
			}

			web_moveInit();
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"UpdateMeta.Exception.UnableToReadStepInfoFromXML"), e); //$NON-NLS-1$
		}
	}

	public void getFields(RowMetaInterface row, String name)
			throws KettleException {
		if (ignoreFlagField != null && ignoreFlagField.length() > 0) {
			ValueMetaInterface v = new ValueMeta(ignoreFlagField,
					ValueMetaInterface.TYPE_BOOLEAN);
			v.setOrigin(name);

			row.addValueMeta(v);
		}
		if (!Const.isEmpty(this.returnrow)) {
			ValueMetaInterface v = new ValueMeta(returnrow,
					ValueMetaInterface.TYPE_STRING);
			v.setOrigin(name);

			row.addValueMeta(v);
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) { 
//			ConstLog.message("==" + this.getTypeId() + "=初始化" + this.getName());
		}
		return true;
	}

	private ProcessReturn lookupValuesByWeb(RowMetaInterface rowMeta,
			Object[] row) throws KettleException, SQLException {
		DatabaseMeta databaseMeta = getDatabaseMeta();

		Object retrunRow = null;
		Object[] outputRow = row;
		Object[] add;

		// if (!Const.isEmpty(getIgnoreFlagField())) // add flag field!
		// {
		outputRow = new Object[rowMeta.size() + 2];
		for (int i = 0; i < rowMeta.size(); i++) {
			outputRow[i] = row[i];
		}
		// }

		Object[] lookupRow = new Object[lookupParameterRowMeta.size()];
		int lookupIndex = 0;
		for (int i = 0; i < keynrs.length; i++) {
			if (keynrs[i] >= 0) {
				lookupRow[lookupIndex] = row[keynrs[i]];
				lookupIndex++;

			}
			if (keynrs2[i] >= 0) {
				lookupRow[lookupIndex] = row[keynrs2[i]];
				lookupIndex++;
			}
		}
		RowMetaInterface returnRowMeta = null;
		Connection connection = web_db.connect();

		if (!isSkipLookup()) {

			PreparedStatement prepStatementLookup = web_db.prepareSQL(
					connection, databaseMeta.stripCR(selectSql));

			web_db.setValues(lookupParameterRowMeta, lookupRow,
					prepStatementLookup);
			add = web_db.getLookup(prepStatementLookup);
			returnRowMeta = web_db.getReturnRowMeta();
			web_db.closePs(prepStatementLookup);

		} else {
			add = null;
		}
		if (add == null && !isSkipLookup()) {
			if (!isErrorIgnored()) {
				if (getStepMeta().isDoingErrorHandling()) {
					outputRow = null;
					if (stringErrorKeyNotFound == null) {
						stringErrorKeyNotFound = BaseMessages.getString(PKG,
								"Update.Exception.KeyCouldNotFound")
								+ lookupParameterRowMeta.getString(lookupRow);
						stringFieldnames = "";
						for (int i = 0; i < lookupParameterRowMeta.size(); i++) {
							if (i > 0)
								stringFieldnames += ", ";
							stringFieldnames += lookupParameterRowMeta
									.getValueMeta(i).getName();
						}
					}
					return putErrorReturn(rowMeta, row, 1,
							stringErrorKeyNotFound, null, "IS_E001");
				} else {
					throw new KettleException(
							BaseMessages.getString(PKG,
									"Update.Exception.KeyCouldNotFound") + lookupParameterRowMeta.getString(lookupRow)); //$NON-NLS-1$
				}
			} else {
				if (!Const.isEmpty(getIgnoreFlagField())) {
					outputRow[rowMeta.size()] = Boolean.FALSE;
				}
			}
		} else {
			boolean update = false;
			retrunRow = add[lookupReturnRowMeta.size()-1];
			if (isSkipLookup()) {
				update = true;
			} else {
				for (int i = 0; i < valuenrs.length; i++) {
					ValueMetaInterface valueMeta = rowMeta
							.getValueMeta(valuenrs[i]);
					Object rowvalue = row[valuenrs[i]];

					// if (returnRowMeta == null) {
					//
					// }
					ValueMetaInterface returnValueMeta = returnRowMeta
							.getValueMeta(i);
					Object retvalue = add[i];

					if (valueMeta.compare(rowvalue, returnValueMeta, retvalue) != 0) {
						update = true;
					}
				}
			}
			PreparedStatement prepStatementUpdate = null;
			if (update) {
				try {
					Object[] updateRow = new Object[updateParameterRowMeta
							.size()];
					for (int i = 0; i < valuenrs.length; i++) {
						updateRow[i] = row[valuenrs[i]]; // the setters
					}
					for (int i = 0; i < lookupRow.length; i++) {
						updateRow[valuenrs.length + i] = lookupRow[i];
					}

					prepStatementUpdate = web_db.prepareSQL(connection,
							databaseMeta.stripCR(updateSql));

					web_db.setValues(updateParameterRowMeta, updateRow,
							prepStatementUpdate);

					web_db.insertRow(prepStatementUpdate);

				} catch (Exception e) {
					throw new KettleException(e);
				} finally {
					if (web_db != null) {
						web_db.clean(true, connection, prepStatementUpdate);
					}
				}
			}
			if (!Const.isEmpty(getIgnoreFlagField())) // add flag field!
			{
				outputRow[rowMeta.size()] = Boolean.TRUE;
			}
		}

		outputRow[outputRow.length - 1] = retrunRow;

		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(outputRow);
		pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
		pReturn.setNextStream(this.getNextStepName());
		return pReturn;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {
		boolean sendToErrorRow = false;
		String errorMessage = null;

		if (first) {
			// 初始化时候同步下
			synchronized (this) {
				if (first) {
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					schemaTable = getDatabaseMeta()
							.getQuotedSchemaTableCombination(
									environmentSubstitute(getSchemaName()),
									environmentSubstitute(getTableName()));

					ArrayList<Integer> keynrsz = new ArrayList<Integer>(
							getKeyStream().length);
					ArrayList<Integer> keynrs2z = new ArrayList<Integer>(
							getKeyStream().length);

					for (int i = 0; i < getKeyStream().length; i++) {
						int keynr = rowMeta.indexOfValue(getKeyStream()[i]);

						if (keynr < 0
								&& // couldn't find field!
								!"IS NULL".equalsIgnoreCase(getKeyCondition()[i]) && // No field needed! //$NON-NLS-1$
								!"IS NOT NULL".equalsIgnoreCase(getKeyCondition()[i]) // No field needed! //$NON-NLS-1$
						) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"Update.Exception.FieldRequired", getKeyStream()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
						keynrsz.add(keynr);

						// this operator needs two bindings
						if ("= ~NULL".equalsIgnoreCase(getKeyCondition()[i])) {
							keynrsz.add(keynr);
							keynrs2z.add(-1);
						}

						int keynr2 = rowMeta.indexOfValue(getKeyStream2()[i]);
						if (keynr2 < 0
								&& // couldn't find field!
								"BETWEEN".equalsIgnoreCase(getKeyCondition()[i]) // 2 fields needed! //$NON-NLS-1$
						) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"Update.Exception.FieldRequired", getKeyStream2()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
						keynrs2z.add(keynr2);

					}

					keynrs = ArrayUtils.toPrimitive(keynrsz
							.toArray(new Integer[0]));
					keynrs2 = ArrayUtils.toPrimitive(keynrs2z
							.toArray(new Integer[0]));

					valuenrs = new int[getUpdateLookup().length];
					for (int i = 0; i < getUpdateLookup().length; i++) {
						valuenrs[i] = rowMeta
								.indexOfValue(getUpdateStream()[i]);
						if (valuenrs[i] < 0) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"Update.Exception.FieldRequired", getUpdateStream()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					if (isSkipLookup()) {
						lookupParameterRowMeta = new RowMeta();
						for (int i = 0; i < getKeyLookup().length; i++) {
							if ("BETWEEN"
									.equalsIgnoreCase(getKeyCondition()[i])) {
								lookupParameterRowMeta.addValueMeta(rowMeta
										.searchValueMeta(getKeyStream()[i]));
								lookupParameterRowMeta.addValueMeta(rowMeta
										.searchValueMeta(getKeyStream2()[i]));
							} else {
								if ("= ~NULL"
										.equalsIgnoreCase(getKeyCondition()[i])) {
									lookupParameterRowMeta
											.addValueMeta(rowMeta
													.searchValueMeta(getKeyStream()[i]));
									lookupParameterRowMeta.addValueMeta(rowMeta
											.searchValueMeta(getKeyStream()[i])
											.clone());
								} else if (!"IS NULL"
										.equalsIgnoreCase(getKeyCondition()[i])
										&& !"IS NOT NULL"
												.equalsIgnoreCase(getKeyCondition()[i])) {
									lookupParameterRowMeta
											.addValueMeta(rowMeta
													.searchValueMeta(getKeyStream()[i]));
								}

							}
						}
					} else {
						selectSql = setLookupSingle(rowMeta);
					}
					updateSql = prepareUpdateSingle(rowMeta);
					first = false;
				}
			}
		}
		try {
			return lookupValuesByWeb(rowMeta, r);
		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				e.printStackTrace();
				throw new KettleException("UPDATE ERROR", e);
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, r, 1, errorMessage, null,
						"IS_E001");
			}
		}
		return null;
	}

	private String[] getUpdateLookup() {
		return updateLookup;
	}

	private String[] getUpdateStream() {
		return updateStream;
	}

	private int getCommitSize() {
		return commitSize;
	}

	private boolean isUseBatchUpdate() {
		return useBatchUpdate;
	}

	private String getIndex() {
		return index;
	}

	private String getINDEXFLAG() {
		return INDEXFLAG;
	}

	private WEB_Database getWeb_db() {
		return web_db;
	}

	private int[] getValuenrs() {
		return valuenrs;
	}

	private String getStringErrorKeyNotFound() {
		return stringErrorKeyNotFound;
	}

	private String getStringFieldnames() {
		return stringFieldnames;
	}

	private RowMetaInterface getOutputRowMeta() {
		return outputRowMeta;
	}
 

	private RowMetaInterface getLookupParameterRowMeta() {
		return lookupParameterRowMeta;
	}

	private RowMetaInterface getLookupReturnRowMeta() {
		return lookupReturnRowMeta;
	}

	private RowMetaInterface getUpdateParameterRowMeta() {
		return updateParameterRowMeta;
	}

	private boolean isHadInit() {
		return hadInit;
	}

	private String getUpdateSql() {
		return updateSql;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public boolean isSkipLookup() {
		return skipLookup;
	}

	public String getSchemaTable() {
		return schemaTable;
	}

	public String getSelectSql() {
		return selectSql;
	}

	public String setLookupSingle(RowMetaInterface rowMeta)
			throws KettleException {
		lookupParameterRowMeta = new RowMeta();
		lookupReturnRowMeta = new RowMeta();

		DatabaseMeta databaseMeta = getDatabaseMeta();

		String sql = "SELECT ";
		/***
		 * zhengjianbo 2014-04-23 加入强制索引
		 */
		index = environmentSubstitute(index);
		if (!Const.isEmpty(index)) {
			sql = sql + " " + index + " ";
		}

		for (int i = 0; i < getUpdateLookup().length; i++) {
			if (i != 0)
				sql += ", ";
			sql += databaseMeta.quoteField(getUpdateLookup()[i]);
			lookupReturnRowMeta.addValueMeta(rowMeta
					.searchValueMeta(getUpdateStream()[i]));
		}

		if (!Const.isEmpty(this.returnrow)) {
			// 把主键字段加上
			sql += " , ";
			sql += databaseMeta.quoteField(returnrow);

			ValueMetaInterface v2 = new ValueMeta(this.returnrow,
					ValueMetaInterface.TYPE_STRING);
			v2.setOrigin(returnrow);
//			rowMeta.addValueMeta(v2);
			// 内容主键加上
			lookupReturnRowMeta.addValueMeta(v2);
		}

		sql += " FROM " + schemaTable + " WHERE ";

		for (int i = 0; i < getKeyLookup().length; i++) {
			if (i != 0) {
				sql += " AND ";
			}

			sql += " ( ( ";

			sql += databaseMeta.quoteField(getKeyLookup()[i]);
			if ("BETWEEN".equalsIgnoreCase(getKeyCondition()[i])) {
				sql += " BETWEEN ? AND ? ";
				lookupParameterRowMeta.addValueMeta(rowMeta
						.searchValueMeta(getKeyStream()[i]));
				lookupParameterRowMeta.addValueMeta(rowMeta
						.searchValueMeta(getKeyStream2()[i]));
			} else {
				if ("IS NULL".equalsIgnoreCase(getKeyCondition()[i])
						|| "IS NOT NULL".equalsIgnoreCase(getKeyCondition()[i])) {
					sql += " " + getKeyCondition()[i] + " ";
				} else if ("= ~NULL".equalsIgnoreCase(getKeyCondition()[i])) {

					sql += " IS NULL AND ";

					if (databaseMeta.requiresCastToVariousForIsNull()) {
						sql += "CAST(? AS VARCHAR(256)) IS NULL";
					} else {
						sql += "? IS NULL";
					}
					// null check
					lookupParameterRowMeta.addValueMeta(rowMeta
							.searchValueMeta(getKeyStream()[i]));
					sql += " ) OR ( "
							+ databaseMeta.quoteField(getKeyLookup()[i])
							+ " = ?";
					lookupParameterRowMeta.addValueMeta(rowMeta
							.searchValueMeta(getKeyStream()[i]).clone());

				} else {
					sql += " " + getKeyCondition()[i] + " ? ";
					lookupParameterRowMeta.addValueMeta(rowMeta
							.searchValueMeta(getKeyStream()[i]));
				}
			}
			sql += " ) ) ";
		}

		return sql;
	}

	public String prepareUpdateSingle(RowMetaInterface rowMeta)
			throws KettleException {
		DatabaseMeta databaseMeta = getDatabaseMeta();
		updateParameterRowMeta = new RowMeta();

		String sql = " UPDATE ";

		index = environmentSubstitute(index);
		if (!Const.isEmpty(index)) {
			sql = sql + " " + index + " ";
		}
		sql += " " + schemaTable + Const.CR;
		sql += "SET ";

		for (int i = 0; i < getUpdateLookup().length; i++) {
			if (i != 0)
				sql += ",   ";
			sql += databaseMeta.quoteField(getUpdateLookup()[i]);
			sql += " = ?" + Const.CR;
			updateParameterRowMeta.addValueMeta(rowMeta
					.searchValueMeta(getUpdateStream()[i]));
		}

		sql += "WHERE ";

		for (int i = 0; i < getKeyLookup().length; i++) {
			if (i != 0)
				sql += "AND   ";
			sql += " ( ( ";
			sql += databaseMeta.quoteField(getKeyLookup()[i]);
			if ("BETWEEN".equalsIgnoreCase(getKeyCondition()[i])) {
				sql += " BETWEEN ? AND ? ";
				updateParameterRowMeta.addValueMeta(rowMeta
						.searchValueMeta(getKeyStream()[i]));
				updateParameterRowMeta.addValueMeta(rowMeta
						.searchValueMeta(getKeyStream2()[i]));
			} else if ("IS NULL".equalsIgnoreCase(getKeyCondition()[i])
					|| "IS NOT NULL".equalsIgnoreCase(getKeyCondition()[i])) {
				sql += " " + getKeyCondition()[i] + " ";
			} else if ("= ~NULL".equalsIgnoreCase(getKeyCondition()[i])) {

				sql += " IS NULL AND ";

				if (databaseMeta.requiresCastToVariousForIsNull()) {
					sql += "CAST(? AS VARCHAR(256)) IS NULL";
				} else {
					sql += "? IS NULL";
				}
				// null check
				updateParameterRowMeta.addValueMeta(rowMeta
						.searchValueMeta(getKeyStream()[i]));
				sql += " ) OR ( " + databaseMeta.quoteField(getKeyLookup()[i])
						+ " = ?";
				// equality check, cloning so auto-rename because of adding same
				// fieldname does not cause problems
				updateParameterRowMeta.addValueMeta(rowMeta.searchValueMeta(
						getKeyStream()[i]).clone());

			} else {
				sql += " " + getKeyCondition()[i] + " ? ";
				updateParameterRowMeta.addValueMeta(rowMeta
						.searchValueMeta(getKeyStream()[i]));
			}
			sql += " ) ) ";
		}
		return sql;
	}

	public boolean web_moveInit() {
		if (hadInit) {
			return true;
		}
		web_db = new WEB_Database(this, getDatabaseMeta());

		hadInit = true;
		return true;
	}

	public DatabaseMeta getDatabaseMeta() {
		return databaseMeta;
	}

	public String[] getKeyStream() {
		return keyStream;
	}

	public String[] getKeyLookup() {
		return keyLookup;
	}

	public String[] getKeyCondition() {
		return keyCondition;
	}

	public String[] getKeyStream2() {
		return keyStream2;
	}

	public boolean isErrorIgnored() {
		return errorIgnored;
	}

	public String getIgnoreFlagField() {
		return ignoreFlagField;
	}

	public int[] getKeynrs() {
		return keynrs;
	}

	public int[] getKeynrs2() {
		return keynrs2;
	}

	public String getTypeid() {
		return typeid;
	}

	public String environmentSubstitute(String key) {
		return key;
	}
 
	private ProcessReturn lookupBatchValuesByWeb(RowMetaInterface rowMeta,
			Object[] row, int dataIndex, PreparedStatement prepStatementLookup,
			PreparedStatement prepStatementUpdate,Connection batchConn) throws KettleException,
			SQLException {
		DatabaseMeta databaseMeta = getDatabaseMeta();

		Object retrunRow = null;
		Object[] outputRow = row;
		Object[] add;

		outputRow = new Object[rowMeta.size() + 2];
		for (int i = 0; i < rowMeta.size(); i++) {
			outputRow[i] = row[i];
		}

		Object[] lookupRow = new Object[lookupParameterRowMeta.size()];
		int lookupIndex = 0;
		for (int i = 0; i < keynrs.length; i++) {
			if (keynrs[i] >= 0) {
				lookupRow[lookupIndex] = row[keynrs[i]];
				lookupIndex++;

			}
			if (keynrs2[i] >= 0) {
				lookupRow[lookupIndex] = row[keynrs2[i]];
				lookupIndex++;
			}
		}
		RowMetaInterface returnRowMeta = null; 

		if (!isSkipLookup()) { 
			web_db.setValues(lookupParameterRowMeta, lookupRow,
					prepStatementLookup);
			add = web_db.getLookup(prepStatementLookup);
			returnRowMeta = web_db.getReturnRowMeta(); 
		} else {
			add = null;
		}
		if (add == null && !isSkipLookup()) {
			if (!isErrorIgnored()) {
				if (getStepMeta().isDoingErrorHandling()) {
					outputRow = null;
					if (stringErrorKeyNotFound == null) {
						stringErrorKeyNotFound = BaseMessages.getString(PKG,
								"Update.Exception.KeyCouldNotFound")
								+ lookupParameterRowMeta.getString(lookupRow);
						stringFieldnames = "";
						for (int i = 0; i < lookupParameterRowMeta.size(); i++) {
							if (i > 0)
								stringFieldnames += ", ";
							stringFieldnames += lookupParameterRowMeta
									.getValueMeta(i).getName();
						}
					}
					return putErrorReturn(rowMeta, row, 1,
							stringErrorKeyNotFound, null, "IS_E001");
				} else {
					throw new KettleException(
							BaseMessages.getString(PKG,
									"Update.Exception.KeyCouldNotFound") + lookupParameterRowMeta.getString(lookupRow)); //$NON-NLS-1$
				}
			} else {
				if (!Const.isEmpty(getIgnoreFlagField())) {
					outputRow[rowMeta.size()] = Boolean.FALSE;
				}
			}
		} else {
			boolean update = false;
			retrunRow = add[lookupReturnRowMeta.size() - 1];
			if (isSkipLookup()) {
				update = true;
			} else {
				for (int i = 0; i < valuenrs.length; i++) {
					ValueMetaInterface valueMeta = rowMeta
							.getValueMeta(valuenrs[i]);
					Object rowvalue = row[valuenrs[i]];

					ValueMetaInterface returnValueMeta = returnRowMeta
							.getValueMeta(i);
					Object retvalue = add[i];

					if (valueMeta.compare(rowvalue, returnValueMeta, retvalue) != 0) {
						update = true;
					}
				}
			}
			if (update) {
				try {
					Object[] updateRow = new Object[updateParameterRowMeta
							.size()];
					for (int i = 0; i < valuenrs.length; i++) {
						updateRow[i] = row[valuenrs[i]]; // the setters
					}
					for (int i = 0; i < lookupRow.length; i++) {
						updateRow[valuenrs.length + i] = lookupRow[i];
					} 
					web_db.setValues(updateParameterRowMeta, updateRow,
							prepStatementUpdate);
					web_db.insertRow(batchConn,prepStatementUpdate, true,
							this.commitSize, dataIndex);

				} catch (Exception e) {
					throw new KettleException(e);
				} finally {
				}
			}
			if (!Const.isEmpty(getIgnoreFlagField())) {
				outputRow[rowMeta.size()] = Boolean.TRUE;
			}
		}
		outputRow[outputRow.length - 1] = retrunRow;

		ProcessReturn pReturn = new ProcessReturn();
		pReturn.setRow(outputRow);
		pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
		pReturn.setNextStream(this.getNextStepName());
		return pReturn;
	}

	public ProcessReturn processBatchRow(RowMetaInterface rowMeta, Object[] r,
			int dataIndex, Object[] createParams) throws KettleException {
		boolean sendToErrorRow = false;
		String errorMessage = null; 
		try {
			
			PreparedStatement prepStatementLookup=(PreparedStatement) createParams[0];
					PreparedStatement prepStatementUpdate=(PreparedStatement) createParams[1];
					Connection batchConn=(Connection) createParams[2];
			
			return lookupBatchValuesByWeb(rowMeta, r, dataIndex,
					prepStatementLookup, prepStatementUpdate,batchConn);
		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				e.printStackTrace();
				throw new KettleException("UPDATE ERROR", e);
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, r, 1, errorMessage, null,
						"IS_E001");
			}
		}
		return null;
	}

	public Object[] createPre(RowMetaInterface rowMeta, Object[] r) throws KettleException {
		PreparedStatement prepStatementLookup = null;
		PreparedStatement prepStatementUpdate = null; 
		if (first) { 
			synchronized (this) {
				if (first) {
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					schemaTable = getDatabaseMeta()
							.getQuotedSchemaTableCombination(
									environmentSubstitute(getSchemaName()),
									environmentSubstitute(getTableName()));

					ArrayList<Integer> keynrsz = new ArrayList<Integer>(
							getKeyStream().length);
					ArrayList<Integer> keynrs2z = new ArrayList<Integer>(
							getKeyStream().length);

					for (int i = 0; i < getKeyStream().length; i++) {
						int keynr = rowMeta.indexOfValue(getKeyStream()[i]);

						if (keynr < 0
								&& !"IS NULL"
										.equalsIgnoreCase(getKeyCondition()[i])
								&& !"IS NOT NULL"
										.equalsIgnoreCase(getKeyCondition()[i])) {
							throw new KettleException(BaseMessages.getString(
									PKG, "Update.Exception.FieldRequired",
									getKeyStream()[i]));
						}
						keynrsz.add(keynr);

						// this operator needs two bindings
						if ("= ~NULL".equalsIgnoreCase(getKeyCondition()[i])) {
							keynrsz.add(keynr);
							keynrs2z.add(-1);
						}

						int keynr2 = rowMeta.indexOfValue(getKeyStream2()[i]);
						if (keynr2 < 0
								&& // couldn't find field!
								"BETWEEN".equalsIgnoreCase(getKeyCondition()[i]) // 2 fields needed! //$NON-NLS-1$
						) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"Update.Exception.FieldRequired", getKeyStream2()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
						keynrs2z.add(keynr2);

					}

					keynrs = ArrayUtils.toPrimitive(keynrsz
							.toArray(new Integer[0]));
					keynrs2 = ArrayUtils.toPrimitive(keynrs2z
							.toArray(new Integer[0]));

					valuenrs = new int[getUpdateLookup().length];
					for (int i = 0; i < getUpdateLookup().length; i++) {
						valuenrs[i] = rowMeta
								.indexOfValue(getUpdateStream()[i]);
						if (valuenrs[i] < 0) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"Update.Exception.FieldRequired", getUpdateStream()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}

					if (isSkipLookup()) {
						lookupParameterRowMeta = new RowMeta();
						for (int i = 0; i < getKeyLookup().length; i++) {
							if ("BETWEEN"
									.equalsIgnoreCase(getKeyCondition()[i])) {
								lookupParameterRowMeta.addValueMeta(rowMeta
										.searchValueMeta(getKeyStream()[i]));
								lookupParameterRowMeta.addValueMeta(rowMeta
										.searchValueMeta(getKeyStream2()[i]));
							} else {
								if ("= ~NULL"
										.equalsIgnoreCase(getKeyCondition()[i])) {
									lookupParameterRowMeta
											.addValueMeta(rowMeta
													.searchValueMeta(getKeyStream()[i]));
									lookupParameterRowMeta.addValueMeta(rowMeta
											.searchValueMeta(getKeyStream()[i])
											.clone());
								} else if (!"IS NULL"
										.equalsIgnoreCase(getKeyCondition()[i])
										&& !"IS NOT NULL"
												.equalsIgnoreCase(getKeyCondition()[i])) {
									lookupParameterRowMeta
											.addValueMeta(rowMeta
													.searchValueMeta(getKeyStream()[i]));
								}

							}
						}
					} else {
						selectSql = setLookupSingle(rowMeta); 
					}
					updateSql = prepareUpdateSingle(rowMeta);

					first = false;
				}
			}
		}
		//单独执行
		Connection batchConn = web_db.connect();
		if (!Const.isEmpty(selectSql)) {
			prepStatementLookup = web_db.prepareSQL(batchConn,
					databaseMeta.stripCR(selectSql));
		}
		prepStatementUpdate = web_db.prepareSQL(batchConn,
				databaseMeta.stripCR(updateSql));
		return new Object[] { prepStatementLookup,
				prepStatementUpdate, batchConn};
	}
 
	public void clearBatch(Object[] createParams,int dataNums) throws  Exception {
		 
		PreparedStatement prepStatementLookup=(PreparedStatement) createParams[0];
		PreparedStatement prepStatementUpdate=(PreparedStatement) createParams[1];
		Connection batchConn=(Connection) createParams[2]; 
		
		if(dataNums%this.commitSize==0){ 
			web_db.insertRowOver(batchConn, prepStatementUpdate, true, true);
		}else{ 
			web_db.insertRowOver(batchConn, prepStatementUpdate, true, false);
		}
		
		
		web_db.closePs(prepStatementLookup);
		web_db.clean(false, batchConn, prepStatementUpdate);
	}

}

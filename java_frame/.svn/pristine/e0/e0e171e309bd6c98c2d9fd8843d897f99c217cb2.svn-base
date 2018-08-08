package com.ram.kettle.steps.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Node;

import com.ram.kettle.database.DatabaseMeta;
import com.ram.kettle.database.WEB_Database;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class InsertUpdateReturn extends StepMeta implements StepInterface {
	private static Class<?> PKG = InsertUpdateReturn.class;
	private final String typeid = "InsertUpdate";

	public WEB_Database web_db;
	public boolean isInit;
	public int keynrs[];
	public int keynrs2[];
	public int valuenrs[];
	public RowMetaInterface outputRowMeta;
	private boolean isUpdateNull = false;
	public String schemaTable;

	public PreparedStatement prepStatementLookup;

	public PreparedStatement prepStatementLookupEISDEL;

	public PreparedStatement prepStatementUpdate;

	public RowMetaInterface updateParameterRowMeta;
	public String[] updateParameterForColumn;

	public RowMetaInterface lookupParameterRowMeta, lookupReturnRowMeta;
	public String[] lookupParameterForColumn;

	// public RowMetaInterface lookupReturnRowMeta;
	public boolean isRInit = true;

	public RowMetaInterface lookupIsdelRowMeta;

	public RowMetaInterface insertRowMeta;

	private boolean addCallBack;
	public boolean hadInit = false;

	/** what's the lookup schema? */
	private String schemaName;

	/** what's the lookup table? */
	private String tableName;

	/** database connection */
	// private DatabaseMeta databaseMeta;

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

	/** boolean indicating if field needs to be updated */
	private Boolean update[];

	/** zhengjianbo 2013-09-12 是否不验证 但更新 */
	private Boolean needUpdate[];

	/** Commit size for inserts/updates */
	private int commitSize;

	/** Bypass any updates */
	private boolean updateBypassed;

	public String returnrow = null;
	private String RETURNROW = "returnrow";

	private int rIndex = -1;

	/*******
	 * zhengjianbo 2014-04-23* 强制使用索引
	 * *************/
	public String index;
	private String INDEXFLAG = "sindex";

	public InsertUpdateReturn() {
		super();
	}

	public InsertUpdateReturn(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrkeys, int nrvalues, int isKeys) {
		keyStream = new String[nrkeys];
		keyLookup = new String[nrkeys];
		keyCondition = new String[nrkeys];
		keyStream2 = new String[nrkeys];
		updateLookup = new String[nrvalues];
		updateStream = new String[nrvalues];
		update = new Boolean[nrvalues];

		needUpdate = new Boolean[nrvalues];

	}

	public void allocate(int nrkeys, int nrvalues) {
		allocate(nrkeys, nrvalues, 10);
	}

	public Object clone() {
		InsertUpdateReturn retval = (InsertUpdateReturn) super.clone();
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
			retval.update[i] = update[i];
		}

		// if (condition != null) {
		// retval.condition = (Condition) condition.clone();
		// } else {
		// retval.condition = null;
		// }

		return retval;
	}

	DatabaseMeta databaseMeta;

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			String csize;
			int nrkeys, nrvalues;

			index = XMLHandler.getTagValue(stepnode, this.INDEXFLAG);

			this.returnrow = XMLHandler.getTagValue(stepnode, this.RETURNROW); // 返回字段

			String con = XMLHandler.getTagValue(stepnode, "connection");

			// 根据名字找到配置
			databaseMeta = DatabaseMeta.findDatabase(con);

			csize = XMLHandler.getTagValue(stepnode, "commit"); //$NON-NLS-1$
			commitSize = Const.toInt(csize, 0);
			schemaName = XMLHandler.getTagValue(stepnode, "lookup", "schema"); //$NON-NLS-1$ //$NON-NLS-2$
			tableName = XMLHandler.getTagValue(stepnode, "lookup", "table"); //$NON-NLS-1$ //$NON-NLS-2$
			updateBypassed = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "update_bypassed")); //$NON-NLS-1$ //$NON-NLS-2$

			// zhengjianbo

			addCallBack = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "add_callback")); //$NON-NLS-1$ //$NON-NLS-2$

			Node lookup = XMLHandler.getSubNode(stepnode, "lookup"); //$NON-NLS-1$
			nrkeys = XMLHandler.countNodes(lookup, "key"); //$NON-NLS-1$
			nrvalues = XMLHandler.countNodes(lookup, "value"); //$NON-NLS-1$

			this.allocate(nrkeys, nrvalues);

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
				String updateValue = XMLHandler.getTagValue(vnode, "update"); //$NON-NLS-1$
				if (updateValue == null) {
					// default TRUE
					update[i] = Boolean.TRUE;
				} else {
					if (updateValue.equalsIgnoreCase("Y"))
						update[i] = Boolean.TRUE;
					else
						update[i] = Boolean.FALSE;
				}

				// zhengjianbo 2013-09-12 是否不比较但更新
				String needUpdateValue = XMLHandler.getTagValue(vnode,
						"needupdate"); //$NON-NLS-1$
				if (needUpdateValue == null) {
					// default TRUE
					needUpdate[i] = Boolean.TRUE;
				} else {
					if (needUpdateValue.equalsIgnoreCase("Y"))
						needUpdate[i] = Boolean.TRUE;
					else
						needUpdate[i] = Boolean.FALSE;
				}
				// end
			}
			// zhengjianbo 2013-03-15
			// The new situation...
			Node compare = XMLHandler.getSubNode(stepnode, "compare"); //$NON-NLS-1$
			Node condnode = XMLHandler.getSubNode(compare, "condition"); //$NON-NLS-1$
			if (condnode != null) {
				// condition = new Condition(condnode);
			} else // Old style condition: Line1 OR Line2 OR Line3: @deprecated!
			{
				// condition = new Condition();
				//
				//							int nrkeys1   = XMLHandler.countNodes(compare, "key"); //$NON-NLS-1$
				// if (nrkeys1==1)
				// {
				//								Node knode = XMLHandler.getSubNodeByNr(compare, "key", 0); //$NON-NLS-1$
				//
				//								String key         = XMLHandler.getTagValue(knode, "name"); //$NON-NLS-1$
				//								String value       = XMLHandler.getTagValue(knode, "value"); //$NON-NLS-1$
				//								String field       = XMLHandler.getTagValue(knode, "field"); //$NON-NLS-1$
				//								String comparator  = XMLHandler.getTagValue(knode, "condition"); //$NON-NLS-1$
				//
				// condition.setOperator( Condition.OPERATOR_NONE );
				// condition.setLeftValuename(key);
				// condition.setFunction( Condition.getFunction(comparator) );
				// condition.setRightValuename(field);
				//								condition.setRightExact( new ValueMetaAndData("value", value ) ); //$NON-NLS-1$
				// }
				// else
				// {
				// for (int i=0;i<nrkeys1;i++)
				// {
				//									Node knode = XMLHandler.getSubNodeByNr(compare, "key", i); //$NON-NLS-1$
				//
				//									String key         = XMLHandler.getTagValue(knode, "name"); //$NON-NLS-1$
				//									String value       = XMLHandler.getTagValue(knode, "value"); //$NON-NLS-1$
				//									String field       = XMLHandler.getTagValue(knode, "field"); //$NON-NLS-1$
				//									String comparator  = XMLHandler.getTagValue(knode, "condition"); //$NON-NLS-1$
				//
				// Condition subc = new Condition();
				// if (i>0) subc.setOperator( Condition.OPERATOR_OR );
				// else subc.setOperator( Condition.OPERATOR_NONE );
				// subc.setLeftValuename(key);
				// subc.setFunction( Condition.getFunction(comparator) );
				// subc.setRightValuename(field);
				//									subc.setRightExact( new ValueMetaAndData("value", value ) ); //$NON-NLS-1$
				//
				// condition.addCondition(subc);
				// }
				// }
			}

			web_db = new WEB_Database(this, databaseMeta);

		} catch (Exception e) {
			throw new KettleException("Unable to load step info from XML", e);
		}
	}

	public void getFields(RowMetaInterface rowMeta, String origin)
			throws KettleException {

		if (this.isAddCallBack()) {
			ValueMetaInterface v = new ValueMeta(origin + "_INSERT",
					ValueMetaInterface.TYPE_BOOLEAN);
			v.setOrigin(origin);
			rowMeta.addValueMeta(v);

			ValueMetaInterface v1 = new ValueMeta(origin + "_UPDATE",
					ValueMetaInterface.TYPE_BOOLEAN);
			v1.setOrigin(origin);
			rowMeta.addValueMeta(v1);
		}

		if (!Const.isEmpty(this.returnrow)) {
			ValueMetaInterface v2 = new ValueMeta(returnrow,
					ValueMetaInterface.TYPE_STRING);
			v2.setOrigin(origin);
			rowMeta.addValueMeta(v2);
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

	public String insertSql, updateSql, selectSql;

	// WEB多线程 高并发 要改进
	private ProcessReturn lookupValuesByWeb(RowMetaInterface rowMeta,
			Object[] row) throws KettleException {

		boolean isUpdate = false;
		boolean isInsert = false;
		String rowid = null;

		Connection connection = null;

		Object[] lookupRow = new Object[lookupParameterRowMeta.size()];
		int lookupIndex = 0;
		int len = keynrs.length;
		for (int i = 0; i < len; i++) {
			if (keynrs[i] >= 0) {
				lookupRow[lookupIndex] = row[keynrs[i]];
				lookupIndex++;
			}
			if (keynrs2[i] >= 0) {
				lookupRow[lookupIndex] = row[keynrs2[i]];
				lookupIndex++;
			}
		}
		// data.db.clean(true, connection, pstmt,rs);
		PreparedStatement prepStatementLookup = null;
		PreparedStatement prepStatementInsert = null;
		PreparedStatement prepStatementUpdate = null;
		try {
			connection = web_db.connect();

			DatabaseMeta databaseMeta = getDatabaseMeta();
			prepStatementLookup = web_db.prepareSQL(connection,
					databaseMeta.stripCR(selectSql));

			web_db.setValues(lookupParameterRowMeta, lookupRow,
					prepStatementLookup);
			Object[] add = web_db.getLookup(prepStatementLookup);
			web_db.closePs(prepStatementLookup);
			if (add == null) {
				Object[] insertRow = new Object[valuenrs.length];
				for (int i = 0; i < valuenrs.length; i++) {
					insertRow[i] = row[valuenrs[i]];
				}

				prepStatementInsert = web_db.prepareSQL(connection,
						databaseMeta.stripCR(insertSql), true);

				web_db.setValues(insertRowMeta, insertRow, prepStatementInsert);
				web_db.insertRow(prepStatementInsert);

				// RowMetaAndData keyMetaData = web_db
				// .getGeneratedKeys(prepStatementInsert);
				web_db.closePs(prepStatementInsert);
				isInsert = true;
				// rowid = keyMetaData.getString(0, null);
				// 如果是rowid,插入的唯一值就是eid
				if (rIndex > -1) {
					rowid = row[rIndex] + "";
				}
			} else {
				// 查找到目标字段
				if (!Const.isEmpty(this.returnrow)) {
					// 返回主键
					int freturn = web_db.getReturnRowMeta().indexOfValue(
							returnrow);
					if (freturn > -1) {
						rowid = add[freturn] + "";
					}
				}
				if (!isUpdateBypassed()) {
					boolean update = false;
					for (int i = 0; i < valuenrs.length; i++) {
						if (getUpdate()[i].booleanValue()) {
							ValueMetaInterface valueMeta = rowMeta
									.getValueMeta(valuenrs[i]);
							ValueMetaInterface retMeta = web_db
									.getReturnRowMeta().getValueMeta(i);

							Object rowvalue = row[valuenrs[i]];
							Object retvalue = add[i];
							if (valueMeta.compare(rowvalue, retMeta, retvalue) != 0) {

								update = true;
							}
						}
					}
					if (update) {
						Object[] updateRow = new Object[updateParameterRowMeta
								.size()];
						int j = 0;
						for (int i = 0; i < valuenrs.length; i++) {
							if (getUpdate()[i].booleanValue()) {
								updateRow[j] = row[valuenrs[i]]; // the setters
								j++;
							}
						}
						for (int i = 0; i < lookupRow.length; i++) {
							updateRow[j + i] = lookupRow[i];
						}
						prepStatementUpdate = web_db.prepareSQL(connection,
								databaseMeta.stripCR(updateSql), true);

						web_db.setValues(updateParameterRowMeta, updateRow,
								prepStatementUpdate);
						web_db.insertRow(prepStatementUpdate);

						isUpdate = true;
					}
				} else {
				}
			}
			if (isAddCallBack()) {
				row = RowDataUtil.addRowData(row, rowMeta.size(), new Object[] {
						isInsert, isUpdate, rowid });
			}
			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(row);
			pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
			pReturn.setNextStream(getNextStepName());
			return pReturn;
		} catch (Exception e) {
			throw new KettleException(e);
		} finally {
			web_db.clean(true, connection, prepStatementUpdate);
		}

	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {

		if (r == null) {
			throw new KettleException("DATA ERROR");
		}
		boolean sendToErrorRow = false;
		String errorMessage = null;
		if (first) {
			// 初始化时候同步下
			synchronized (this) {
				if (first) {

					if (!Const.isEmpty(this.returnrow)) {
						rIndex = rowMeta.indexOfValue(returnrow);
					}

					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					String[] updateLookup = getUpdateLookup();
					if (updateLookup == null || updateLookup.length == 0) {
						isUpdateNull = true;
						RowMetaInterface postMeta = rowMeta.clone();
						updateLookup = postMeta.getFieldNames();

						setUpdateLookup(updateLookup);
						setUpdateStream(updateLookup);

						Boolean[] updateboolean = new Boolean[updateLookup.length];
						for (int i = 0; i < updateboolean.length; i++) {
							updateboolean[i] = true;
						}
						setUpdate(updateboolean);
					}

					schemaTable = getDatabaseMeta()
							.getQuotedSchemaTableCombination((getSchemaName()),
									(getTableName()));

					ArrayList<Integer> keynrsX = new ArrayList<Integer>(
							getKeyStream().length);
					ArrayList<Integer> keynrs2X = new ArrayList<Integer>(
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
													"InsertUpdate.Exception.FieldRequired", getKeyStream()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
						keynrsX.add(keynr);

						// this operator needs two bindings
						if ("= ~NULL".equalsIgnoreCase(getKeyCondition()[i])) {
							keynrsX.add(keynr);
							keynrs2X.add(-1);
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
													"InsertUpdate.Exception.FieldRequired", getKeyStream2()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
						keynrs2X.add(keynr2);

					}

					keynrs = ArrayUtils.toPrimitive(keynrsX
							.toArray(new Integer[0]));
					keynrs2 = ArrayUtils.toPrimitive(keynrs2X
							.toArray(new Integer[0]));

					valuenrs = new int[getUpdateLookup().length];
					for (int i = 0; i < getUpdateLookup().length; i++) {
						valuenrs[i] = rowMeta
								.indexOfValue(getUpdateStream()[i]);
						if (valuenrs[i] < 0) // couldn't find field!
						{
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"InsertUpdate.Exception.FieldRequired", getUpdateStream()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}

					selectSql = setLookupSingle(rowMeta);

					insertRowMeta = new RowMeta();

					for (int i = 0; i < getUpdateLookup().length; i++) {
						ValueMetaInterface insValue = insertRowMeta
								.searchValueMeta(getUpdateLookup()[i]);
						if (insValue == null) // Don't add twice!
						{
							// we already checked that this value exists so it's
							// probably safe to ignore lookup failure...
							ValueMetaInterface insertValue = rowMeta
									.searchValueMeta(getUpdateStream()[i])
									.clone();
							insertValue.setName(getUpdateLookup()[i]);
							insertRowMeta.addValueMeta(insertValue);
						} else {
							throw new KettleException(
									"The same column can't be inserted into the target row twice: "
											+ insValue.getName());
						}
					}

					insertSql = web_db.getInsertStatement((getSchemaName()),
							(getTableName()), insertRowMeta);

					if (!isUpdateBypassed()) {
						List<String> updateColumns = new ArrayList<String>();
						for (int i = 0; i < getUpdate().length; i++) {
							if (getUpdate()[i].booleanValue()) {
								updateColumns.add(getUpdateLookup()[i]);
							}
						}
						updateSql = prepareUpdateSingle(rowMeta);
					}
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

	public String getSchemaName() {
		return schemaName;
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

		if (!Const.isEmpty(index)) {
			sql = sql + " " + index + " ";
		}

		for (int i = 0; i < getUpdateLookup().length; i++) {
			if (i != 0)
				sql += ", ";
			sql += databaseMeta.quoteField(getUpdateLookup()[i]);
			lookupReturnRowMeta.addValueMeta(rowMeta.searchValueMeta(
					getUpdateStream()[i]).clone());
		}

		sql += " FROM " + schemaTable + " WHERE ";

		for (int i = 0; i < getKeyLookup().length; i++) {
			if (i != 0) {
				sql += " AND ";
			}

			sql += " ( ( ";

			sql += databaseMeta.quoteField(getKeyLookup()[i]);
			if ("BETWEEN".equalsIgnoreCase(getKeyCondition()[i])) {
				// Zhangjinwei 2013-05-22
				// 为让Timestamp能用到Oracle的日期Date类型的索引，增加日期方法 to_date
				ValueMetaInterface v = rowMeta
						.searchValueMeta(getKeyStream()[i]);
				sql += " BETWEEN " + getDateSql(v) + " AND " + getDateSql(v);
				// Zhangjinwei 2013-05-22
				// 为让Timestamp能用到Oracle的日期Date类型的索引，增加日期方法 to_date
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
						sql += " CAST(? AS VARCHAR(256)) IS NULL ";
					} else {
						sql += " ? IS NULL ";
					}
					// null check
					// Zhangjinwei 2013-05-22
					ValueMetaInterface v = rowMeta
							.searchValueMeta(getKeyStream()[i]);
					lookupParameterRowMeta.addValueMeta(v);
					sql += " ) OR ( "
							+ databaseMeta.quoteField(getKeyLookup()[i])
							+ " = " + getDateSql(v);
					// equality check, cloning so auto-rename because of adding
					// same fieldname does not cause problems
					lookupParameterRowMeta.addValueMeta(rowMeta
							.searchValueMeta(getKeyStream()[i]).clone());

				} else {// Zhangjinwei 2013-05-22
					// 为让Timestamp能用到Oracle的日期Date类型的索引，增加日期方法 to_date
					ValueMetaInterface v = rowMeta
							.searchValueMeta(getKeyStream()[i]);
					sql += " " + getKeyCondition()[i] + getDateSql(v);
					lookupParameterRowMeta.addValueMeta(v);
					// Zhangjinwei 2013-05-22
					// 为让Timestamp能用到Oracle的日期Date类型的索引，增加日期方法 to_date

				}
			}
			sql += " ) ) ";
		}

		return sql;
	}

	public String[] getKeyLookup() {
		return keyLookup;
	}

	public String[] getUpdateLookup() {
		return updateLookup;
	}

	public String[] getKeyStream() {
		return keyStream;
	}

	// Lookup certain fields in a table
	public String prepareUpdateSingle(RowMetaInterface rowMeta)
			throws KettleException {
		DatabaseMeta databaseMeta = getDatabaseMeta();
		updateParameterRowMeta = new RowMeta();

		String sql = " UPDATE ";
		/**
		 * zhengjianbo:2014-04-23 加入强制索引
		 */

		if (!Const.isEmpty(index)) {
			sql = sql + " " + index + " ";
		}
		sql += " " + schemaTable + Const.CR;
		sql += "SET ";

		boolean comma = false;

		for (int i = 0; i < getUpdateLookup().length; i++) {
			if (getUpdate()[i].booleanValue()) {
				if (comma)
					sql += ",   ";
				else
					comma = true;

				sql += databaseMeta.quoteField(getUpdateLookup()[i]);
				sql += " = ?" + Const.CR;
				updateParameterRowMeta.addValueMeta(rowMeta.searchValueMeta(
						getUpdateStream()[i]).clone());
			}
		}

		sql += "WHERE ";

		for (int i = 0; i < getKeyLookup().length; i++) {
			if (i != 0)
				sql += "AND   ";
			sql += " ( ( ";
			sql += databaseMeta.quoteField(getKeyLookup()[i]);
			if ("BETWEEN".equalsIgnoreCase(getKeyCondition()[i])) {
				// Zhangjinwei 2013-05-22
				// 为让Timestamp能用到Oracle的日期Date类型的索引，增加日期方法 to_date
				ValueMetaInterface v = rowMeta
						.searchValueMeta(getKeyStream()[i]);
				sql += " BETWEEN " + getDateSql(v) + " AND " + getDateSql(v);
				// Zhangjinwei 2013-05-22
				// 为让Timestamp能用到Oracle的日期Date类型的索引，增加日期方法 to_date

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
				// Zhangjinwei 2013-05-22
				ValueMetaInterface v = rowMeta
						.searchValueMeta(getKeyStream()[i]);
				updateParameterRowMeta.addValueMeta(v);
				sql += " ) OR ( " + databaseMeta.quoteField(getKeyLookup()[i])
						+ " = " + getDateSql(v);
				updateParameterRowMeta.addValueMeta(rowMeta.searchValueMeta(
						getKeyStream()[i]).clone());

			} else {
				ValueMetaInterface v = rowMeta.searchValueMeta(
						getKeyStream()[i]).clone();
				sql += " " + getKeyCondition()[i] + getDateSql(v);
				updateParameterRowMeta.addValueMeta(v);

			}
			sql += " ) ) ";
		}
		return sql;
	}

	public String[] getUpdateStream() {
		return updateStream;
	}

	/**
	 * 增加方法判断是否日期类型，把timestamp用TO_DATE转换，引用索引 使用ORACLE mysql时不能用
	 * 
	 * @param v
	 * @return
	 */
	private String getDateSql(ValueMetaInterface v) {
		return " ? ";
	}

	public DatabaseMeta getDatabaseMeta() {
		return databaseMeta;
	}

	public boolean isUpdateBypassed() {
		return updateBypassed;
	}

	public boolean isAddCallBack() {
		return addCallBack;
	}

	public Boolean[] getUpdate() {
		return update;
	}

	public void setUpdateLookup(String[] updateLookup) {
		this.updateLookup = updateLookup;
	}

	/**
	 * @param updateStream
	 *            The updateStream to set.
	 */
	public void setUpdateStream(String[] updateStream) {
		this.updateStream = updateStream;
	}

	public void setUpdate(Boolean[] update) {
		this.update = update;
	}

	public String getTableName() {
		return tableName;
	}

	public String[] getKeyCondition() {
		return keyCondition;
	}

	public String[] getKeyStream2() {
		return keyStream2;
	}

	// ----------------------------WEB结束-------------------------------

}

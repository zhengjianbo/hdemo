package com.ram.kettle.steps.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.ram.kettle.cache.DataCacheApplication;
import com.ram.kettle.database.DatabaseMeta;
import com.ram.kettle.database.WEB_Database;
import com.ram.kettle.database.impl.MySqlDatabaseMeta;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowChange;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;
import com.ram.server.util.BaseLog;

public class DynamicSQLRowList extends StepMeta implements StepInterface {
	private static Class<?> PKG = DynamicSQLRowList.class;
	private final String typeid = "dynamicSQLROWLIST";

	/** database connection */
	// private DatabaseMeta databaseMeta;

	/** SQL Statement */
	private String sql;

	private String sqlfieldname;

	/** Number of rows to return (0=ALL) */
	private int rowLimit;

	/**
	 * false: don't return rows where nothing is found true: at least return one
	 * source row, the rest is NULL
	 */
	private boolean outerJoin;

	private boolean replacevars;

	public boolean queryonlyonchange;

	private int cacheTime;

	private boolean singleVars;
	private String outResultColumn;

	private boolean rowSingleVars;

	// WEB_Database web_db;

	RowMetaInterface outputRowMeta;
	RowMetaInterface lookupRowMeta;
	public boolean hadInit = false;
	public int keynrs[];
	public Object[] notfound;
	public boolean isCanceled;

	public int indexOfSQLField;
	public int dbField;

	public String con = null;
	public boolean skipPreviousRow;

	public String previousSQL;

	public ArrayList<Object[]> previousrowbuffer;

	public DynamicSQLRowList() {
		super();
	}

	public DynamicSQLRowList(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrparam) {
		notfound = null;
		indexOfSQLField = -1;
		dbField = -1;
		skipPreviousRow = false;
		previousSQL = null;
		previousrowbuffer = new ArrayList<Object[]>();
	}

	public Object clone() {
		DynamicSQLRowList retval = (DynamicSQLRowList) super.clone();

		return retval;
	}

	public void setDefault() {
		rowLimit = 0;
		cacheTime = 5;
		sql = ""; //$NON-NLS-1$
		outerJoin = false;
		replacevars = false;
		sqlfieldname = null;
		queryonlyonchange = false;
		rowSingleVars = false;
		notfound = null;
		indexOfSQLField = -1;
		skipPreviousRow = false;
		previousSQL = null;
		singleVars = false;
		rowSingleVars = false;
		previousrowbuffer = new ArrayList<Object[]>();
		allocate(0);
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			setDefault();
			con = XMLHandler.getTagValue(stepnode, "connection"); //$NON-NLS-1$

			sql = XMLHandler.getTagValue(stepnode, "sql"); //$NON-NLS-1$
			outerJoin = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "outer_join")); //$NON-NLS-1$ //$NON-NLS-2$
			replacevars = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode,
					"replace_vars"));
			queryonlyonchange = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
					stepnode, "query_only_on_change"));

			this.cacheTime = Const.toInt(
					XMLHandler.getTagValue(stepnode, "cacheTime"), 5); //$NON-NLS-1$

			rowLimit = Const.toInt(
					XMLHandler.getTagValue(stepnode, "rowlimit"), 0); //$NON-NLS-1$
			sqlfieldname = XMLHandler.getTagValue(stepnode, "sql_fieldname"); //$NON-NLS-1$

			outResultColumn = XMLHandler.getTagValue(stepnode,
					"outResultColumn"); //$NON-NLS-1$ 
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"DatabaseJoinMeta.Exception.UnableToLoadStepInfo"), e); //$NON-NLS-1$
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

	public void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}

	public boolean isRowSingleVars() {
		return rowSingleVars;
	}

	/**
	 * 
	 * 建立key
	 * @throws KettleException 
	 * 
	 */
	public String getKey(RowMetaInterface rowMeta,Object[] rowData) throws KettleException {
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(this.getParentTransMeta().getName());
		keyBuffer.append("_");
		keyBuffer.append(this.getStepname());
		keyBuffer.append("_");

		StringBuffer xkeyBuffer = new StringBuffer();
		if (!Const.isEmpty(rowData)) {
			int len = rowData.length;
			for (int i = 0; i < len; i++) {
				xkeyBuffer.append(rowData[i]).append("_");
			}
		}
		xkeyBuffer.append("_");
		xkeyBuffer.append(toString());//静态配置
		
		
		//动态配置
		xkeyBuffer.append("_");
		String sql = rowMeta.getString(rowData, indexOfSQLField);
		xkeyBuffer.append(sql);
		
		xkeyBuffer.append("_");
		String db = rowMeta.getString(rowData, dbField);
		xkeyBuffer.append(db);
		
		String md5 = Const.getMd5(xkeyBuffer.toString());

		// 加上配置
		String key = keyBuffer.append(md5).toString();
		// md5 序列 
		return key;
	}

	@Override
	public String toString() {
		return "DynamicSQLRowList [typeid=" + typeid + ", sql=" + sql
				+ ", sqlfieldname=" + sqlfieldname + ", rowLimit=" + rowLimit
				+ ", outerJoin=" + outerJoin + ", replacevars=" + replacevars
				+ ", queryonlyonchange=" + queryonlyonchange + ", cacheTime="
				+ cacheTime + ", singleVars=" + singleVars
				+ ", outResultColumn=" + outResultColumn + ", rowSingleVars="
				+ rowSingleVars + ", hadInit=" + hadInit + ", keynrs="
				+ Arrays.toString(keynrs) + ", notfound="
				+ Arrays.toString(notfound) + ", isCanceled=" + isCanceled
				+ ", indexOfSQLField=" + indexOfSQLField + ", dbField="
				+ dbField + ", con=" + con + ", skipPreviousRow="
				+ skipPreviousRow + ", previousSQL=" + previousSQL
				+ ", previousrowbuffer=" + previousrowbuffer + "]";
	}

	private DataCacheApplication cacheInstance = DataCacheApplication.getInstance();

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta,
			Object[] rowData) throws KettleException {

		boolean sendToErrorRow = false;
		String errorMessage = null;

		if (rowData == null) {
			throw new KettleException("DATA ERROR");
		}

		if (first) {
			synchronized (this) {
				if (first) {
					if (Const.isEmpty(this.sqlfieldname))
						throw new KettleException(BaseMessages.getString(PKG,
								"DynamicSQLRow.Exception.SQLFieldNameEmpty"));

					if (indexOfSQLField < 0) {
						indexOfSQLField = rowMeta
								.indexOfValue(this.sqlfieldname);
						if (indexOfSQLField < 0) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"DynamicSQLRow.Exception.FieldNotFound", sqlfieldname)); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					if (dbField < 0) {
						dbField = rowMeta
								.indexOfValue(this.con);
						if (dbField < 0) {
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"DynamicSQLRow.Exception.FieldNotFound", con)); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					first = false;
				}
			}
		}

		// 建立缓存索引
		// 查询缓存
		String key = null;

		if (this.cacheTime > 0) {
			try {
				key = getKey(rowMeta,rowData);
				Object cacheDataRowOut = cacheInstance.get(key);
				if (cacheDataRowOut != null) {
					ProcessReturn cacheReturn = new ProcessReturn();
					cacheReturn.setRow((Object[]) (cacheDataRowOut));
					cacheReturn.setRowMeta(RowMeta.clone(outputRowMeta));
					cacheReturn.setNextStream(getNextStepName());
					return cacheReturn;
				}
			} catch (Exception e) {
				// BaseLog.error(e);// e.printStackTrace();
				// 没有获取到数据 --可能数据超时 或者没有
			}
		}
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		String db = rowMeta.getString(rowData, dbField);
		DatabaseMeta databaseMeta = DatabaseMeta.findDatabase(db);
		
		if (databaseMeta == null) {
			throw new KettleException(BaseMessages.getString(PKG,
					"DatabaseJoin.Init.ConnectionMissing", getStepname()));
		}
		WEB_Database web_db = new WEB_Database(this, databaseMeta);

		try {
			// web_moveInit(); //动态化 每次请求单独进行读取

			connection = web_db.connect();
			String sql = rowMeta.getString(rowData, indexOfSQLField);

			pstmt = web_db.prepareSQL(connection, sql);
			rs = web_db.openQuery(pstmt, null, null);

			RowMetaInterface addMeta = web_db
					.getRowInfo(
							rs.getMetaData(),
							databaseMeta.getDatabaseInterface() instanceof MySqlDatabaseMeta,
							false);

			Object[] add = web_db.getRow(rs);

			int counter = 0;
			int addSize = addMeta.size();
			int newIndex = rowMeta.size();

			Object[] dataRowOut = RowDataUtil.resizeArray(rowData,
					outputRowMeta.size(), 0);

			List<Object> dataList = new ArrayList<Object>();

			String[] columnMetaNames = (String[]) RowChange
					.changeToReturnMeta(addMeta);
			Map<Object, Object> mapDataRow = new HashMap<Object, Object>();

			// 多行记录
			while (add != null
					&& (getRowLimit() == 0 || counter < getRowLimit())) {
				counter++;
				Object[] dataRow = new Object[addSize];
				for (int i = 0; i < addSize; i++) {
					dataRow[i] = add[i];
				}

				Map<Object, Object> mapData = new HashMap<Object, Object>();
				int columnIndex = 0;
				for (String columnMetaName : columnMetaNames) {
					mapData.put(columnMetaName, dataRow[columnIndex]);
					columnIndex++;
				}
				mapDataRow = mapData;
				dataList.add(mapData);

				if (getRowLimit() == 0 || counter < getRowLimit()) {
					add = web_db.getRow(rs);
				}
			}

			dataRowOut[newIndex++] = dataList;

			try {
				if (this.cacheTime > 0) {
					cacheInstance.put(key, dataRowOut, cacheTime);// 放入缓存
																	// 需要copy
																	// 不然内容公用
																	// 数据冲突
				}
			} catch (Exception e) {
				BaseLog.error(e);
			}

			ProcessReturn pReturn = new ProcessReturn();
			pReturn.setRow(dataRowOut);
			pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
			pReturn.setNextStream(getNextStepName());
			return pReturn;

		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException(BaseMessages.getString(PKG,
						"DATABASEJOIN ERROR"), e);
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, rowData, 1, errorMessage, null,
						"IS_E001");
			}
		} finally {
			web_db.clean(true, connection, pstmt, rs);
		}
		throw new KettleException("DynamicSQLROW ERROR");
	}

	public void getFields(RowMetaInterface row, String name)
			throws KettleException {

		ValueMetaInterface addColumn = new ValueMeta(outResultColumn,
				ValueMetaInterface.TYPE_SERIALIZABLE);
		addColumn.setOrigin(name);
		row.addValueMeta(addColumn);
		return;

	}

	public int getRowLimit() {
		return rowLimit;
	}

	public boolean isSingleVars() {
		return singleVars;
	}
}

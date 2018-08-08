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
import com.ram.kettle.log.ConstLog;
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

public class Databaselist extends StepMeta implements StepInterface {
	private static Class<?> PKG = Databaselist.class;
	private final String typeid = "databasejoinlist";

	/** database connection */
	private DatabaseMeta databaseMeta;

	/** SQL Statement */
	private String sql;

	private int cacheTime = 5;// 数据查询缓存时间 如果五秒内频繁查询 可以使用缓存这样
								// 一定时间内无论怎么查询都是只查询数据库一次

	private int rowLimit;

	private boolean needCommit; // 执行查询时是否需要进行commit 主要是mysql 调用获取SEQ时
	/**
	 * false: don't return rows where nothing is found true: at least return one
	 * source row, the rest is NULL
	 */
	private boolean outerJoin;

	/** Fields to use as parameters (fill in the ? markers) */
	private String parameterField[];

	/** Type of the paramenters */
	private int parameterType[];

	private boolean replacevars;

	private boolean singleVars;

	private boolean rowSingleVars;

	private String outResultColumn;

	WEB_Database web_db;

	RowMetaInterface outputRowMeta;
	RowMetaInterface lookupRowMeta;
	public boolean hadInit = false;
	public int keynrs[];
	public Object[] notfound;
	public boolean isCanceled;

	public Databaselist() {
		super();
	}

	public Databaselist(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrparam) {
		parameterField = new String[nrparam];
		parameterType = new int[nrparam];
	}

	public Object clone() {
		Databaselist retval = (Databaselist) super.clone();

		int nrparam = parameterField.length;

		retval.allocate(nrparam);

		for (int i = 0; i < nrparam; i++) {
			retval.parameterField[i] = parameterField[i];
			retval.parameterType[i] = parameterType[i];
		}

		return retval;
	}

	public void setDefault() {
		rowSingleVars = false;
		singleVars = false;
		outResultColumn = "";

		databaseMeta = null;
		rowLimit = 0;
		sql = ""; //$NON-NLS-1$
		outerJoin = false;
		parameterField = null;
		parameterType = null;
		needCommit = false;
		replacevars = false;

		int nrparam = 0;

		allocate(nrparam);

		for (int i = 0; i < nrparam; i++) {
			parameterField[i] = "param" + i; //$NON-NLS-1$
			parameterType[i] = ValueMetaInterface.TYPE_NUMBER;
		}
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
			String con = XMLHandler.getTagValue(stepnode, "connection"); //$NON-NLS-1$
			 
			databaseMeta = DatabaseMeta.findDatabase(con);
			 
			
			sql = XMLHandler.getTagValue(stepnode, "sql"); //$NON-NLS-1$
			outerJoin = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "outer_join")); //$NON-NLS-1$ //$NON-NLS-2$
			needCommit = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "need_commit")); //$NON-NLS-1$ //$NON-NLS-2$

			replacevars = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode,
					"replace_vars"));
			rowLimit = Const.toInt(
					XMLHandler.getTagValue(stepnode, "rowlimit"), 0); //$NON-NLS-1$
			cacheTime = Const.toInt(
					XMLHandler.getTagValue(stepnode, "cacheTime"), cacheTime); // 缓存时间

			singleVars = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode,
					"singleVars"));

			rowSingleVars = "Y".equalsIgnoreCase(XMLHandler.getTagValue(
					stepnode, "rowSingleVars"));

			outResultColumn = XMLHandler.getTagValue(stepnode,
					"outResultColumn"); //$NON-NLS-1$

			Node param = XMLHandler.getSubNode(stepnode, "parameter"); //$NON-NLS-1$
			int nrparam = XMLHandler.countNodes(param, "field"); //$NON-NLS-1$

			allocate(nrparam);

			for (int i = 0; i < nrparam; i++) {
				Node pnode = XMLHandler.getSubNodeByNr(param, "field", i); //$NON-NLS-1$
				parameterField[i] = XMLHandler.getTagValue(pnode, "name"); //$NON-NLS-1$
				String ptype = XMLHandler.getTagValue(pnode, "type"); //$NON-NLS-1$
				parameterType[i] = ValueMeta.getType(ptype);
			}
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

	/**
	 * 
	 * 建立key
	 * 
	 */
	public String getKey(Object[] rowData) {
		//转换名+插件名 
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(this.getParentTransMeta().getName());
		keyBuffer.append("_");
		keyBuffer.append(this.getStepname());
		keyBuffer.append("_");

		//查询条件+插件配置 
		StringBuffer xkeyBuffer = new StringBuffer();
		if (!Const.isEmpty(rowData)) {
			int len = rowData.length;
			for (int i = 0; i < len; i++) {
				xkeyBuffer.append(rowData[i]).append("_");
			}
		}
		xkeyBuffer.append("_");
		//静态配置
		xkeyBuffer.append(toString());
		String md5 = Const.getMd5(xkeyBuffer.toString());

		// 加上配置
		String key = keyBuffer.append(md5).toString();
		// md5 序列

		return key;
	}

	 
	@Override
	public String toString() {
		return "Databaselist [typeid=" + typeid + ", databaseMeta="
				+ databaseMeta + ", sql=" + sql + ", cacheTime=" + cacheTime
				+ ", rowLimit=" + rowLimit + ", needCommit=" + needCommit
				+ ", outerJoin=" + outerJoin + ", parameterField="
				+ Arrays.toString(parameterField) + ", parameterType="
				+ Arrays.toString(parameterType) + ", replacevars="
				+ replacevars + ", singleVars=" + singleVars
				+ ", rowSingleVars=" + rowSingleVars + ", outResultColumn="
				+ outResultColumn + ", web_db=" + web_db + ", hadInit="
				+ hadInit + ", keynrs=" + Arrays.toString(keynrs)
				+ ", notfound=" + Arrays.toString(notfound) + ", isCanceled="
				+ isCanceled + "]";
	}


	private DataCacheApplication cacheInstance = DataCacheApplication.getInstance();

	private ProcessReturn lookupValuesByWeb(RowMetaInterface rowMeta,
			Object[] rowData) throws KettleException {

		Object[] lookupRowData = new Object[lookupRowMeta.size()];
		for (int i = 0; i < keynrs.length; i++) {
			lookupRowData[i] = rowData[keynrs[i]];
		}

		// 建立缓存索引
		// 查询缓存
		String key = null;
		// 缓存失败 直接处理
		// 只有一条数据 则输出正常数据 //多条数据时放入到一个额外的字段中用于web显示

		if (this.cacheTime > 0) {
			try {
				key = getKey(lookupRowData);
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

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = web_db.connect();

			pstmt = web_db.prepareSQL(connection, sql);
			rs = web_db.openQuery(pstmt, lookupRowMeta, lookupRowData);
			RowMetaInterface addMeta = web_db
					.getRowInfo(
							rs.getMetaData(),
							databaseMeta.getDatabaseInterface() instanceof MySqlDatabaseMeta,
							false);

			Object[] add = web_db.getRow(rs);

			int counter = 0;
			int addSize = addMeta.size();
			int newIndex = rowMeta.size();

			if (isSingleVars()) {
				// 只是单条记录
				Object[] dataRow = RowDataUtil.resizeArray(rowData,
						outputRowMeta.size(), 0);
				if (Const.isEmpty(add)) {
					dataRow[newIndex++] = null;
				} else {
					for (int i = 0; i < addSize; i++) {
						dataRow[newIndex++] = add[i];
					}
				}

				try {
					if (this.cacheTime > 0) {
						cacheInstance.put(key, dataRow, cacheTime);// 放入缓存
					}
				} catch (Exception e) {
					BaseLog.error(e);
				}
				ProcessReturn pReturn = new ProcessReturn();
				pReturn.setRow(dataRow);
				pReturn.setRowMeta(RowMeta.clone(outputRowMeta));
				pReturn.setNextStream(getNextStepName());
				return pReturn;
			} else {

				Object[] dataRowOut = RowDataUtil.resizeArray(rowData,
						outputRowMeta.size(), 0);

				List<Object> dataList = new ArrayList<Object>();

				String[] columnMetaNames = (String[]) RowChange
						.changeToReturnMeta(addMeta);
				Map<Object, Object> mapDataRow = new HashMap<Object, Object>();

				if (isRowSingleVars()) {
					setRowLimit(1);
				}

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

				if (isRowSingleVars()) {
					dataRowOut[newIndex++] = mapDataRow;
				} else {
					dataRowOut[newIndex++] = dataList;
				}
				// 只有一条数据 则输出正常数据 //多条数据时放入到一个额外的字段中用于web显示
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
			}
		} catch (Exception e) {
			throw new KettleException(e);
		} finally {
			web_db.clean(true, connection, pstmt, rs);
		}
	}

	public void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}

	public boolean isRowSingleVars() {
		return rowSingleVars;
	}

	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {

		if (r == null) {
			throw new KettleException("DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {
					web_moveInit();
					outputRowMeta = rowMeta.clone();
					getFields(outputRowMeta, getStepname());

					lookupRowMeta = new RowMeta();

					keynrs = new int[getParameterField().length];

					for (int i = 0; i < getParameterField().length; i++) {
						keynrs[i] = rowMeta
								.indexOfValue(getParameterField()[i]);
						if (keynrs[i] < 0) {
							ConstLog.message("发生错误插件:" + this.getStepname());
							throw new KettleException(
									BaseMessages
											.getString(
													PKG,
													"DatabaseJoin.Exception.FieldNotFound", getParameterField()[i])); //$NON-NLS-1$ //$NON-NLS-2$
						}

						lookupRowMeta.addValueMeta(rowMeta.getValueMeta(
								keynrs[i]).clone());
					}
					first = false;
				}
			}
		}
		boolean sendToErrorRow = false;
		String errorMessage = null;
		try {
			return lookupValuesByWeb(rowMeta, r);
		} catch (Exception e) {
			if (getStepMeta().isDoingErrorHandling()) {
				sendToErrorRow = true;
				errorMessage = e.toString();
			} else {
				throw new KettleException(BaseMessages.getString(PKG,
						"DATABASEJOIN ERROR"), e);
			}
			if (sendToErrorRow) {
				return putErrorReturn(rowMeta, r, 1, errorMessage, null,
						"IS_E001");
			}
		}
		throw new KettleException("DATABASEJOIN ERROR");
	}

	public boolean web_moveInit() {
		if (hadInit) {
			return true;
		}
		if (this.databaseMeta == null) {
			ConstLog.message(BaseMessages.getString(PKG,
					"DatabaseJoin.Init.ConnectionMissing", getStepname()));
			return false;
		}
		web_db = new WEB_Database(this, databaseMeta);

		try {
			hadInit = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		hadInit = false;
		return false;
	}

	public RowMetaInterface getParameterRow(RowMetaInterface fields) {
		RowMetaInterface param = new RowMeta();

		if (fields != null) {
			for (int i = 0; i < parameterField.length; i++) {
				ValueMetaInterface v = fields
						.searchValueMeta(parameterField[i]);
				if (v != null)
					param.addValueMeta(v);
			}
		}
		return param;
	}

	public void getFields(RowMetaInterface row, String name)
			throws KettleException {

		if (databaseMeta == null)
			return;

		if (!singleVars) {
			ValueMetaInterface addColumn = new ValueMeta((outResultColumn),
					ValueMetaInterface.TYPE_SERIALIZABLE);
			addColumn.setOrigin(name);
			row.addValueMeta(addColumn);
			return;
		}

		web_db = new WEB_Database(this, databaseMeta);

		RowMetaInterface param = getParameterRow(row);

		RowMetaInterface add = null;
		try {
			add = web_db.getQueryFields(web_db.connect(), sql, true, param,
					new Object[param.size()]);
		} catch (KettleException dbe) {
			throw new KettleException(
					BaseMessages
							.getString(PKG,
									"DatabaseJoinMeta.Exception.UnableToDetermineQueryFields") + Const.CR + sql, dbe); //$NON-NLS-1$
		}

		if (add != null) {
			for (int i = 0; i < add.size(); i++) {
				ValueMetaInterface v = add.getValueMeta(i);
				v.setOrigin(name);
			}
			row.addRowMeta(add);

		} else
			try {
				add = web_db.getQueryFields(web_db.connect(), sql, true, param,
						new Object[param.size()]);
				for (int i = 0; i < add.size(); i++) {
					ValueMetaInterface v = add.getValueMeta(i);
					v.setOrigin(name);
				}
				row.addRowMeta(add);

			} catch (KettleException dbe) {
				throw new KettleException(BaseMessages.getString(PKG,
						"DatabaseJoinMeta.Exception.ErrorObtainingFields"), dbe); //$NON-NLS-1$
			}
	}

	public int getRowLimit() {
		return rowLimit;
	}

	public boolean isSingleVars() {
		return singleVars;
	}

	public String[] getParameterField() {
		return parameterField;
	}
}

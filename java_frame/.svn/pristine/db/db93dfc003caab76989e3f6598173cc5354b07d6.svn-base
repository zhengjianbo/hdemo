package com.ram.kettle.database;

import java.io.StringReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.ram.kettle.database.impl.MySqlDatabaseMeta;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaAndData;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.KettleEnvironment;
import com.ram.kettle.value.ValueMeta;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.server.util.BaseLog;

public class WEB_Database {

	private int rowlimit = 0;

	private DatabaseMeta databaseMeta;

	private RowMetaInterface rowMeta = null;

	public WEB_Database(StepMeta stepMeta, DatabaseMeta databaseMeta) {
		this(databaseMeta);
	}

	public WEB_Database(DatabaseMeta databaseMeta) {
		this.databaseMeta = databaseMeta;
		rowlimit = 0;
	}

	public RowMetaInterface getReturnRowMeta() {
		return rowMeta;
	}

	public RowMetaInterface getReturnRowMeta(ResultSet rs)
			throws KettleException {
		if (rowMeta == null) {// 只执行一次，获取字段名相关信息--wei
			ResultSetMetaData rsmd = null;
			try {
				rsmd = rs.getMetaData();
			} catch (SQLException e) {
				throw new KettleException(
						"Unable to retrieve metadata from resultset", e);
			}

			rowMeta = getRowInfo(rsmd, false, false);
		}
		return rowMeta;
	}

	public Connection connect() throws KettleException {
		return normalConnect(null);
	}

	public Connection normalConnect(String partitionId) throws KettleException {
		if (databaseMeta == null) {
			throw new KettleException("No valid database connection defined!");
		}
		try {
			// 进行判断使用哪个数据池
			if ("HikariCPPoolUtil"
					.equalsIgnoreCase(KettleEnvironment.KTRDBPOOL)) {
				return HikariCPPoolUtil
						.getConnection(databaseMeta, partitionId);
			}
			return ConnectionPoolUtil.getConnection(databaseMeta, partitionId);
		} catch (Exception e) {
			e.printStackTrace();
			ConstLog.message("初始化数据源连接" + databaseMeta.getName() + ","
					+ databaseMeta.getHostname() + "发生错误,让程序继续运行，");
			return null;
		}
	}

	public PreparedStatement prepareSQL(Connection connection, String sql)
			throws KettleException {
		return prepareSQL(connection, sql, false);
	}

	public RowMetaAndData getGeneratedKeys(PreparedStatement ps)
			throws KettleException {
		ResultSet keys = null;
		try {
			keys = ps.getGeneratedKeys();
			ResultSetMetaData resultSetMetaData = keys.getMetaData();
			if (resultSetMetaData == null) {
				resultSetMetaData = ps.getMetaData();
			}
			RowMetaInterface rowMeta;
			if (resultSetMetaData == null) {
				rowMeta = new RowMeta();
				rowMeta.addValueMeta(new ValueMeta("ai-key",
						ValueMetaInterface.TYPE_INTEGER));
			} else {
				rowMeta = getRowInfo(resultSetMetaData, false, false);
			}

			return new RowMetaAndData(rowMeta, getRow(keys, resultSetMetaData,
					rowMeta));
		} catch (Exception ex) {
			throw new KettleException(
					"Unable to retrieve key(s) from auto-increment field(s)",
					ex);
		} finally {
			if (keys != null) {
				try {
					keys.close();
				} catch (SQLException e) {
					throw new KettleException(
							"Unable to close resultset of auto-generated keys",
							e);
				}
			}
		}
	}

	public PreparedStatement prepareSQL(Connection connection, String sql,
			boolean returnKeys) throws KettleException {
		try {
			if (returnKeys) {
				return connection.prepareStatement(databaseMeta.stripCR(sql),
						Statement.RETURN_GENERATED_KEYS);
			} else {
				return connection.prepareStatement(databaseMeta.stripCR(sql));
			}
		} catch (SQLException ex) {
			throw new KettleException("Couldn't prepare statement:" + Const.CR
					+ sql, ex);
		}
	}

	public RowMetaInterface getRowInfo(ResultSetMetaData rm,
			boolean ignoreLength, boolean lazyConversion)
			throws KettleException {
		if (rm == null) {
			throw new KettleException(
					"No result set metadata available to retrieve row metadata!");
		}

		RowMeta rowMeta = new RowMeta();

		try {
			int fieldNr = 1;
			int nrcols = rm.getColumnCount();
			for (int i = 1; i <= nrcols; i++) {
				String name;
				if (databaseMeta.isMySQLVariant()) {
					name = new String(rm.getColumnLabel(i));
				} else {
					name = new String(rm.getColumnName(i));
				}

				if (Const.isEmpty(name) || Const.onlySpaces(name)) {
					name = "Field" + fieldNr;
					fieldNr++;
				}

				ValueMetaInterface v = getValueFromSQLType(name, rm, i,
						ignoreLength, lazyConversion);
				rowMeta.addValueMeta(v);
			}
			return rowMeta;
		} catch (SQLException ex) {
			throw new KettleException(
					"Error getting row information from database: ", ex);
		}
	}

	private ValueMetaInterface getValueFromSQLType(String name,
			ResultSetMetaData rm, int index, boolean ignoreLength,
			boolean lazyConversion) throws SQLException {
		int length = -1;
		int precision = -1;
		int valtype = ValueMetaInterface.TYPE_NONE;
		boolean isClob = false;

		int type = rm.getColumnType(index);
		boolean signed = rm.isSigned(index);
		switch (type) {
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR: // Character Large Object
			valtype = ValueMetaInterface.TYPE_STRING;
			if (!ignoreLength)
				length = rm.getColumnDisplaySize(index);
			break;

		case java.sql.Types.CLOB:
			valtype = ValueMetaInterface.TYPE_STRING;
			length = DatabaseMeta.CLOB_LENGTH;
			isClob = true;
			break;

		case java.sql.Types.BIGINT:
			// verify Unsigned BIGINT overflow!
			//
			if (signed) {
				valtype = ValueMetaInterface.TYPE_INTEGER;
				precision = 0; // Max 9.223.372.036.854.775.807
				length = 15;
			} else {
				valtype = ValueMetaInterface.TYPE_BIGNUMBER;
				precision = 0; // Max 18.446.744.073.709.551.615
				length = 16;
			}
			break;

		case java.sql.Types.INTEGER:
			valtype = ValueMetaInterface.TYPE_INTEGER;
			precision = 0; // Max 2.147.483.647
			length = 9;
			break;

		case java.sql.Types.SMALLINT:
			valtype = ValueMetaInterface.TYPE_INTEGER;
			precision = 0; // Max 32.767
			length = 4;
			break;

		case java.sql.Types.TINYINT:
			valtype = ValueMetaInterface.TYPE_INTEGER;
			precision = 0; // Max 127
			length = 2;
			break;

		case java.sql.Types.DECIMAL:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
		case java.sql.Types.REAL:
		case java.sql.Types.NUMERIC:
			valtype = ValueMetaInterface.TYPE_NUMBER;
			length = rm.getPrecision(index);
			precision = rm.getScale(index);
			if (length >= 126)
				length = -1;
			if (precision >= 126)
				precision = -1;

			if (type == java.sql.Types.DOUBLE || type == java.sql.Types.FLOAT
					|| type == java.sql.Types.REAL) {
				if (precision == 0) {
					precision = -1; // precision is obviously incorrect if the
									// type if Double/Float/Real
				}

				// MySQL: max resolution is double precision floating point
				// (double)
				// The (12,31) that is given back is not correct
				// if (databaseMeta.getDatabaseInterface() instanceof
				// MySQLDatabaseMeta) {
				// if (precision >= length) {
				// precision = -1;
				// length = -1;
				// }
				// }

				// if the length or precision needs a BIGNUMBER
				if (length > 15 || precision > 15)
					valtype = ValueMetaInterface.TYPE_BIGNUMBER;
			} else {
				if (precision == 0) {
					if (length <= 18 && length > 0) { // Among others Oracle is
														// affected here.
						valtype = ValueMetaInterface.TYPE_INTEGER; // Long can
																	// hold up
																	// to 18
																	// significant
																	// digits
					} else if (length > 18) {
						valtype = ValueMetaInterface.TYPE_BIGNUMBER;
					}
				} else { // we have a precision: keep NUMBER or change to
							// BIGNUMBER?
					if (length > 15 || precision > 15)
						valtype = ValueMetaInterface.TYPE_BIGNUMBER;
				}
			}

			// if (databaseMeta.getDatabaseInterface() instanceof
			// OracleDatabaseMeta) {
			// if (precision == 0 && length == 38) {
			// valtype = ValueMetaInterface.TYPE_INTEGER;
			// }
			// if (precision <= 0 && length <= 0) // undefined size: BIGNUMBER,
			// // precision on Oracle can
			// // be 38, too big for a
			// // Number type
			// {
			// valtype = ValueMetaInterface.TYPE_BIGNUMBER;
			// length = -1;
			// precision = -1;
			// }
			// }
			break;

		case java.sql.Types.DATE:

		case java.sql.Types.TIME:
		case java.sql.Types.TIMESTAMP:
			valtype = ValueMetaInterface.TYPE_DATE;
			//
			// if (databaseMeta.getDatabaseInterface() instanceof
			// MySQLDatabaseMeta) {
			// String property = databaseMeta.getConnectionProperties()
			// .getProperty("yearIsDateType");
			// if (property != null && property.equalsIgnoreCase("false")
			// && rm.getColumnTypeName(index).equalsIgnoreCase("YEAR")) {
			// valtype = ValueMetaInterface.TYPE_INTEGER;
			// precision = 0;
			// length = 4;
			// break;
			// }
			// }
			break;

		case java.sql.Types.BOOLEAN:
		case java.sql.Types.BIT:
			valtype = ValueMetaInterface.TYPE_BOOLEAN;
			break;

		case java.sql.Types.BINARY:
		case java.sql.Types.BLOB:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			valtype = ValueMetaInterface.TYPE_BINARY;

			if (databaseMeta.isDisplaySizeTwiceThePrecision()
					&& (2 * rm.getPrecision(index)) == rm
							.getColumnDisplaySize(index)) {

				length = rm.getPrecision(index);
			}
			// else if ((databaseMeta.getDatabaseInterface() instanceof
			// OracleDatabaseMeta || databaseMeta
			// .isMySQLVariant())
			// && (type == java.sql.Types.VARBINARY || type ==
			// java.sql.Types.LONGVARBINARY)) {
			// // set the length for Oracle "RAW" or "LONGRAW" data types
			// valtype = ValueMetaInterface.TYPE_STRING;
			// length = rm.getColumnDisplaySize(index);
			// }
			else {
				length = -1;
			}
			precision = -1;
			break;

		default:
			valtype = ValueMetaInterface.TYPE_STRING;
			precision = rm.getScale(index);
			break;
		}

		// Grab the comment as a description to the field as well.
		String comments = rm.getColumnLabel(index);

		// get & store more result set meta data for later use
		int originalColumnType = rm.getColumnType(index);
		String originalColumnTypeName = rm.getColumnTypeName(index);
		int originalPrecision = -1;
		if (!ignoreLength)
			rm.getPrecision(index); // Throws exception on MySQL
		int originalScale = rm.getScale(index);
		boolean originalSigned = rm.isSigned(index);

		ValueMetaInterface v = new ValueMeta(name, valtype);
		v.setLength(length);
		v.setPrecision(precision);
		v.setComments(comments);
		v.setLargeTextField(isClob);
		v.setOriginalColumnType(originalColumnType);
		v.setOriginalColumnTypeName(originalColumnTypeName);
		v.setOriginalPrecision(originalPrecision);
		v.setOriginalScale(originalScale);
		v.setOriginalSigned(originalSigned);

		if (lazyConversion && valtype == ValueMetaInterface.TYPE_STRING) {
			v.setStorageType(ValueMetaInterface.STORAGE_TYPE_BINARY_STRING);

			ValueMetaInterface storageMetaData = v.clone();
			storageMetaData.setType(ValueMetaInterface.TYPE_STRING);
			storageMetaData
					.setStorageType(ValueMetaInterface.STORAGE_TYPE_NORMAL);
			v.setStorageMetadata(storageMetaData);
		}

		return v;
	}

	public Object[] getRow(RowMetaInterface rowInfo, ResultSet rs)
			throws KettleException {

		RowMetaInterface rowMeta = getReturnRowMeta(rs);
		return getRow(rs, null, rowMeta);
	}

	public Object[] getRow(ResultSet rs) throws KettleException {

		RowMetaInterface rowMeta = getReturnRowMeta(rs);
		return getRow(rs, null, rowMeta);
	}

	public Object[] getRow(ResultSet rs, ResultSetMetaData dummy,
			RowMetaInterface rowInfo) throws KettleException {
		try {
			int nrcols = rowInfo.size();

			Object[] data = RowDataUtil.allocateRowData(nrcols);

			if (rs.next()) {
				for (int i = 0; i < nrcols; i++) {
					ValueMetaInterface val = rowInfo.getValueMeta(i);
					data[i] = databaseMeta.getValueFromResultSet(rs, val, i);
				}
			} else {
				data = null;
			}

			return data;
		} catch (Exception ex) {
			throw new KettleException("Couldn't get row from result set:"
					+ rowInfo.size(), ex);
		}
	}

	public void clean(boolean isUpdate, Connection conn, PreparedStatement ps,
			ResultSet rs) throws KettleException {

		try {
			if (rs != null)
				rs.close();
			rs = null;
			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (!DSTransactionManager.inTrans()) {
				try {
					if (conn != null) {
						BaseLog.debug("conn.getAutoCommit():"
								+ conn.getAutoCommit());
						if (isUpdate && !conn.getAutoCommit())
							conn.commit();

						conn.close();
					}
				} catch (Exception e) {
					throw new KettleException(e);
				}
			}
		} catch (SQLException e) {
			throw new KettleException(e);
		}
	}

	public void clean(boolean isUpdate, Connection conn, PreparedStatement ps)
			throws KettleException {
		ResultSet rs = null;
		this.clean(isUpdate, conn, ps, rs);
	}

	public void closePs(PreparedStatement ps) throws KettleException {
		try {
			if (ps != null) {
				ps.close();
				ps = null;
			}
		} catch (Exception ex) {
			throw new KettleException(
					"Couldn't close query: resultset or prepared statements",
					ex);
		}
	}

	public String getInsertStatement(String schemaName, String tableName,
			RowMetaInterface fields) {
		StringBuffer ins = new StringBuffer(128);
		if (!Const.isEmpty(schemaName)) {
			tableName = schemaName + "." + tableName;
		}
		ins.append("INSERT INTO ").append(tableName).append(" (");
		// now add the names in the row:
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0)
				ins.append(", ");
			String name = fields.getValueMeta(i).getName();
			ins.append((name));
		}
		ins.append(") VALUES (");

		for (int i = 0; i < fields.size(); i++) {
			if (i > 0)
				ins.append(", ");
			ins.append(" ?");
		}
		ins.append(')');

		return ins.toString();
	}

	public Object[] getLookup(PreparedStatement ps) throws KettleException {
		return getLookup(rowMeta, ps, false);
	}

	public Object[] getLookup(RowMetaInterface rowMeta, PreparedStatement ps,
			boolean failOnMultipleResults) throws KettleException {
		ResultSet res = null;
		try {
			res = ps.executeQuery();
			Object[] ret = getRow(rowMeta, res);

			if (failOnMultipleResults) {
				if (ret != null && res.next()) {
					throw new KettleException(
							"Only 1 row was expected as a result of a lookup, and at least 2 were found!");
				}
			}
			return ret;
		} catch (SQLException ex) {
			throw new KettleException("Error looking up row in database", ex);
		} finally {
			try {
				if (res != null)
					res.close(); // close resultset!
			} catch (SQLException e) {
				throw new KettleException(
						"Unable to close resultset after looking up data", e);
			}
		}
	}

	public boolean isAutoCommit() {
		return !DSTransactionManager.inTrans();
	}

	public boolean insertRow(PreparedStatement ps) throws KettleException {
		String debug = "insertRow start";
		boolean rowsAreSafe = false;
		try {
			ps.executeUpdate();
			rowsAreSafe = true;
			return rowsAreSafe;
		} catch (BatchUpdateException ex) {
			KettleException kdbe = new KettleException("Error updating batch",
					ex);
			throw kdbe;
		} catch (SQLException ex) {
			throw new KettleException("Error inserting/updating row", ex);
		} catch (Exception e) {
			throw new KettleException(
					"Unexpected error inserting/updating row in part [" + debug
							+ "]", e);
		}
	}

	// 执行插入 更新 批量操作
	public boolean insertRow(Connection conn, PreparedStatement ps,
			boolean useBatchInsert, int commitsize, int written)
			throws Exception {
		String debug = "insertRow start";
		boolean rowsAreSafe = false;

		if (!isAutoCommit()) {
			if (useBatchInsert) {
				debug = "insertRow add batch";
				ps.addBatch();
			} else {
				debug = "insertRow exec update";
				ps.executeUpdate();
			}
		} else {
			ps.executeUpdate();
			// ps.getMetaData();
		}

		written++;
		if (!isAutoCommit() && (written % commitsize) == 0) {
			if (useBatchInsert) {
				debug = "insertRow executeBatch commit";
				ps.executeBatch();
				// 批量 100条后 直接提交
				commit();
				ps.clearBatch();
			} else {
				debug = "insertRow normal commit";
				commit();
			}
			rowsAreSafe = true;
			return rowsAreSafe;
		}
		// 剩余的零散数据还未进行commit的 可以在最后commit
		return rowsAreSafe;

	}

	public boolean insertRowOver(Connection batchCon, PreparedStatement ps,
			boolean useBatchInsert, boolean rowsAreSafe) throws Exception {
		if (!isAutoCommit() && !rowsAreSafe) {
			if (useBatchInsert) {
				ps.executeBatch();
				// 批量 100条后 直接提交
				commit();
				ps.clearBatch();
			}
		}
		return true;
	}

	public void commit() throws Exception {
		if (DSTransactionManager.inTrans()) {
			DSTransactionManager.commit(databaseMeta.getName());
		} else {
			// 未用事务
			connect().commit();
		}
	}

	public int countParameters(String sql) {
		int q = 0;
		boolean quote_opened = false;
		boolean dquote_opened = false;

		for (int x = 0; x < sql.length(); x++) {
			char c = sql.charAt(x);

			switch (c) {
			case '\'':
				quote_opened = !quote_opened;
				break;
			case '"':
				dquote_opened = !dquote_opened;
				break;
			case '?':
				if (!quote_opened && !dquote_opened)
					q++;
				break;
			}
		}

		return q;
	}

	public RowMetaInterface getParameterMetaData(String sql,
			RowMetaInterface inform, Object[] data) {
		int q = countParameters(sql);

		RowMetaInterface par = new RowMeta();

		if (inform != null && q == inform.size()) {
			for (int i = 0; i < q; i++) {
				ValueMetaInterface inf = inform.getValueMeta(i);
				ValueMetaInterface v = inf.clone();
				par.addValueMeta(v);
			}
		} else {
			for (int i = 0; i < q; i++) {
				ValueMetaInterface v = new ValueMeta("name" + i,
						ValueMetaInterface.TYPE_NUMBER);
				par.addValueMeta(v);
			}
		}

		return par;
	}

	public RowMetaInterface getQueryFields(Connection connection, String sql,
			boolean param, RowMetaInterface inform, Object[] data)
			throws KettleException {
		RowMetaInterface fields;

		if (connection == null)
			return null;

		if (databaseMeta.supportsPreparedStatementMetadataRetrieval()) {

			PreparedStatement preparedStatement = null;
			try {
				preparedStatement = connection.prepareStatement(
						databaseMeta.stripCR(sql), ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				ResultSetMetaData rsmd = preparedStatement.getMetaData();
				fields = getRowInfo(rsmd, false, false);
			} catch (Exception e) {
				fields = getQueryFieldsFallback(connection, sql, param, inform,
						data);
			} finally {
				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						throw new KettleException(
								"Unable to close prepared statement after determining SQL layout",
								e);
					}
				}
			}
		} else {
			fields = getQueryFieldsFallback(connection, sql, param, inform,
					data);
		}

		return fields;
	}

	private RowMetaInterface getQueryFieldsFallback(Connection connection,
			String sql, boolean param, RowMetaInterface inform, Object[] data)
			throws KettleException {
		RowMetaInterface fields;

		try {
			sql = databaseMeta.stripCR(sql);
			sql = sql.replaceAll("(?i)[\\s\\r\\n]where[\\s\\r\\n]",
					" WHERE 1=2 and ");

			if (inform == null
					&& databaseMeta.getDatabaseInterface() instanceof MySqlDatabaseMeta) {
				Statement sel_stmt = connection
						.createStatement(ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_READ_ONLY);

				sel_stmt.setFetchSize(1);

				sel_stmt.setMaxRows(1);

				ResultSet r = sel_stmt.executeQuery(databaseMeta.stripCR(sql));
				fields = getRowInfo(r.getMetaData(), false, false);
				r.close();
				sel_stmt.close();
				sel_stmt = null;
			} else {
				PreparedStatement ps = connection.prepareStatement(databaseMeta
						.stripCR(sql));
				if (param) {
					RowMetaInterface par = inform;

					if (par == null || par.isEmpty())
						par = getParameterMetaData(sql, inform, data);

					setValues(par, data, ps);
				}
				ResultSet r = ps.executeQuery();
				fields = getRowInfo(ps.getMetaData(), false, false);
				r.close();
				ps.close();
			}
		} catch (Exception ex) {
			throw new KettleException("Couldn't get field info from [" + sql
					+ "]" + Const.CR, ex);
		}

		return fields;
	}

	public void setValue(PreparedStatement ps, ValueMetaInterface v,
			Object object, int pos) throws KettleException {
		String debug = "";

		try {
			switch (v.getType()) {
			case ValueMetaInterface.TYPE_NUMBER:
				if (!v.isNull(object)) {
					debug = "Number, not null, getting number from value";
					double num = v.getNumber(object).doubleValue();
					if (databaseMeta.supportsFloatRoundingOnUpdate()
							&& v.getPrecision() >= 0) {
						debug = "Number, rounding to precision ["
								+ v.getPrecision() + "]";
						num = Const.round(num, v.getPrecision());
					}
					debug = "Number, setting [" + num + "] on position #" + pos
							+ " of the prepared statement";
					ps.setDouble(pos, num);
				} else {
					ps.setNull(pos, java.sql.Types.DOUBLE);
				}
				break;
			case ValueMetaInterface.TYPE_INTEGER:
				debug = "Integer";
				if (!v.isNull(object)) {
					if (databaseMeta.supportsSetLong()) {
						ps.setLong(pos, v.getInteger(object).longValue());
					} else {
						double d = v.getNumber(object).doubleValue();
						if (databaseMeta.supportsFloatRoundingOnUpdate()
								&& v.getPrecision() >= 0) {
							ps.setDouble(pos, d);
						} else {
							ps.setDouble(pos, Const.round(d, v.getPrecision()));
						}
					}
				} else {
					ps.setNull(pos, java.sql.Types.INTEGER);
				}
				break;
			case ValueMetaInterface.TYPE_STRING:
				debug = "String";
				if (v.getLength() < DatabaseMeta.CLOB_LENGTH) {
					if (!v.isNull(object)) {
						ps.setString(pos, v.getString(object));
					} else {
						ps.setNull(pos, java.sql.Types.VARCHAR);
					}
				} else {
					if (!v.isNull(object)) {
						String string = v.getString(object);

						int maxlen = databaseMeta.getMaxTextFieldLength();
						int len = string.length();

						// Take the last maxlen characters of the string...
						int begin = len - maxlen;
						if (begin < 0)
							begin = 0;

						// Get the substring!
						String logging = string.substring(begin);

						if (databaseMeta.supportsSetCharacterStream()) {
							StringReader sr = new StringReader(logging);
							ps.setCharacterStream(pos, sr, logging.length());
						} else {
							ps.setString(pos, logging);
						}
					} else {
						ps.setNull(pos, java.sql.Types.VARCHAR);
					}
				}
				break;
			case ValueMetaInterface.TYPE_DATE:
				debug = "Date";
				if (!v.isNull(object)) {
					long dat = v.getInteger(object).longValue(); // converts
																	// using
																	// Date.getTime()

					if (v.getPrecision() == 1
							|| !databaseMeta
									.supportsTimeStampToDateConversion()) {
						// Convert to DATE!
						java.sql.Date ddate = new java.sql.Date(dat);
						ps.setDate(pos, ddate);
					} else {
						java.sql.Timestamp sdate = new java.sql.Timestamp(dat);
						ps.setTimestamp(pos, sdate);
					}
				} else {
					if (v.getPrecision() == 1
							|| !databaseMeta
									.supportsTimeStampToDateConversion()) {
						ps.setNull(pos, java.sql.Types.DATE);
					} else {
						ps.setNull(pos, java.sql.Types.TIMESTAMP);
					}
				}
				break;
			case ValueMetaInterface.TYPE_BOOLEAN:
				debug = "Boolean";
				if (databaseMeta.supportsBooleanDataType()) {
					if (!v.isNull(object)) {
						ps.setBoolean(pos, v.getBoolean(object).booleanValue());
					} else {
						ps.setNull(pos, java.sql.Types.BOOLEAN);
					}
				} else {
					if (!v.isNull(object)) {
						ps.setString(pos,
								v.getBoolean(object).booleanValue() ? "Y" : "N");
					} else {
						ps.setNull(pos, java.sql.Types.CHAR);
					}
				}
				break;
			case ValueMetaInterface.TYPE_BIGNUMBER:
				debug = "BigNumber";
				if (!v.isNull(object)) {
					ps.setBigDecimal(pos, v.getBigNumber(object));
				} else {
					ps.setNull(pos, java.sql.Types.DECIMAL);
				}
				break;
			case ValueMetaInterface.TYPE_BINARY:
				debug = "Binary";
				if (!v.isNull(object)) {
					ps.setBytes(pos, v.getBinary(object));
				} else {
					ps.setNull(pos, java.sql.Types.BINARY);
				}
				break;
			default:
				debug = "default";
				// placeholder
				ps.setNull(pos, java.sql.Types.VARCHAR);
				break;
			}
		} catch (SQLException ex) {
			throw new KettleException("Error setting value #" + pos + " ["
					+ v.toString() + "] on prepared statement (" + debug + ")"
					+ Const.CR + ex.toString(), ex);
		} catch (Exception e) {
			throw new KettleException("Error setting value #" + pos + " ["
					+ (v == null ? "NULL" : v.toString())
					+ "] on prepared statement (" + debug + ")" + Const.CR
					+ e.toString(), e);
		}
	}

	public void setValues(RowMetaInterface rowMeta, Object[] data,
			PreparedStatement ps) throws KettleException {
		for (int i = 0; i < rowMeta.size(); i++) {
			ValueMetaInterface v = rowMeta.getValueMeta(i);
			Object object = data[i];
			try {
				setValue(ps, v, object, i + 1);
			} catch (KettleException e) {
				throw new KettleException("offending row : " + rowMeta, e);
			}
		}
	}

	public void setValues(RowMetaAndData row, PreparedStatement ps)
			throws KettleException {
		setValues(row.getRowMeta(), row.getData(), ps);
	}

	private boolean canWeSetFetchSize(Statement statement) throws SQLException {
		return true;
	}

	public ResultSet openQuery(Connection connection, String sql,
			RowMetaInterface params, Object[] data) throws KettleException {
		return openQuery(connection, sql, params, data,
				ResultSet.FETCH_FORWARD, false);
	}

	public ResultSet openQuery(Connection connection, String sql,
			RowMetaInterface params, Object[] data, int fetch_mode,
			boolean lazyConversion) throws KettleException {
		ResultSet res;
		String debug = "Start";

		try {
			if (params != null) {
				debug = "P create prepared statement (con==null? "
						+ (connection == null) + ")";
				PreparedStatement pstmt = connection.prepareStatement(
						databaseMeta.stripCR(sql), ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				debug = "P Set values";
				setValues(params, data, pstmt); // set the dates etc!
				if (canWeSetFetchSize(pstmt)) {
					debug = "P Set fetchsize";
					int fs = Const.FETCH_SIZE <= pstmt.getMaxRows() ? pstmt
							.getMaxRows() : Const.FETCH_SIZE;

					pstmt.setFetchSize(fs);
					debug = "P Set fetch direction";
					pstmt.setFetchDirection(fetch_mode);
				}
				debug = "P Set max rows";
				pstmt.setMaxRows(rowlimit);
				debug = "exec query";
				res = pstmt.executeQuery();
			} else {
				debug = "create statement";
				Statement sel_stmt = connection.createStatement();
				if (canWeSetFetchSize(sel_stmt)) {
					debug = "Set fetchsize";
					int fs = Const.FETCH_SIZE <= sel_stmt.getMaxRows() ? sel_stmt
							.getMaxRows() : Const.FETCH_SIZE;
					// if (databaseMeta.getDatabaseInterface() instanceof
					// MySQLDatabaseMeta
					// && databaseMeta.isStreamingResults()) {
					// sel_stmt.setFetchSize(Integer.MIN_VALUE);
					// } else {
					sel_stmt.setFetchSize(fs);
					// }
					debug = "Set fetch direction";
					sel_stmt.setFetchDirection(fetch_mode);
				}
				debug = "Set max rows";
				if (rowlimit > 0)
					sel_stmt.setMaxRows(rowlimit);

				debug = "exec query";
				res = sel_stmt.executeQuery(databaseMeta.stripCR(sql));
			}
			debug = "openQuery : get rowinfo";

		} catch (SQLException ex) {

			throw new KettleException("An error occurred executing SQL: "
					+ Const.CR + sql, ex);
		} catch (Exception e) {
			ConstLog.message("ERROR executing query: " + e.toString());
			ConstLog.message("ERROR in part: " + debug);
			throw new KettleException(
					"An error occurred executing SQL in part [" + debug + "]:"
							+ Const.CR + sql, e);
		}

		return res;
	}

	public ResultSet openQuery(PreparedStatement ps, RowMetaInterface params,
			Object[] data) throws KettleException {
		ResultSet res;
		String debug = "Start";

		// Create a Statement
		try {
			debug = "OQ Set values";
			if (params != null) {
				setValues(params, data, ps); // set the parameters!
			}
			res = ps.executeQuery();

			debug = "OQ getRowInfo()";
			BaseLog.debug(" openquery select sql ");

		} catch (SQLException ex) {
			throw new KettleException("ERROR executing query in part[" + debug
					+ "]", ex);
		} catch (Exception e) {
			throw new KettleException("ERROR executing query in part[" + debug
					+ "]", e);
		}

		return res;
	}
}

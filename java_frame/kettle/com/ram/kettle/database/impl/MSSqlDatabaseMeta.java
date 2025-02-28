package com.ram.kettle.database.impl;

import com.ram.kettle.database.BaseDatabaseMeta;
import com.ram.kettle.database.DatabaseInterface;
import com.ram.kettle.database.DatabaseMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.value.ValueMetaInterface;

public class MSSqlDatabaseMeta extends BaseDatabaseMeta implements
		DatabaseInterface {
	private String pluginId = "MSSQL";

	public String getPluginId() {
		return pluginId;
	}

	public int[] getAccessTypeList() {
		return new int[] { DatabaseMeta.TYPE_ACCESS_NATIVE,
				DatabaseMeta.TYPE_ACCESS_ODBC, DatabaseMeta.TYPE_ACCESS_JNDI };
	}

	public int getDefaultDatabasePort() {
		if (getAccessType() == DatabaseMeta.TYPE_ACCESS_NATIVE)
			return 3306;
		return -1;
	}

	public String getLimitClause(int nrRows) {
		return " LIMIT " + nrRows;
	}

	/**
	 * Returns the minimal SQL to launch in order to determine the layout of the
	 * resultset for a given database table
	 * 
	 * @param tableName
	 *            The name of the table to determine the layout for
	 * @return The SQL to launch.
	 */
	public String getSQLQueryFields(String tableName) {
		return "SELECT * FROM " + tableName + " LIMIT 0"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getSQLTableExists(String tablename) {
		return getSQLQueryFields(tablename);
	}

	public String getSQLColumnExists(String columnname, String tablename) {
		return getSQLQueryColumnFields(columnname, tablename);
	}

	public String getSQLQueryColumnFields(String columnname, String tableName) {
		return "SELECT " + columnname + " FROM " + tableName + " where 1=2 "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @see org.pentaho.di.core.database.DatabaseInterface#getNotFoundTK(boolean)
	 */
	public int getNotFoundTK(boolean use_autoinc) {
		if (supportsAutoInc() && use_autoinc) {
			return 1;
		}
		return super.getNotFoundTK(use_autoinc);
	}

	public String getDriverClass() {
		 if (getAccessType()==DatabaseMeta.TYPE_ACCESS_ODBC)
		    {
		      return "sun.jdbc.odbc.JdbcOdbcDriver"; //$NON-NLS-1$
		    }
		    else
		    {
		      return "com.microsoft.sqlserver.jdbc.SQLServerDriver";//$NON-NLS-1$
		    }
	}

	public String getURL(String hostname, String port, String databaseName) {
		if (getAccessType()==DatabaseMeta.TYPE_ACCESS_ODBC)
	    {
	      return "jdbc:odbc:"+databaseName;//$NON-NLS-1$
	    }
	    else
	    {
	      
	      String url = "jdbc:sqlserver://"+hostname;
	      
	      if (!Const.isEmpty(port) && Const.toInt(port, -1)>0) {
	        url += ":"+port;
	      }
	      url+=";databaseName="+databaseName;
	      
	      return url;
	    }
	}

	/**
	 * @return The extra option separator in database URL for this platform
	 *         (usually this is semicolon ; )
	 */
	public String getExtraOptionSeparator() {
		return "&";
	}

	/**
	 * @return This indicator separates the normal URL from the options
	 */
	public String getExtraOptionIndicator() {
		return "?";
	}

	/**
	 * @return true if the database supports transactions.
	 */
	public boolean supportsTransactions() {
		return false;
	}

	/**
	 * @return true if the database supports bitmap indexes
	 */
	public boolean supportsBitmapIndex() {
		return false;
	}

	/**
	 * @return true if the database supports views
	 */
	public boolean supportsViews() {
		return true;
	}

	/**
	 * @return true if the database supports synonyms
	 */
	public boolean supportsSynonyms() {
		return false;
	}

	/**
	 * Generates the SQL statement to add a column to the specified table
	 * 
	 * @param tablename
	 *            The table to add
	 * @param v
	 *            The column defined as a value
	 * @param tk
	 *            the name of the technical key field
	 * @param use_autoinc
	 *            whether or not this field uses auto increment
	 * @param pk
	 *            the name of the primary key field
	 * @param semicolon
	 *            whether or not to add a semi-colon behind the statement.
	 * @return the SQL statement to add a column to the specified table
	 */
	public String getAddColumnStatement(String tablename, ValueMetaInterface v,
			String tk, boolean use_autoinc, String pk, boolean semicolon) {
		return "ALTER TABLE " + tablename + " ADD "
				+ getFieldDefinition(v, tk, pk, use_autoinc, true, false);
	}

	/**
	 * Generates the SQL statement to modify a column in the specified table
	 * 
	 * @param tablename
	 *            The table to add
	 * @param v
	 *            The column defined as a value
	 * @param tk
	 *            the name of the technical key field
	 * @param use_autoinc
	 *            whether or not this field uses auto increment
	 * @param pk
	 *            the name of the primary key field
	 * @param semicolon
	 *            whether or not to add a semi-colon behind the statement.
	 * @return the SQL statement to modify a column in the specified table
	 */
	public String getModifyColumnStatement(String tablename,
			ValueMetaInterface v, String tk, boolean use_autoinc, String pk,
			boolean semicolon) {
		return "ALTER TABLE " + tablename + " MODIFY "
				+ getFieldDefinition(v, tk, pk, use_autoinc, true, false);
	}

	public String getFieldDefinition(ValueMetaInterface v, String tk,
			String pk, boolean use_autoinc, boolean add_fieldname,
			boolean add_cr) {
		String retval = "";

		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();

		if (add_fieldname)
			retval += fieldname + " ";

		int type = v.getType();
		switch (type) {
		case ValueMetaInterface.TYPE_DATE:
			retval += "DATETIME";
			break;
		case ValueMetaInterface.TYPE_BOOLEAN:
			if (supportsBooleanDataType()) {
				retval += "BOOLEAN";
			} else {
				retval += "CHAR(1)";
			}
			break;

		case ValueMetaInterface.TYPE_NUMBER:
		case ValueMetaInterface.TYPE_INTEGER:
		case ValueMetaInterface.TYPE_BIGNUMBER:
			if (fieldname.equalsIgnoreCase(tk) || // Technical key
					fieldname.equalsIgnoreCase(pk) // Primary key
			) {
				if (use_autoinc) {
					retval += "BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY";
				} else {
					retval += "BIGINT NOT NULL PRIMARY KEY";
				}
			} else {
				// Integer values...
				if (precision == 0) {
					if (length > 9) {
						if (length < 19) {
							// can hold signed values between
							// -9223372036854775808 and 9223372036854775807
							// 18 significant digits
							retval += "BIGINT";
						} else {
							retval += "DECIMAL(" + length + ")";
						}
					} else {
						retval += "INT";
					}
				}
				// Floating point values...
				else {
					if (length > 15) {
						retval += "DECIMAL(" + length;
						if (precision > 0)
							retval += ", " + precision;
						retval += ")";
					} else {
						// A double-precision floating-point number is accurate
						// to approximately 15 decimal places.
						// http://mysql.mirrors-r-us.net/doc/refman/5.1/en/numeric-type-overview.html
						retval += "DOUBLE";
					}
				}
			}
			break;
		case ValueMetaInterface.TYPE_STRING:
			if (length > 0) {
				if (length == 1)
					retval += "CHAR(1)";
				else if (length < 256)
					retval += "VARCHAR(" + length + ")";
				else if (length < 65536)
					retval += "TEXT";
				else if (length < 16777216)
					retval += "MEDIUMTEXT";
				else
					retval += "LONGTEXT";
			} else {
				retval += "TINYTEXT";
			}
			break;
		case ValueMetaInterface.TYPE_BINARY:
			retval += "LONGBLOB";
			break;
		default:
			retval += " UNKNOWN";
			break;
		}

		if (add_cr)
			retval += Const.CR;

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pentaho.di.core.database.DatabaseInterface#getReservedWords()
	 */
	public String[] getReservedWords() {
		return new String[] { "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS",
				"ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY",
				"BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE",
				"CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION",
				"CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE",
				"CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
				"CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR",
				"DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC",
				"DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC",
				"DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV",
				"DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED",
				"ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH",
				"FLOAT", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GOTO",
				"GRANT", "GROUP", "HAVING", "HIGH_PRIORITY",
				"HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF",
				"IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT",
				"INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERVAL", "INTO",
				"IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING",
				"LEAVE", "LEFT", "LIKE", "LIMIT", "LINES", "LOAD", "LOCALTIME",
				"LOCALTIMESTAMP", "LOCATE", "LOCK", "LONG", "LONGBLOB",
				"LONGTEXT", "LOOP", "LOW_PRIORITY", "MATCH", "MEDIUMBLOB",
				"MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND",
				"MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT",
				"NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE",
				"OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER",
				"OUTFILE", "POSITION", "PRECISION", "PRIMARY", "PROCEDURE",
				"PURGE", "READ", "READS", "REAL", "REFERENCES", "REGEXP",
				"RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESTRICT", "RETURN",
				"REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS",
				"SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR",
				"SET", "SHOW", "SMALLINT", "SONAME", "SPATIAL", "SPECIFIC",
				"SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING",
				"SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT",
				"SSL", "STARTING", "STRAIGHT_JOIN", "TABLE", "TERMINATED",
				"THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING",
				"TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK",
				"UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE",
				"UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR",
				"VARCHARACTER", "VARYING", "WHEN", "WHERE", "WHILE", "WITH",
				"WRITE", "XOR", "YEAR_MONTH", "ZEROFILL" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pentaho.di.core.database.DatabaseInterface#getStartQuote()
	 */
	public String getStartQuote() {
		return "`";
	}

	/**
	 * Simply add an underscore in the case of MySQL!
	 * 
	 * @see org.pentaho.di.core.database.DatabaseInterface#getEndQuote()
	 */
	public String getEndQuote() {
		return "`";
	}

	/**
	 * @param tableNames
	 *            The names of the tables to lock
	 * @return The SQL command to lock database tables for write purposes.
	 */
	public String getSQLLockTables(String tableNames[]) {
		String sql = "LOCK TABLES ";
		for (int i = 0; i < tableNames.length; i++) {
			if (i > 0)
				sql += ", ";
			sql += tableNames[i] + " WRITE";
		}
		sql += ";" + Const.CR;

		return sql;
	}

	/**
	 * @param tableName
	 *            The name of the table to unlock
	 * @return The SQL command to unlock a database table.
	 */
	public String getSQLUnlockTables(String tableName[]) {
		return "UNLOCK TABLES"; // This unlocks all tables
	}

	public boolean needsToLockAllTables() {
		return true;
	}

	/**
	 * @return extra help text on the supported options on the selected database
	 *         platform.
	 */
	public String getExtraOptionsHelpText() {
		return "http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-configuration-properties.html";
	}

	public String[] getUsedLibraries() {
		return new String[] { "mysql-connector-java-3.1.14-bin.jar" };
	}

	/**
	 * @param tableName
	 * @return true if the specified table is a system table
	 */
	public boolean isSystemTable(String tableName) {
		if (tableName.startsWith("sys"))
			return true;
		if (tableName.equals("dtproperties"))
			return true;
		return false;
	}

	/**
	 * Get the SQL to insert a new empty unknown record in a dimension.
	 * 
	 * @param schemaTable
	 *            the schema-table name to insert into
	 * @param keyField
	 *            The key field
	 * @param versionField
	 *            the version field
	 * @return the SQL to insert the unknown record into the SCD.
	 */
	public String getSQLInsertAutoIncUnknownDimensionRow(String schemaTable,
			String keyField, String versionField) {
		return "insert into " + schemaTable + "(" + keyField + ", "
				+ versionField + ") values (1, 1)";
	}

	/**
	 * @param string
	 * @return A string that is properly quoted for use in a SQL statement
	 *         (insert, update, delete, etc)
	 */
	public String quoteSQLString(String string) {
		string = string.replaceAll("'", "\\\\'");
		string = string.replaceAll("\\n", "\\\\n");
		string = string.replaceAll("\\r", "\\\\r");
		return "'" + string + "'";
	}

	/**
	 * @return true if the database is a MySQL variant, like MySQL 5.1,
	 *         InfiniDB, InfoBright, and so on.
	 */
	public boolean isMySQLVariant() {
		return true;
	}

	/**
	 * Returns a false as Oracle does not allow for the releasing of savepoints.
	 */
	@Override
	public boolean releaseSavepoint() {
		return false;
	}

	@Override
	public boolean supportsErrorHandlingOnBatchUpdates() {
		return true;
	}  
	
	public String getPageSql(final String from,final String orderby, final int page, final int size) {
		int pageNum = page;// 页数从1开始计数
		int sizeNum = size;
		if (pageNum < 1) {
			pageNum = 1;
		} 
		if (sizeNum < 0) {
			sizeNum = PAGE_SIZE;
		}
		String iOrderBy=orderby;
		if(Const.isEmpty(iOrderBy)){
			return null;
		}else{
			if(iOrderBy.toUpperCase().trim().indexOf("ORDER")<0){ 
				iOrderBy=" order by "+iOrderBy;
			}
		}
		int start = (pageNum - 1) * sizeNum;
		int end =  pageNum  * sizeNum;

		String sql= String.format(" select * from (select row_number() over(%s) rownumber,* from (%s) a ) a1 where rownumber>%s and rownumber<=%s", iOrderBy,from,start,end );
		  return sql;
	}
	
}

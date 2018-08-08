package com.ram.kettle.database;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Node;

import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.Encr;
import com.ram.kettle.util.KettleEnvironment;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;

public class DatabaseMeta implements Cloneable {
	private static Class<?> PKG = DatabaseMeta.class;

	public static final String XML_TAG = "connection";

	private DatabaseInterface databaseInterface;

	public static final Comparator<DatabaseMeta> comparator = new Comparator<DatabaseMeta>() {
		public int compare(DatabaseMeta dbm1, DatabaseMeta dbm2) {
			return dbm1.getName().compareToIgnoreCase(dbm2.getName());
		}
	};

	public Object getValueFromResultSet(ResultSet rs, ValueMetaInterface val,
			int i) throws KettleException {
		return databaseInterface.getValueFromResultSet(rs, val, i);
	}

	public boolean supportsPreparedStatementMetadataRetrieval() {
		return true;
	}

	public boolean supportsBooleanDataType() {
		return databaseInterface.supportsBooleanDataType();
	}

	public boolean isDisplaySizeTwiceThePrecision() {
		return databaseInterface.isDisplaySizeTwiceThePrecision();
	}

	public boolean isMySQLVariant() {
		return databaseInterface.isMySQLVariant();
	}

	public boolean supportsSetCharacterStream() {
		return databaseInterface.supportsSetCharacterStream();
	}

	public boolean supportsTimeStampToDateConversion() {
		return databaseInterface.supportsTimeStampToDateConversion();
	}

	public int getMaxTextFieldLength() {
		return databaseInterface.getMaxTextFieldLength();
	}

	public String stripCR(String sbsql) {
		if (sbsql == null)
			return null;
		return stripCR(new StringBuffer(sbsql));
	}

	public boolean supportsSetLong() {
		return databaseInterface.supportsSetLong();
	}

	public boolean supportsFloatRoundingOnUpdate() {
		return databaseInterface.supportsFloatRoundingOnUpdate();
	}

	private boolean supportsNewLinesInSQL() {
		return databaseInterface.supportsNewLinesInSQL();
	}

	public String stripCR(StringBuffer sbsql) {
		// DB2 Can't handle \n in SQL Statements...
		if (!supportsNewLinesInSQL()) {
			// Remove CR's
			for (int i = sbsql.length() - 1; i >= 0; i--) {
				if (sbsql.charAt(i) == '\n' || sbsql.charAt(i) == '\r')
					sbsql.setCharAt(i, ' ');
			}
		}

		return sbsql.toString();
	}

	/**
	 * Connect natively through JDBC thin driver to the database.
	 */
	public static final int TYPE_ACCESS_NATIVE = 0;

	/**
	 * Connect to the database using ODBC.
	 */
	public static final int TYPE_ACCESS_ODBC = 1;

	/**
	 * Connect to the database using OCI. (Oracle only)
	 */
	public static final int TYPE_ACCESS_OCI = 2;

	/**
	 * Connect to the database using plugin specific method. (SAP ERP)
	 */
	public static final int TYPE_ACCESS_PLUGIN = 3;

	/**
	 * Connect to the database using JNDI.
	 */
	public static final int TYPE_ACCESS_JNDI = 4;

	/**
	 * Short description of the access type, used in XML and the repository.
	 */
	public static final String dbAccessTypeCode[] = { "Native", "ODBC", "OCI",
			"Plugin", "JNDI", };

	/**
	 * Longer description for user interactions.
	 */
	public static final String dbAccessTypeDesc[] = { "Native (JDBC)", "ODBC",
			"OCI", "Plugin specific access method", "JNDI", "Custom", };

	/**
	 * Use this length in a String value to indicate that you want to use a CLOB
	 * in stead of a normal text field.
	 */
	public static final int CLOB_LENGTH = 9999999;

	/**
	 * The value to store in the attributes so that an empty value doesn't get
	 * lost...
	 */
	public static final String EMPTY_OPTIONS_STRING = "><EMPTY><";

	public DatabaseMeta(String name, String type, String access, String host,
			String db, String port, String user, String pass) {

		try {
			databaseInterface = getDatabaseInterface(type);
		} catch (KettleException kde) {
			throw new RuntimeException("Database type not found!", kde);
		} catch (Exception kde) {
			throw new RuntimeException("Database type not found!", kde);
		}

		setName(name);
		setAccessType(getAccessType(access));
		setHostname(host);
		setDBName(db);
		setDBPort(port);
		setUsername(user);
		setPassword(pass);
		setServername(null);
		setChanged(false);
	}

	private static final DatabaseInterface findDatabaseInterface(
			String databaseTypeDesc) throws Exception {

		return KettleEnvironment.getDatabaseInterfacesMap().get(
				databaseTypeDesc.toUpperCase());
	}

	public static final DatabaseInterface getDatabaseInterface(
			String databaseType) throws Exception {
		DatabaseInterface di = findDatabaseInterface(databaseType);
		if (di == null) {
			throw new KettleException(
					BaseMessages
							.getString(
									PKG,
									"DatabaseMeta.Error.DatabaseInterfaceNotFound:" + databaseType, databaseType)); //$NON-NLS-1$
		}
		return (DatabaseInterface) di.clone();
	}

	public DatabaseMeta(Node con) throws KettleException {

		try {
			String type = XMLHandler.getTagValue(con, "type");
			try {
				databaseInterface = getDatabaseInterface(type);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			setName(XMLHandler.getTagValue(con, "name"));
			setHostname(XMLHandler.getTagValue(con, "server"));
			String acc = XMLHandler.getTagValue(con, "access");
			setAccessType(getAccessType(acc));

			setDBName(XMLHandler.getTagValue(con, "database"));

			setDBPort(XMLHandler.getTagValue(con, "port"));
			setUsername(XMLHandler.getTagValue(con, "username"));
			setPassword(Encr.decryptPasswordOptionallyEncrypted(XMLHandler
					.getTagValue(con, "password")));
			setServername(XMLHandler.getTagValue(con, "servername"));
			setDataTablespace(XMLHandler.getTagValue(con, "data_tablespace"));
			setIndexTablespace(XMLHandler.getTagValue(con, "index_tablespace"));

			Node attrsnode = XMLHandler.getSubNode(con, "attributes");
			if (attrsnode != null) {
				List<Node> attrnodes = XMLHandler.getNodes(attrsnode,
						"attribute");
				for (Node attrnode : attrnodes) {
					String code = XMLHandler.getTagValue(attrnode, "code");
					String attribute = XMLHandler.getTagValue(attrnode,
							"attribute");
					if (code != null && attribute != null)
						getAttributes().put(code, attribute);
					getDatabasePortNumberString();
				}
			}

		} catch (Exception e) {
			throw new KettleException(
					"Unable to load database connection info from XML node", e);
		}
	}

	public Properties getAttributes() {
		return databaseInterface.getAttributes();
	}

	public void setDataTablespace(String data_tablespace) {
		databaseInterface.setDataTablespace(data_tablespace);
	}

	public void setIndexTablespace(String index_tablespace) {
		databaseInterface.setIndexTablespace(index_tablespace);
	}

	public void setChanged(boolean ch) {
		databaseInterface.setChanged(ch);
	}

	public String toString() {
		return getName();
	}

	public String getQuotedSchemaTableCombination(String schemaName,
			String tableName) {
		if (Const.isEmpty(schemaName)) {
			return tableName;
		} else {
			return schemaName + "." + tableName;
		}
	}

	public String quoteField(String fieldName) {
		return fieldName;
	}

	public boolean hasSpacesInField(String fieldname) {
		if (fieldname == null)
			return false;
		if (fieldname.indexOf(' ') >= 0)
			return true;
		return false;
	}

	private static DataSourceApplication instance = DataSourceApplication
			.getInstanceSingle();

	public static final DatabaseMeta findDatabase(String dbname)
			throws KettleException {
		if (instance.getMeta(dbname) == null)
			return null;
		return (DatabaseMeta) instance.getMeta(dbname.toUpperCase());
	}

	/**
	 * Sets the name of the database connection. This name should be unique in a
	 * transformation and in general in a single repository.
	 * 
	 * @param name
	 *            The name of the database connection
	 */
	public void setName(String name) {
		databaseInterface.setName(name);
	}

	public String getExtraOptionIndicator() {
		return databaseInterface.getExtraOptionIndicator();
	}

	public String getExtraOptionSeparator() {
		return databaseInterface.getExtraOptionSeparator();
	}

	public String getExtraOptionValueSeparator() {
		return databaseInterface.getExtraOptionValueSeparator();
	}

	public Map<String, String> getExtraOptions() {
		return databaseInterface.getExtraOptions();
	}

	public String getURL(String partitionId) throws KettleException {

		if (getAccessType() == TYPE_ACCESS_JNDI) {

		}

		String baseUrl;
		baseUrl = databaseInterface.getURL(getHostname(),
				getDatabasePortNumberString(), getDatabaseName());

		StringBuffer url = new StringBuffer((baseUrl));

		if (databaseInterface.supportsOptionsInURL()) {
			// OK, now add all the options...
			String optionIndicator = getExtraOptionIndicator();
			String optionSeparator = getExtraOptionSeparator();
			String valueSeparator = getExtraOptionValueSeparator();

			Map<String, String> map = getExtraOptions();
			if (map.size() > 0) {
				Iterator<String> iterator = map.keySet().iterator();
				boolean first = true;
				while (iterator.hasNext()) {
					String typedParameter = (String) iterator.next();
					int dotIndex = typedParameter.indexOf('.');
					if (dotIndex >= 0) {
						String typeCode = typedParameter.substring(0, dotIndex);
						String parameter = typedParameter
								.substring(dotIndex + 1);
						String value = map.get(typedParameter);

						if (databaseInterface.getPluginId().equals(typeCode)) {
							if (first && url.indexOf(valueSeparator) == -1) {
								url.append(optionIndicator);
							} else {
								url.append(optionSeparator);
							}

							url.append(parameter);
							if (!Const.isEmpty(value)
									&& !value.equals(EMPTY_OPTIONS_STRING)) {
								url.append(valueSeparator).append(value);
							}
							first = false;
						}
					}
				}
			}
		} else {
		}

		return url.toString();
	}

	public boolean requiresCastToVariousForIsNull() {
		return databaseInterface.requiresCastToVariousForIsNull();
	}

	/**
	 * Returns the name of the database connection
	 * 
	 * @return The name of the database connection
	 */
	public String getName() {
		return databaseInterface.getName();
	}

	/*
	 * Sets the type of database.
	 * 
	 * @param db_type The database type public void setDatabaseType(int db_type)
	 * { databaseInterface this.databaseType = db_type; }
	 */

	/**
	 * Return the type of database access. One of
	 * <p>
	 * TYPE_ACCESS_NATIVE
	 * <p>
	 * TYPE_ACCESS_ODBC
	 * <p>
	 * TYPE_ACCESS_OCI
	 * <p>
	 * 
	 * @return The type of database access.
	 */
	public int getAccessType() {
		return databaseInterface.getAccessType();
	}

	/**
	 * Set the type of database access.
	 * 
	 * @param access_type
	 *            The access type.
	 */
	public void setAccessType(int access_type) {
		databaseInterface.setAccessType(access_type);
	}

	/**
	 * Return the hostname of the machine on which the database runs.
	 * 
	 * @return The hostname of the database.
	 */
	public String getHostname() {
		return databaseInterface.getHostname();
	}

	/**
	 * Sets the hostname of the machine on which the database runs.
	 * 
	 * @param hostname
	 *            The hostname of the machine on which the database runs.
	 */
	public void setHostname(String hostname) {
		databaseInterface.setHostname(hostname);
	}

	/**
	 * Return the port on which the database listens as a String. Allows for
	 * parameterisation.
	 * 
	 * @return The database port.
	 */
	public String getDatabasePortNumberString() {
		return databaseInterface.getDatabasePortNumberString();
	}

	/**
	 * Sets the port on which the database listens.
	 * 
	 * @param db_port
	 *            The port number on which the database listens
	 */
	public void setDBPort(String db_port) {
		databaseInterface.setDatabasePortNumberString(db_port);
	}

	/**
	 * Return the name of the database.
	 * 
	 * @return The database name.
	 */
	public String getDatabaseName() {
		return databaseInterface.getDatabaseName();
	}

	/**
	 * Set the name of the database.
	 * 
	 * @param databaseName
	 *            The new name of the database
	 */
	public void setDBName(String databaseName) {
		databaseInterface.setDatabaseName(databaseName);
	}

	/**
	 * Get the username to log into the database on this connection.
	 * 
	 * @return The username to log into the database on this connection.
	 */
	public String getUsername() {
		return databaseInterface.getUsername();
	}

	/**
	 * Sets the username to log into the database on this connection.
	 * 
	 * @param username
	 *            The username
	 */
	public void setUsername(String username) {
		databaseInterface.setUsername(username);
	}

	/**
	 * Get the password to log into the database on this connection.
	 * 
	 * @return the password to log into the database on this connection.
	 */
	public String getPassword() {
		return databaseInterface.getPassword();
	}

	/**
	 * Sets the password to log into the database on this connection.
	 * 
	 * @param password
	 *            the password to log into the database on this connection.
	 */
	public void setPassword(String password) {
		databaseInterface.setPassword(password);
	}

	/**
	 * @param servername
	 *            the Informix servername
	 */
	public void setServername(String servername) {
		databaseInterface.setServername(servername);
	}

	public final static int getAccessType(String dbaccess) {
		int i;

		if (dbaccess == null)
			return TYPE_ACCESS_NATIVE;

		for (i = 0; i < dbAccessTypeCode.length; i++) {
			if (dbAccessTypeCode[i].equalsIgnoreCase(dbaccess)) {
				return i;
			}
		}
		for (i = 0; i < dbAccessTypeDesc.length; i++) {
			if (dbAccessTypeDesc[i].equalsIgnoreCase(dbaccess)) {
				return i;
			}
		}

		return TYPE_ACCESS_NATIVE;
	}

	public String environmentSubstitute(String aString) {
		return aString;
	}

	public Properties getConnectionPoolingProperties() {
		return databaseInterface.getConnectionPoolingProperties();
	}

	public String getDriverClass() {
		return environmentSubstitute(databaseInterface.getDriverClass());
	}

	public DatabaseInterface getDatabaseInterface() {
		return databaseInterface;
	}
	
	public   String getPageSql(final String sql,final String orderby, final int page,
			final int size) {
		 return databaseInterface.getPageSql(sql,orderby, page, size);
	}

	
   
	
}
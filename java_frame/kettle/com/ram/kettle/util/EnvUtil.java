package com.ram.kettle.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.ram.kettle.log.KettleException;

public class EnvUtil {
	private static Properties env = null;

	public static Properties readProperties(String fileName)
			throws KettleException  {

		String kettlePropsFilename = EnvUtil.class.getResource("/" + fileName)
				.getPath();

		Properties props = new Properties();
		InputStream is = null;
		try {
			kettlePropsFilename = java.net.URLDecoder.decode(
					kettlePropsFilename, "UTF-8");// 转换处理中文及空格
			is = new FileInputStream(kettlePropsFilename);
			props.load(is);
		} catch (IOException ioe) {
			throw new KettleException("Unable to read file '"
					+ kettlePropsFilename + "'", ioe);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return props;
	}

	public static void environmentInit() throws KettleException {

		Map<?, ?> kettleProperties = EnvUtil
				.readProperties(Const.KETTLE_PROPERTIES);
		for (Object key : kettleProperties.keySet()) {
			String variable = (String) key;
			String value = ((String) kettleProperties.get(key));
			if ("ktrhome".equals(variable)) {
				KettleEnvironment.KTRHOME = value;
			}else if ("connectionpool".equals(variable)) {
				KettleEnvironment.KTRDBPOOL = value;
			}else if ("cache".equals(variable)) {
				KettleEnvironment.CACHE = value;
			}else{ 
				KettleEnvironment.KTRHOME = "/ktr/";
				KettleEnvironment.KTRDBPOOL = "";
				KettleEnvironment.CACHE = "com.ram.kettle.cache.impl.OSCache";
			}
		}
	}

	/**
	 * Add a number of internal variables to the Kettle Variables at the root.
	 * 
	 * @param variables
	 */
	public static void addInternalVariables(Properties prop) {
		prop.put(Const.INTERNAL_VARIABLE_KETTLE_VERSION, Const.VERSION);

	}

	@SuppressWarnings({ "unchecked" })
	private static final Properties getEnv() {
		Class<?> system = System.class;
		if (env == null) {
			Map<String, String> returnMap = null;
			try {
				Method method = system.getMethod("getenv");

				returnMap = (Map<String, String>) method.invoke(system);
			} catch (Exception ex) {
				returnMap = null;
			}

			env = new Properties();
			if (returnMap != null) {
				// We're on a VM with getenv() defined.
				ArrayList<String> list = new ArrayList<String>(
						returnMap.keySet());
				for (int i = 0; i < list.size(); i++) {
					String var = list.get(i);
					String val = returnMap.get(var);

					env.setProperty(var, val);
				}
			}
		}
		return env;
	}

	/**
	 * @return an array of strings, made up of all the environment variables
	 *         available in the VM, format var=value. To be used for
	 *         Runtime.exec(cmd, envp)
	 */
	public static final String[] getEnvironmentVariablesForRuntimeExec() {
		Properties sysprops = new Properties();
		sysprops.putAll(getEnv());
		sysprops.putAll(System.getProperties());
		addInternalVariables(sysprops);

		String[] envp = new String[sysprops.size()];
		List<Object> list = new ArrayList<Object>(sysprops.keySet());
		for (int i = 0; i < list.size(); i++) {
			String var = (String) list.get(i);
			String val = sysprops.getProperty(var);

			envp[i] = var + "=" + val;
		}

		return envp;
	}

	/**
	 * This method is written especially for weird JVM's like IBM's on AIX and
	 * OS/400. On these platforms, we notice that environment variables have an
	 * extra double quote around it... This is messing up the ability to specify
	 * things.
	 * 
	 * @param key
	 *            The key, the name of the environment variable to return
	 * @param def
	 *            The default value to return in case the key can't be found
	 * @return The value of a System environment variable in the java virtual
	 *         machine. If the key is not present, the variable is not defined
	 *         and the default value is returned.
	 */
	public static final String getSystemPropertyStripQuotes(String key,
			String def) {
		String value = System.getProperty(key, def);
		if (value.startsWith("\"") && value.endsWith("\"")
				&& value.length() > 1) {
			return value.substring(1, value.length() - 2);
		}
		return value;
	}

	/**
	 * This method is written especially for weird JVM's like
	 * 
	 * @param key
	 *            The key, the name of the environment variable to return
	 * @param def
	 *            The default value to return in case the key can't be found
	 * @return The value of a System environment variable in the java virtual
	 *         machine. If the key is not present, the variable is not defined
	 *         and the default value is returned.
	 */
	public static final String getSystemProperty(String key, String def) {
		String value = System.getProperty(key, def);
		return value;
	}

	/**
	 * @param key
	 *            The key, the name of the environment variable to return
	 * @return The value of a System environment variable in the java virtual
	 *         machine. If the key is not present, the variable is not defined
	 *         and null returned.
	 */
	public static final String getSystemProperty(String key) {
		return getSystemProperty(key, null);
	}

	/**
	 * Returns an available java.util.Locale object for the given localeCode.
	 * 
	 * The localeCode code can be case insensitive, if it is available the
	 * method will find it and return it.
	 * 
	 * @param localeCode
	 * @return java.util.Locale.
	 */
	public static Locale createLocale(String localeCode) {
		Locale resultLocale = null;
		if (localeCode != null) {
			StringTokenizer parser = new StringTokenizer(localeCode, "_"); //$NON-NLS-1$
			if (parser.countTokens() == 2) {
				resultLocale = new Locale(parser.nextToken(),
						parser.nextToken());
			} else {
				resultLocale = new Locale(localeCode);
			}
		}
		return resultLocale;
	}

}

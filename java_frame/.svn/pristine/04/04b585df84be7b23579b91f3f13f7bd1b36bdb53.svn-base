package com.ram.server.util;

import org.apache.log4j.Logger;

public class BaseLog {

	public static Logger logger = Logger.getLogger(BaseLog.class.getName());

	static {
		init();
	}

	public static void main(String args[]) {
//		BaseLog.info("FFF");
	}

	public static void init() {
		try {
			// URL filePath = BaseLog.class.getResource("/log4j.properties");
			// PropertyConfigurator.configure(filePath.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void error(String message) {

		 System.out.println(message);
//		logger.info(message);
		//
	}

	public static void error(Exception e) {
		e.printStackTrace();
		// System.out.println(e.getMessage());
		// StackTraceElement[] stacks = e.getStackTrace();
		// for (StackTraceElement stackTraceElement : stacks) {
		// logger.error("========error==========="
		// + stackTraceElement.getClassName() + "."
		// + stackTraceElement.getMethodName() + "("
		// + stackTraceElement.getLineNumber() + "):" + e.getMessage());
		// }
		// e.printStackTrace();
//		logger.info(e.getMessage());
	}

	public static void debug(String message) {
		  System.out.println(message);
//		logger.info(message);
	}

	public static void log(Exception e) {
		// System.out.println(e.getMessage());
		// logger.info(e.getMessage(), e);

		// logger.info("[" + e + "]"+System.getProperty("java.io.tmpdir"));

	}

	public static void info(String message) {
//		logger.info(message);
		// System.out.println(message);
	}

	public static void debug(String message, int level) {

	}

	@SuppressWarnings("rawtypes")
	public static void debug(Class fromClass, String message) {

	}
}

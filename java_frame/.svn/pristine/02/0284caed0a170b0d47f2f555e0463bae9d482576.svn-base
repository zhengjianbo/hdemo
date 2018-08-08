package com.ram.kettle.log;
   
public class BaseMessages  {
	static BaseMessages instance = null; 
	static {
		getInstance();
	}
	
	private BaseMessages() {
		init();
	}
	
	private void init() {
		 
	}
	
	public static BaseMessages getInstance() {
		if (instance == null) {
			instance = new BaseMessages();
		}
		return instance;
	}
	 

	public static String getString(Class<?> packageClass, String key, Object...parameters)
	{
//		String[] strings = new String[parameters.length];
//		for (int i=0;i<strings.length;i++) {
//			strings[i]=parameters[i]!=null ? parameters[i].toString() : "";
//		}
//		return getString(packageClass, key, strings);
		return key;
	}
 
}

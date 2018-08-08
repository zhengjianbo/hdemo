package com.ram.kettle.controller;

import java.util.concurrent.ConcurrentHashMap;

public class CtrlDb {
	private static final CtrlDb instanceSingle = new CtrlDb();

	public static CtrlDb getInstanceSingle() {
		return instanceSingle;
	}

	private static ConcurrentHashMap<String, DataController> map = new ConcurrentHashMap<String, DataController>();

	@SuppressWarnings("static-access")
	public CtrlDb add(String controllerKey, DataController controller) {
		if (controller != null) {
			controller.init(controllerKey.toUpperCase());
			this.map.put(controllerKey.toUpperCase(), controller);
		}
		return this;
	}

	public DataController getController(String key) {
		 
		return map.get(key.toUpperCase());
	}

}

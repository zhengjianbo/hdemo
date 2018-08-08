package com.ram.server.util;

import com.ram.kettle.controller.CtrlDb;
import com.ram.kettle.controller.DataController;
import com.ram.server.filter.InterConfig;

public final class ConfigController {
	private static final CtrlDb controllers = new CtrlDb();

	private static final ConfigController me = new ConfigController();

	public static ConfigController me() {
		return me;
	}

	private ConfigController() {
	}

	public CtrlDb getControllers() {
		return controllers;
	}

	public static DataController getController(String key) {
		return controllers.getController(key);
	}

	public static void configController(InterConfig interconfig) {
		interconfig.configHandler(controllers);
		interconfig.configDim(controllers, null);
	}

}

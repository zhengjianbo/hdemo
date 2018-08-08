package com.ram.server.filter;

import javax.servlet.ServletContext;

import com.ram.kettle.controller.CtrlDb;

/**
 * JFinalConfig.
 * <p>
 * Config order: configConstant(), configRoute(), configPlugin(),
 * configInterceptor(), configHandler()
 */
public abstract class InterConfig {
	public abstract void configHandler(CtrlDb me);

	public abstract void configDim(CtrlDb controller, Object me);

	public void setContext(ServletContext context) {

	}
}

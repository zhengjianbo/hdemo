package com.ram.server.jfinal.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.render.RenderFactory;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.BaseLog;
import com.ram.server.util.ConfigController;
import com.ram.server.util.RowUtils;

/**
 * 所有系统检测一起 性能上会降低消耗
 * 
 * 看看这个是否能放入 servlet filter中处理
 * 
 */
public class OnlyInterceptor implements Interceptor {

	public static final int EXTRA = AppConfig.EXTATTR;
	private static final RenderFactory renderFactory = RenderFactory.me();

	@Override
	public void intercept(Invocation ai) {
		Controller controller = ai.getController();

		String methodName = ai.getMethodName();// jmodal jmodald modal modald
												// umodal umodald

		controller.setAttr(AppConfig.ACTIONKEY, ai.getActionKey());
		controller.setAttr(AppConfig.CONTROLLERKEY, ai.getControllerKey());
		controller.setAttr(AppConfig.METHODNAME, methodName);
		controller.setAttr(AppConfig.URLPARA, controller.getPara());
 
		try {
			/******************************/
			ai.invoke();
			/**************************/
		} catch (ActionException e) {
			BaseLog.debug("OnlyInterceptor ActionException:" + e.getMessage());
			controller.setAttr(AppConfig.MESSAGE, "错误信息:" + e.getMessage());
			controller.render(e.getErrorRender());
		} catch (Exception e) {
			// 确定放入的是json 还是页面
			BaseLog.debug("OnlyInterceptor Exception:" + e.getMessage());
			controller.setAttr(AppConfig.MESSAGE, "系统级错误");
			controller.render(renderFactory.getErrorRender(410));
		}
	}
}

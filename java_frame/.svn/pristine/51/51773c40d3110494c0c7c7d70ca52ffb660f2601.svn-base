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
public class ValiInterceptor implements Interceptor {

	public static final int EXTRA = AppConfig.EXTATTR;
	private static final RenderFactory renderFactory = RenderFactory.me();

	@Override
	public void intercept(Invocation ai) {
		Controller controller = ai.getController();

		String methodName = ai.getMethodName();// jmodal jmodald modal modald
												// umodal umodald
 
		Boolean willVali = false;
		// jmodal jmodald modal modald umodal umodald
		if (methodName.indexOf("j") == 0 || methodName.indexOf("J") == 0) {
			willVali = true;
		} else if (methodName.indexOf("m") == 0 || methodName.indexOf("M") == 0) {
			willVali = true;
		} else if (methodName.indexOf("u") == 0 || methodName.indexOf("U") == 0) {
			willVali = true;
		}
		// 需要校验
		if (willVali) {
			BaseLog.debug("需要校验VALI:" + ai.getActionKey());
			// 需要进行校验
			String dataAction = controller.getPara(AppConfig.DATAACTION);
			if (Const.isEmpty(dataAction)) {
				dataAction = controller.getPara();
			}
			// 如果校验错误 则直接处理 不走 invoke (统一校验 校验ktr: VALI_*** (***表 dataaction))
			dataAction = AppConfig.VALI_PREFIX + dataAction;

			DataController valiController = ConfigController
					.getController(dataAction);
			if (valiController != null) {
				// 加入vali判断 modald umodald 判断是返回json 还是页面
				RequestLocal reqLocal = new RequestLocal(
						controller.getRequest());
				Boolean isVali = false;
				String message = null;

				try {
					Object rRow = valiController.getReturnRow(null,
							reqLocal.getInputMeta(), reqLocal.getInputData());

					if (rRow != null) {
						Object havVali = RowUtils.findColumnValue(rRow,
								AppConfig.RESULT); // 是否校验成功
						if ((Boolean) havVali) {
							isVali = true;
						} else {
							// 校验失败
							message = ""
									+ RowUtils.findColumnValue(rRow,
											AppConfig.MESSAGE);
						}
					}
				} catch (KettleException e) {
					message = "ERROR";
				}
				if (!isVali) {
					// 校验失败的情况下要判断跳转处理
					boolean isJson = false;
					// jmodal jmodald modal modald umodal umodald
					if (methodName.indexOf("j") == 0
							|| methodName.indexOf("J") == 0) {// j开头的
						isJson = true;
					}
					controller.setAttr(AppConfig.MESSAGE, message);
					controller.setAttr(AppConfig.RESULT, isVali);
					if (isJson) {
						controller.render(renderFactory.getErrorRender(604));
					} else {
						controller.render(renderFactory.getErrorRender(605));
					}
					return;
				}
			}
		}
		try {
			/******************************/
			ai.invoke();
			/**************************/
		} catch (ActionException e) {
			BaseLog.debug("ValiInterceptor ActionException:" + e.getMessage());
			controller.setAttr(AppConfig.MESSAGE, "错误信息:" + e.getMessage());
			controller.render(e.getErrorRender());
		} catch (Exception e) {
			// 确定放入的是json 还是页面
			BaseLog.debug("ValiInterceptor Exception:" + e.getMessage());
			controller.setAttr(AppConfig.MESSAGE, "系统级错误");
			controller.render(renderFactory.getErrorRender(410));
		}
	}
}

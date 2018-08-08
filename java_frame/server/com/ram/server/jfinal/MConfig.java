package com.ram.server.jfinal;

import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.ram.kettle.controller.CtrlDb;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.database.KettleApplication;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.util.Const;
import com.ram.server.filter.InterConfig;
import com.ram.server.util.BaseLog;
import com.ram.server.util.FinalConst;
import com.ram.task.impl.ExceptionHandlingAsyncTaskExecutor;
import com.ram.task.impl.ThreadPoolTaskExecutor;

public class MConfig extends InterConfig {

	protected Gson gson = new Gson();

	public static String ktrFolder = "ktr/";

	public static final String INITRUNKTR = "INITRUNKTR";
	public String initktr = null; // 初始化运行ktr

	public static final String QUARTZKTR = "QUARTZKTR";
	public String quartzktr = null; // 初始化运行quartz

	public static ExceptionHandlingAsyncTaskExecutor taskExecutor = null;

	public void setContext(ServletContext context) {
		initktr = context.getInitParameter(INITRUNKTR);
		quartzktr = context.getInitParameter(QUARTZKTR);

		// 读取线程池配置
		// 初始化线程池 用于异步处理 ，如发送短信以及邮件
		 loadThreadPool(context);
 
	}

	public void configHandler(CtrlDb me) {
		BaseLog.debug("INIT START");
		KettleApplication.init(FinalConst.path);

		// 加载其他类型
		loadOtherController(me);
		// 初始化一些信息

	}
	public static void loadThreadPool() {
		ThreadPoolTaskExecutor xexecutor = new ThreadPoolTaskExecutor();
		xexecutor.setThreadFactory(null);
		xexecutor.setCorePoolSize(2);
		xexecutor.setMaxPoolSize(5);
		xexecutor.setQueueCapacity(10);
		xexecutor
				.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

		xexecutor.initialize();

		taskExecutor = new ExceptionHandlingAsyncTaskExecutor(xexecutor);
	}
	public void loadThreadPool(ServletContext context) {

		ThreadPoolTaskExecutor xexecutor = new ThreadPoolTaskExecutor();
		xexecutor.setThreadFactory(null);

		String corePoolSize = context.getInitParameter("corePoolSize");
		if (!Const.isEmpty(corePoolSize)) {
			try {
				xexecutor.setCorePoolSize(Integer.parseInt(corePoolSize));
			} catch (Exception e) {
			}
		}

		String queueCapacity = context.getInitParameter("queueCapacity");
		if (!Const.isEmpty(queueCapacity)) {
			try {
				xexecutor.setQueueCapacity(Integer.parseInt(queueCapacity));
			} catch (Exception e) {
			}
		}
		String maxPoolSize = context.getInitParameter("maxPoolSize");
		if (!Const.isEmpty(maxPoolSize)) {
			try {
				xexecutor.setMaxPoolSize(Integer.parseInt(maxPoolSize));
			} catch (Exception e) {
			}
		}

		String keepAliveSeconds = context.getInitParameter("keepAliveSeconds");
		if (!Const.isEmpty(keepAliveSeconds)) {
			try {
				xexecutor.setKeepAliveSeconds(Integer
						.parseInt(keepAliveSeconds));
			} catch (Exception e) {
			}
		}

		xexecutor
				.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

		xexecutor.initialize();

		taskExecutor = new ExceptionHandlingAsyncTaskExecutor(xexecutor);

	}

	public void loadOtherController(CtrlDb me) {

	}

	@Override
	public void configDim(CtrlDb xcontroller, Object me) {
		ConstLog.message("initktr:");
		if (Const.isEmpty(initktr)) {
			return;
		}
		String[] iKtr = initktr.split(",");
		for (String ktr : iKtr) {
			if (Const.isEmpty(ktr)) {
				return;
			}
			DataController controller = xcontroller.getController(ktr);
			if (controller == null) {
				BaseLog.debug("NO FOUND LOAD INITRUNKTR:" + ktr);
				return;
			}
			RequestLocal req = new RequestLocal(1);
			req.put(0, "SID", "");
			try {
				controller.getReturnRow(null, req.getInputMeta(),
						req.getInputData());
				BaseLog.debug("LOAD INITRUNKTR:" + ktr);
			} catch (Exception e) {
				BaseLog.debug("LOAD DIM KTR ERROR:" + e.getMessage());
			}
		}
	}

}

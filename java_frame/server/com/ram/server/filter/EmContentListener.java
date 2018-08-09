package com.ram.server.filter;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.beetl.core.misc.BeetlUtil;

import com.ram.server.jfinal.AppConfig;
import com.ram.server.jfinal.MConfig;
import com.ram.server.util.BaseLog;
import com.ram.server.util.ConfigController;
import com.ram.server.util.FinalConst;

public class EmContentListener implements ServletContextListener {
	private ServletContext context = null;
	private InterConfig config = null;
	private static final ConfigController jController = ConfigController.me();

	public static String ERRINT = "";
	public static String ERR = "";
	public static String ERRCODE = "";
	public static String ERRPAGE = "";

	@SuppressWarnings("static-access")
	public void contextInitialized(ServletContextEvent event)
			throws RuntimeException {
		context = event.getServletContext();
		try {
			FinalConst.path = BeetlUtil.getWebRoot();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		BaseLog.debug("EmContentListener contextInitialized");

		ERRINT = context.getInitParameter(AppConfig.ERRINT);
		ERR = context.getInitParameter(AppConfig.ERR);
		ERRCODE = context.getInitParameter("ERRORCODE");
		ERRPAGE = context.getInitParameter("ERRORPAGE");

		try {
			FinalConst.cache = Integer.parseInt(context
					.getInitParameter(AppConfig.CACHE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		createConfig(context.getInitParameter("configClass"));

		config.setContext(context);

		jController.configController(config);
		
		

		 try {
				Future<Boolean> future = MConfig.taskExecutor
						.submit(new Callable<Boolean>() {
							public Boolean call() throws Exception { 
 								System.out.println("系统启动完成");
								return true;
							}
						});
			 } catch (Exception e) {
				 System.out.println("ERROR Exception:"+e.getMessage());
			}
		
	}

	public void createConfig(String configClass) throws RuntimeException {

		Object temp = null;
		try {
			temp = Class.forName(configClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Can not create instance of class: "
					+ configClass, e);
		}

		if (temp instanceof InterConfig)
			config = (InterConfig) temp;
		else
			throw new RuntimeException("Can not create instance of class: "
					+ configClass + ". Please check the config in web.xml");

	}

	public void contextDestroyed(ServletContextEvent event) {
		this.context = null;
	}
}
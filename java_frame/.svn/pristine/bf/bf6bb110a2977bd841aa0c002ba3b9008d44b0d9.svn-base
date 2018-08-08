package com.ram.server.jfinal;

import org.beetl.ext.jfinal.BeetlRenderFactory;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.render.ViewType;
import com.ram.kettle.util.Const;
import com.ram.server.action.ViewController;
import com.ram.server.filter.EmContentListener;
import com.ram.server.jfinal.interceptor.OnlyInterceptor;
import com.ram.server.jfinal.interceptor.ValiInterceptor;

public class AppConfig extends JFinalConfig {
	public static final String ISJSON = "ISJSON";
	public static final String SEC_CHECK_UNIT = "SEC_CHECK_UNIT";
	public static final String SEC_AUTH_COLUMN = "SEC_AUTH_COLUMN";
	public static final String LOGIN_CHECK = "LOGIN_CHECK";
	public static final String LOGIN_AUTH_COLUMN = "LOGIN_AUTH_COLUMN";
	public static final String MESSAGE = "MESSAGE";
	public static final String contextPathName = "BASE_PATH";
	public static final String DOMAIN_PATH = "DOMAIN_PATH";
	public static final String IP = "IP";
	public static final String BORWSER = "BORWSER";
	public static final String REQURI = "REQURI";
	public static final String ACTIONKEY = "actionKey";
	public static final String CONTROLLERKEY = "controllerKey";
	public static final String METHODNAME = "methodName";
	public static final String URLPARA = "urlPara";
	public static final String VALI_PREFIX = "VALI_";
	public static final String AJAX = "ajax"; // 这个可以卸载全局变量里面 方便以后修改 暂时先这样
	public static final String NEEDDIM = "DIM";

	public static final String CODE = "CODE";
	public static final String SID = "SID";
	public static final String ISADMIN = "ISADMIN";
	public static final String USER = "USER";
	public static final String TOKEN = "TOKEN";
	public static final String COOKIE_SID = "_s_sid_";
	public static final String COOKIE_TOKEN = "_s_token_";

	public static final Boolean TRUE = true;
	public static final int PAGE = 1;
	public static final int JSON = 0;

	public static final int JSONERRORPAGE = 420;
	public static final int NOFOUNDCONTROLLER = 440;
	public static final int NOFOUNDPARAM = 430;
	public static final int VIEWERRORPAGE = 410;

	public static final String DATAACTION = "action";
	public static final String DATAATTR = "data";
	public static final String STEPSEC = "STEPSEC"; // 组件权限
	public static final String STEP = "STEP"; // 组件
	public static final String RESULT = "RESULT"; // 组件权限
	public static final String FLAG = "FLAG"; // FLAG
	public static final String STEPARRAY = "STEPARRAY"; // 组件权限ARRAY
	public static final int EXTATTR = 10; //属性 request.attribute 最多10个

	public static final String CACHE = "CACHE";
	public static final String ERRINT = "ERRINT";
	public static final String ERR = "ERR";

	public static final String ERROR500 = "/uview/err.html";
	public static final String UPLOADFILE = "upfile";
	public static final String FILESAVEFOLDER = "FILESAVEFOLDER";
	public static final String FILE = "FILE"; // 数据FILE链接属性
	public static final String FILENAME = "FILENAME"; // 数据FILE链接属性
	public static String filefolder = "";

	public static final String MAPPARRAY = "MAPPARRAY"; // 组件权限ARRAY
	public static final String INPUTSTREAM = "INPUTSTREAM"; // INPUTSTREAM
	public static final String RESPONSE = "RESPONSE"; // INPUTSTREAM
	public static final String ACTION_RETURN = "_return"; // 组件权限ARRAY

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {

		PropKit.use("jconfig");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setBaseUploadPath(PropKit.get(FILESAVEFOLDER));

		me.setViewType(ViewType.OTHER);
		me.setMainRenderFactory(new BeetlRenderFactory());

		if (!Const.isEmpty(EmContentListener.ERRCODE)) {
			String[] errs = EmContentListener.ERRCODE.split(",");
			String[] errPages = EmContentListener.ERRPAGE.split(","); 
			for (int errIndex = 0; errIndex < errs.length; errIndex++) {
				me.setErrorView(Integer.parseInt(errs[errIndex]),
						errPages[errIndex]);
			}
		}
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/view", ViewController.class); // 第三个参数为该Controller的视图存放路径
		me.add("/uview", ViewController.class);
		me.add("/admin", ViewController.class);// 第三个参数为该Controller的视图存放路径

		// me.add("/", ViewController.class, "/uview");
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new OnlyInterceptor());
//		me.add(new ValiInterceptor());//校验输入参数  交由filter处理
		// me.add(new DbTransInterceptor()); //交给filter 处理
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		// ContextPathHandler path = new ContextPathHandler(contextPathName);  //交给filter 处理
		// me.add(path);
	}

	@Override
	public void configPlugin(Plugins me) {

	}
}

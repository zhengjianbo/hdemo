package com.ram.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Action;
import com.jfinal.core.JFinalExt;
import com.jfinal.render.RenderFactory;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.BaseLog;
import com.ram.server.util.ConfigController;

/**
 * 获取页面中组件的权限
 **/
public class ViewStepSecurityFilter implements Filter {

	private static final RenderFactory renderFactory = RenderFactory.me();
	private static final JFinalExt jfinal = JFinalExt.me();

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		BaseLog.debug("获取页面组件权限");
		HttpServletRequest request = (HttpServletRequest) req;
		// HttpServletResponse response = (HttpServletResponse) resp;

		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);

		try {
			if (!isSTEPSEC) {
				chain.doFilter(req, resp);
				return;
			}

			DataController controller = ConfigController.getController(STEPSEC);
			if (controller == null) {
				chain.doFilter(req, resp);
				return;
			}
			
			String[] urlPara = new String[2];
			Action action = jfinal.getAction(target, urlPara); 
			
			RequestLocal reqLocal = new RequestLocal(request, 6);
			int len = reqLocal.getExtIndexStart();

			reqLocal.put(len, AppConfig.ACTIONKEY, action.getActionKey());
			reqLocal.put(len + 1, AppConfig.CONTROLLERKEY,
					action.getControllerKey());
			reqLocal.put(len + 2, AppConfig.METHODNAME, action.getMethodName());
			reqLocal.put(len + 3, AppConfig.URLPARA, urlPara[0]);

			Object sidObject = request.getAttribute(sid);
			reqLocal.put(len + 4, sid, "" + sidObject);

			reqLocal.put(len + 5, AppConfig.ISADMIN,
					"" + request.getAttribute(AppConfig.ISADMIN));

			Object rRow = controller.getReturnRow(null,
					reqLocal.getInputMeta(), reqLocal.getInputData());

			if (rRow == null) {
				this.setAttr(request, AppConfig.MESSAGE, "没有获取详细权限");
				chain.doFilter(req, resp);
				return;
			} else {
				this.setAttr(request, AppConfig.STEPSEC, rRow);
				chain.doFilter(req, resp);
				return;
			}

		} catch (Exception e) {
			setAttr(request, AppConfig.MESSAGE, "获取详细权限发送错误:" + e.getMessage());
		}
		chain.doFilter(req, resp);
	}

	public void setAttr(HttpServletRequest request, String name, Object value) {
		request.setAttribute(name, value);
	}

	private String sid = AppConfig.SID;
	private int contextPathLength = 0;
	private String STEPSEC = null;
	boolean isSTEPSEC = false;

	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());

		STEPSEC = filterConfig.getInitParameter(AppConfig.STEPSEC);
		if (!Const.isEmpty(STEPSEC)) {
			isSTEPSEC = true;
		}
	}

	public void destroy() {
	}

}

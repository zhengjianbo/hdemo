package com.ram.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Action;
import com.jfinal.core.JFinalExt;
import com.jfinal.render.RenderFactory;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.ConfigController;
import com.ram.server.util.RowUtils;

/**
 * 权限判断
 */
public class ViewFilter implements Filter {

	private static final RenderFactory renderFactory = RenderFactory.me();
	private static final JFinalExt jfinal = JFinalExt.me();

	private String SEC_AUTH_COLUMN = null;
	private String check_sec_unit = null;

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		Object isAdmin = req.getAttribute(AppConfig.ISADMIN);
		if ((Boolean) isAdmin) {
			chain.doFilter(req, resp);
			return;
		}
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (!is_SEC_AUTH_COLUMN) {
			renderFactory.getRender(resultPage).setContext(request, response)
					.render();
			return;
		}
		if (!is_check_sec_unit) {
			renderFactory.getRender(resultPage).setContext(request, response)
					.render();
			return;
		}
		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);

		String[] urlPara = new String[2];
		Action action = jfinal.getAction(target, urlPara);

		/**************** 权限控制判断结束 *******/
		try {

			DataController controller = ConfigController
					.getController(check_sec_unit);
			if (controller == null) {
				renderFactory.getRender(resultPage)
						.setContext(request, response).render();
				return;
			}
			
			//request map 加上6个自定义参数
			RequestLocal reqLocal = new RequestLocal(request, 6);
			int len = reqLocal.getExtIndexStart();
			
			reqLocal.put(len, AppConfig.ACTIONKEY, action.getActionKey());
			reqLocal.put(len + 1, AppConfig.CONTROLLERKEY,
					action.getControllerKey());
			reqLocal.put(len + 2, AppConfig.METHODNAME, action.getMethodName());
			reqLocal.put(len + 3, AppConfig.URLPARA, urlPara[0]);

			reqLocal.put(len + 4, sid, "" + request.getAttribute(sid));
			reqLocal.put(len + 5, AppConfig.ISADMIN,
					"" + request.getAttribute(AppConfig.ISADMIN));

			Object rRow = controller.getReturnRow(null,
					reqLocal.getInputMeta(), reqLocal.getInputData());

			if (rRow == null) {
				renderFactory.getRender(resultPage)
						.setContext(request, response).render();
				return;
			} else {
				Object hadSec = RowUtils.findColumnValue(rRow, SEC_AUTH_COLUMN);
				if ((Boolean) hadSec) {
					chain.doFilter(req, resp);
					return;
				}
				Object ERRMSG = RowUtils.findColumnValue(rRow,
						AppConfig.MESSAGE);
				if (ERRMSG != null) {
					request.setAttribute(AppConfig.MESSAGE, ERRMSG + "");
				}

				if (isJson) {
					renderFactory.getJsonRender(rRow)
							.setContext(request, response).render();
					return;
				}
			}
		} catch (Exception e) {
			request.setAttribute(AppConfig.MESSAGE, "ERROR:" + e.getMessage());
		}
		renderFactory.getRender(resultPage).setContext(request, response)
				.render();
		return;

	}

	private int contextPathLength = 0;
	private final String result = AppConfig.RESULT;
	private String resultPage = ""; // 错误页面
	private String sid = AppConfig.SID;
	private String ISJSON = "N";
	private boolean is_SEC_AUTH_COLUMN = false;
	private boolean is_check_sec_unit = false;
	private boolean isJson = false;

	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());
		if (filterConfig.getInitParameter(result) != null)
			resultPage = filterConfig.getInitParameter(result);

		SEC_AUTH_COLUMN = filterConfig
				.getInitParameter(AppConfig.SEC_AUTH_COLUMN);
		check_sec_unit = filterConfig
				.getInitParameter(AppConfig.SEC_CHECK_UNIT);
		ISJSON = filterConfig.getInitParameter(AppConfig.ISJSON);

		if (!Const.isEmpty(SEC_AUTH_COLUMN)) {
			is_SEC_AUTH_COLUMN = true;
		}
		if (!Const.isEmpty(check_sec_unit)) {
			is_check_sec_unit = true;
		}
		if ("Y".equals(ISJSON)) {
			isJson = true;
		}
	}

	public void destroy() {
	}

}
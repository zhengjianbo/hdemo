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

import com.jfinal.render.RenderFactory;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.BaseLog;
import com.ram.server.util.ConfigController;
import com.ram.server.util.CookieUtils;
import com.ram.server.util.RowUtils;

/**
 * 登录情况判断
 */
public class TokenSecurityFilter implements Filter {

	private static final RenderFactory renderFactory = RenderFactory.me();

	private String LOGIN_AUTH_COLUMN = null;
	private String LOGIN_CHECK = null;
	@SuppressWarnings("unused")
	private boolean IS_LOGIN_AUTH_COLUMN = false;
	private boolean IS_LOGIN_CHECK = false;

	private final String msg = AppConfig.MESSAGE;
	private final String sid = AppConfig.SID;
	private final String CODE = AppConfig.CODE;

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		BaseLog.debug("TokenSecurityFilter doFilter ");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (!IS_LOGIN_CHECK) {
			renderFactory.getRender(resultPage).setContext(request, response)
					.render();
			return;
		}
		/**************** 权限控制判断结束 *******/
		try {

			DataController controller = ConfigController
					.getController(LOGIN_CHECK);
			if (controller == null) {
				renderFactory.getRender(resultPage)
						.setContext(request, response).render();
				return;
			}
			RequestLocal reqLocal = new RequestLocal(request,2);
			int reqLen = reqLocal.getExtIndexStart();

			String xsid = CookieUtils.getCookie(request, AppConfig.COOKIE_SID,
					"");
			String token = CookieUtils.getCookie(request,
					AppConfig.COOKIE_TOKEN, "");

			reqLocal.put(reqLen++, AppConfig.SID, xsid);
			reqLocal.put(reqLen++, AppConfig.TOKEN, token);

			Object rRow = controller.getReturnRow(null,
					reqLocal.getInputMeta(), reqLocal.getInputData());

			if (rRow == null) {
				request.setAttribute(CODE, resultPage);

				renderFactory.getRender(resultPage)
						.setContext(request, response).render();
				return;
			} else {
				Object hadLogin = RowUtils.findColumnValue(rRow,
						LOGIN_AUTH_COLUMN);
				if ((Boolean) hadLogin) {
					req.setAttribute(sid, RowUtils.findColumnValue(rRow, sid));
					req.setAttribute(AppConfig.ISADMIN,
							RowUtils.findColumnValue(rRow, AppConfig.ISADMIN));
					req.setAttribute(
							AppConfig.USER,
							RowUtils.findColumnValue(rRow,
									AppConfig.DATAATTR.toUpperCase()));
					chain.doFilter(req, resp);
					return;
				}
				request.setAttribute(msg, "登录验证失败");
				request.setAttribute(CODE, resultPage);
			}

		} catch (Exception e) {
			request.setAttribute(msg, "错误:" + e.getMessage());
			request.setAttribute(CODE, resultPage);
		}
		renderFactory.getRender(resultPage).setContext(request, response)
				.render();
		return;

	}

	@SuppressWarnings("unused")
	private int contextPathLength = 0;
	private final String result = AppConfig.RESULT;
	private String resultPage = ""; // 错误页面

	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());
		if (filterConfig.getInitParameter(result) != null)
			resultPage = filterConfig.getInitParameter(result);

		LOGIN_CHECK = filterConfig.getInitParameter(AppConfig.LOGIN_CHECK);

		LOGIN_AUTH_COLUMN = filterConfig
				.getInitParameter(AppConfig.LOGIN_AUTH_COLUMN);

		if (!Const.isEmpty(LOGIN_CHECK)) {
			IS_LOGIN_CHECK = true;
		}

		if (!Const.isEmpty(LOGIN_AUTH_COLUMN)) {
			IS_LOGIN_AUTH_COLUMN = true;
		}

	}

	public void destroy() {
	}

}

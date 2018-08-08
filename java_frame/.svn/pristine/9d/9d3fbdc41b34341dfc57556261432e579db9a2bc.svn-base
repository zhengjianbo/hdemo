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
import com.ram.kettle.log.KettleException;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.ConfigController;
import com.ram.server.util.RowUtils;

/**
 * 数据请求参数 校验值是否符合要求
 */
public class ValiFilter implements Filter {

	private static final RenderFactory renderFactory = RenderFactory.me();
	private static final JFinalExt jfinal = JFinalExt.me();

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);

		// 哪些需要校验 由filter控制
		try {

			String[] urlPara = new String[2];
			Action action = jfinal.getAction(target, urlPara);
			
			String dataAction = req.getParameter(AppConfig.DATAACTION);
			if (Const.isEmpty(dataAction)) {
				dataAction = urlPara[0];
			}
			DataController valiController = ConfigController
					.getController(AppConfig.VALI_PREFIX + dataAction);
			if (valiController == null) {
				chain.doFilter(req, resp);// 不验证
				return;
			}
			RequestLocal reqLocal = new RequestLocal(request,4);
			int len=reqLocal.getExtIndexStart();
			reqLocal.put(len, AppConfig.ACTIONKEY, action.getActionKey());
			reqLocal.put(len + 1, AppConfig.CONTROLLERKEY,
					action.getControllerKey());
			reqLocal.put(len + 2, AppConfig.METHODNAME, action.getMethodName());
			reqLocal.put(len + 3, AppConfig.URLPARA, urlPara[0]);
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
			if (isVali) {
				chain.doFilter(req, resp); //校验成功
				return;
			} else { 
				request.setAttribute(AppConfig.MESSAGE, message);
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
	private String resultPage = ""; // 错误返回页面 (JSON ，提示页面) 
 
	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());
		if (filterConfig.getInitParameter(result) != null)
			resultPage = filterConfig.getInitParameter(result);
 
	}

	public void destroy() {
	}

}
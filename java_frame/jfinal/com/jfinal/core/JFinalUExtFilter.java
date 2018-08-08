/*
 *
 ** uview下控制
 */

package com.jfinal.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.ram.kettle.util.Const;
import com.ram.server.util.BaseLog;

/**
 * JFinal framework filter
 */
public final class JFinalUExtFilter implements Filter { 
	private Handler handler;
	private String encoding;
	private static final JFinalExt jfinal = JFinalExt.me();
	private static Log log;
	private int contextPathLength;
	private String forbidden = null;
	private boolean isForbidden = false;

	public void init(FilterConfig filterConfig) throws ServletException {
		forbidden = filterConfig.getInitParameter("RESULT");
		if (!Const.isEmpty(forbidden)) {
			isForbidden = true;
		}
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		BaseLog.debug("UJFinal Ext do filter ");
		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);

		if (target.indexOf('.') != -1) {
			chain.doFilter(req, res);
			return;
		}
		String[] urlPara = new String[2];
		Action action = jfinal.getAction(target, urlPara);
		boolean[] isHandled = { false };
		if (urlPara[0].toUpperCase().indexOf("GET") == 0
				|| urlPara[0].toUpperCase().indexOf("LOGIN") == 0) {
			try {
				if (handler == null) {
					handler = jfinal.getHandler();
				}
				handler.handle(target, request, response, isHandled);
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					String qs = request.getQueryString();
					log.error(qs == null ? target : target + "?" + qs, e);
				}
			}
		}
		if (isHandled[0] == false) {
			// 不符合jfinal 条件 报错误
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(forbidden);
			dispatcher.forward(request, response);
		}
	}

	public void destroy() {

	}

	static void initLog() {
		log = Log.getLog(JFinalUExtFilter.class);
	}
}

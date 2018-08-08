package com.ram.server.filter;

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

import com.ram.kettle.util.Const;
import com.ram.server.util.BaseLog;

/**
 * 允许访问
 */
public class UViewFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		BaseLog.debug("UViewFilter执行");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (isForbidden) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(forbidden);
			dispatcher.forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
		return;

	}

	@SuppressWarnings("unused")
	private int contextPathLength = 0;
	private String forbidden = null;
	private boolean isForbidden = false;

	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());

		forbidden = filterConfig.getInitParameter("RESULT");
		if (!Const.isEmpty(forbidden)) {
			isForbidden = true;
		}

	}

	public void destroy() {
	}

}
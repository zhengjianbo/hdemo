/**
 * Copyright (c) 2011-2016, James Zhan 瑭规尝 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public final class JFinalExtFilter implements Filter {

	private Handler handler;
	private String encoding;
	private JFinalConfig jfinalConfig;
	private Constants constants;
	private static final JFinalExt jfinal = JFinalExt.me();
	private static Log log;
	private int contextPathLength;
	private String forbidden = null;
	private boolean isForbidden = false;

	public void init(FilterConfig filterConfig) throws ServletException {

		createJFinalConfig(filterConfig.getInitParameter("configClass"));

		if (jfinal.init(jfinalConfig, filterConfig.getServletContext()) == false)
			throw new RuntimeException("JFinal init error!");

		handler = jfinal.getHandler();
		constants = Config.getConstants();
		encoding = constants.getEncoding();
		jfinalConfig.afterJFinalStart();

		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());
		forbidden = filterConfig.getInitParameter("RESULT");
		if (!Const.isEmpty(forbidden)) {
			isForbidden = true;
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setCharacterEncoding(encoding);
		BaseLog.debug("JFinal Ext do filter ");
		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);

		if (target.indexOf('.') != -1) {
			chain.doFilter(req, res);
			return;
		}

		boolean[] isHandled = { false };
		try {
			handler.handle(target, request, response, isHandled);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				String qs = request.getQueryString();
				log.error(qs == null ? target : target + "?" + qs, e);
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
		jfinalConfig.beforeJFinalStop();
		jfinal.stopPlugins();
	}

	private void createJFinalConfig(String configClass) {
		if (configClass == null)
			throw new RuntimeException(
					"Please set configClass parameter of JFinalFilter in web.xml");

		Object temp = null;
		try {
			temp = Class.forName(configClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Can not create instance of class: "
					+ configClass, e);
		}

		if (temp instanceof JFinalConfig)
			jfinalConfig = (JFinalConfig) temp;
		else
			throw new RuntimeException("Can not create instance of class: "
					+ configClass + ". Please check the config in web.xml");
	}

	static void initLog() {
		log = Log.getLog(JFinalExtFilter.class);
	}
}

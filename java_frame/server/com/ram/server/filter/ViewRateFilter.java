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

import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.ram.kettle.util.Const;
import com.ram.server.util.BaseLog;

/**
 * view 访问 频率限制
 
 */
public final class ViewRateFilter implements Filter {
  
	private int contextPathLength=0;
	private String xforbidden = null;
	private String jforbidden = null;
	private boolean isForbidden = false;

	public void init(FilterConfig filterConfig) throws ServletException {
		  xforbidden = filterConfig.getInitParameter("RESULT");
		  jforbidden = filterConfig.getInitParameter("RESULT_JSON");
		if (!Const.isEmpty(xforbidden)||!Const.isEmpty(jforbidden)) {
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
 
		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);
		//执行频率判断
		if (isForbidden) {
			//通过调用程序来判断 这样系统灵活度加大
			 
			//forbidden 生成内容模板
			String forbidden=xforbidden;
			 //判断访问频率
			 //如果超过一定频率 需要做限制
			 // /*/j*/* 这种格式的限制 返回JSON ,非此种访问返回页面 
			 if(target.indexOf("/j/")>-1){
				 //uview admin view  /*/jmodal/这种格式的是json请求 就是限制的话返回JSON
				 forbidden=jforbidden;
			 }
			 //不跳转  直接显示内容
			//RequestDispatcher dispatcher = request
			//		.getRequestDispatcher(forbidden);
			//dispatcher.forward(request, response);
		} else {
			chain.doFilter(request, response);
		} 
	}

	public void destroy() {

	} 
}

package com.ram.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ram.server.jfinal.AppConfig;

/**
 * 采用Filter统一处理字符集.
 * 
 * @author Summer
 */
public class CharsetEncodingFilter implements Filter {
	private String encoding = "UTF-8";
	protected FilterConfig filterConfig;
	private String contextPath = "";
	private String basePath = "";

	private boolean isFirst = true;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		if (filterConfig.getInitParameter("encoding") != null)
			encoding = filterConfig.getInitParameter("encoding");
		contextPath = filterConfig.getServletContext().getContextPath();
	}

	public void doFilter(ServletRequest srequset, ServletResponse sresponse,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequset;
		request.setCharacterEncoding(encoding);

		if (isFirst) {
			basePath = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort() + contextPath + "/";
			isFirst = false;
		}
		srequset.setAttribute(AppConfig.contextPathName, contextPath);

		srequset.setAttribute(AppConfig.DOMAIN_PATH, basePath);

		srequset.setAttribute(AppConfig.IP, request.getRemoteAddr()); 

		filterChain.doFilter(srequset, sresponse);
	}

	public void destroy() {
		this.encoding = null;
	}
}

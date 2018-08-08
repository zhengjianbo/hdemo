package com.ram.server.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ram.kettle.database.DSTransactionManager;
import com.ram.kettle.util.Const;
import com.ram.server.util.BaseLog;

/**
 * 允许访问的情况 压缩 并直接输出流
 */
public class XViewFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
 
		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);
		
		BaseLog.debug("XViewFilter doFilter :"+target);
		
		if (isDbManger) {
			// 启用事务
			DSTransactionManager.start();
		}
		if (isGzip && isGZipEncoding(request)) {
			BufferResponse myresponse = new BufferResponse(response);
			chain.doFilter(request, myresponse);

			if (isDbManger) {
				// 启用事务
				try {
					DSTransactionManager.commit();
				} catch (Exception e) {

				}
			}
			// 拿出缓存中的数据，压缩后再打给浏览器
			byte out[] = myresponse.getBuffer();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			// 压缩输出流中的数据
			GZIPOutputStream gout = new GZIPOutputStream(bout);
			gout.write(out);
			gout.close();
			byte gzip[] = bout.toByteArray();
			response.setHeader("content-encoding", "gzip");
			response.setContentLength(gzip.length);
			response.getOutputStream().write(gzip);
			response.getOutputStream().close();
			return;
		} else {
			BufferResponse myresponse = new BufferResponse(response);
			chain.doFilter(request, myresponse);
			if (isDbManger) {
				// 启用事务
				try {
					DSTransactionManager.commit();
				} catch (Exception e) {
				}
			}
			byte out[] = myresponse.getBuffer();
			response.getOutputStream().write(out);
			response.getOutputStream().close();
		}
	}

	private int contextPathLength = 0;
	private boolean isDbManger = false;
	private boolean isGzip = true;

	public void init(FilterConfig filterConfig) throws ServletException {
		String contextPath = filterConfig.getServletContext().getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0
				: contextPath.length());

		String DBMANAGER = filterConfig.getInitParameter("DBMANAGER");
		if (!Const.isEmpty(DBMANAGER) && DBMANAGER.equals("Y")) {
			isDbManger = true;
		}

		String gzip = filterConfig.getInitParameter("GZIP");
		if (!Const.isEmpty(gzip) && gzip.equals("N")) {
			isGzip = false;
		}
	}

	/**
	 * 判断浏览器是否支持GZIP
	 * 
	 * @param request
	 * @return
	 */
	private static boolean isGZipEncoding(HttpServletRequest request) {
		boolean flag = false;
		String encoding = request.getHeader("Accept-Encoding");
		if(Const.isEmpty(encoding)){
			return false;
		}
		if (encoding.indexOf("gzip") != -1) {
			flag = true;
		}
		return flag;
	}

	public void destroy() {
	}

}
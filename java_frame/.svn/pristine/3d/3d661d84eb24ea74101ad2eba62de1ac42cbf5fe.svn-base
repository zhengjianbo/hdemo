package com.ram.server.jfinal.handler;

import java.io.Writer;

import org.beetl.core.ConsoleErrorHandler;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;

import com.jfinal.core.ActionException;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;
import com.ram.server.util.BaseLog;

public class BeetlErrorHandler extends ConsoleErrorHandler {
	private static final RenderFactory renderFactory = RenderFactory.me();

	@Override
	public void processExcption(BeetlException e, Writer writer) {
		// 判断是不是开发者模式,如果不是调用父类方法(默认输出控制台)
		if (!Boolean.valueOf(e.gt.getConf().getProperty("ERROR_HANDLER_SUPER"))) {
			super.processExcption(e, writer);
		}
		ErrorInfo error = new ErrorInfo(e);
		StringBuilder title = new StringBuilder();
		StringBuilder msg = new StringBuilder();

		if (error.getErrorCode().equals(BeetlException.CLIENT_IO_ERROR_ERROR)) {
			// 不输出详细提示信息
			title = new StringBuilder(">>").append("客户端IO异常:").append(
					e.resourceId);
			if (e.getCause() != null) {
				msg.append(e.getCause());
			}
		} else {

			int line = error.getErrorTokenLine();

			title = new StringBuilder(">>").append(error.getType()).append(":")
					.append(error.getErrorTokenText()).append(" 位于")
					.append(line).append("行").append(" 资源:")
					.append(e.resourceId);
		}
		int errCode = 440;
		
		if (error.getErrorCode().equals(BeetlException.CLIENT_IO_ERROR_ERROR)) {
			errCode = 410;
		} 
		if (error.getErrorCode().equals(BeetlException.TEMPLATE_LOAD_ERROR)) {
			errCode = 404;
		}
		BaseLog.error("BEETL 模板语言发生错误:\r\n错误代码: " + error.getErrorCode()
				+ "\r\n 标题:" + title.toString() + "\r\n错误内容:" + msg.toString());
		if (!error.getErrorCode().equals(BeetlException.NATIVE_CALL_EXCEPTION)) {
			//非assets错误的才显示错误页面
			Render render = renderFactory.getErrorRender(errCode); 
	 		throw new ActionException(errCode, render);
		}

	}
}

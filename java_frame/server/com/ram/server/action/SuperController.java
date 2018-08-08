package com.ram.server.action;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.render.RenderFactory;
import com.ram.server.jfinal.AppConfig;

public class SuperController extends Controller {
	private static final RenderFactory renderFactory = RenderFactory.me();

	public void renderFile(String fileURI, String fileName) {
		render(renderFactory.getFileRender(fileURI));
	}

	public void render(String view) {
		//如果有#号
		if (view.indexOf(".html") < 0) {
			view = view + ".html";
		}
		
		String ajax=this.getRequest().getParameter(AppConfig.AJAX);
		if(!StringUtils.isEmpty(ajax)){
			view=view+"#"+ajax;
		} 
		render(renderFactory.getRender(view));
	}

	public Controller keepParaExtAttr() {
		HttpServletRequest request = this.getRequest();
		Map<String, String[]> map = this.getRequest().getParameterMap();
		
		for (Entry<String, String[]> e : map.entrySet()) {
			String[] values = e.getValue();
			if(e.getKey().equalsIgnoreCase(AppConfig.SID)){
				 continue;//sid不允许传入
			}
			if (request.getAttribute(e.getKey()) == null) { 
				if (values.length >= 1)
					request.setAttribute(e.getKey(), values[0]);
				else
					request.setAttribute(e.getKey(), values);
			}
		}
		return this;
	}
}

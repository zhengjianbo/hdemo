package com.jfinal.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfinal.config.Constants;
import com.jfinal.aop.Invocation;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderFactory;

/**
 * ActionHandler
 */
final class ActionExtHandler extends Handler {

	private final boolean devMode;
	private final ActionMapping actionMapping;
	private static final RenderFactory renderFactory = RenderFactory.me();
	private static final Log log = Log.getLog(ActionHandler.class);

	public ActionExtHandler(ActionMapping actionMapping, Constants constants) {
		this.actionMapping = actionMapping;
		this.devMode = constants.getDevMode();
	}

	/**
	 * handle 1: Action action = actionMapping.getAction(target) 2: new
	 * Invocation(...).invoke() 3: render(...)
	 */
	public final void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		if (target.indexOf('.') != -1) {
			return;
		}

		isHandled[0] = true;
		String[] urlPara = { null };
		Action action = actionMapping.getAction(target, urlPara);

		if (action == null) {
			if (log.isWarnEnabled()) {
				String qs = request.getQueryString();
				log.warn("404 Action Not Found: "
						+ (qs == null ? target : target + "?" + qs));
			}
			renderFactory.getErrorRender(404).setContext(request, response)
					.render();
			return;
		}

		try {
			Controller controller = action.getControllerClass().newInstance();
			controller.init(request, response, urlPara[0]);

			if (devMode) {
				if (ActionReporter.isReportAfterInvocation(request)) {
					new Invocation(action, controller).invoke();
					ActionReporter.report(controller, action);
				} else {
					ActionReporter.report(controller, action);
					new Invocation(action, controller).invoke();
				}
			} else {
				new Invocation(action, controller).invoke();
			}

			Render render = controller.getRender();
			if (render instanceof ActionRender) {
				String actionUrl = ((ActionRender) render).getActionUrl();
				if (target.equals(actionUrl))
					throw new RuntimeException(
							"The forward action url is the same as before.");
				else
					handle(actionUrl, request, response, isHandled);
				return;
			}

			if (render == null)
				render = renderFactory.getDefaultRender(action.getViewPath()
						+ action.getMethodName());
			render.setContext(request, response, action.getViewPath()).render();
		} catch (RenderException e) {
			if (log.isErrorEnabled()) {
				String qs = request.getQueryString();
				log.error(qs == null ? target : target + "?" + qs);
			}
		} catch (ActionException e) {
			int errorCode = e.getErrorCode();
			if (errorCode == 404 && log.isWarnEnabled()) {
				String qs = request.getQueryString();
				log.warn("404 Not Found: "
						+ (qs == null ? target : target + "?" + qs));
			} else if (errorCode == 401 && log.isWarnEnabled()) {
				String qs = request.getQueryString();
				log.warn("401 Unauthorized: "
						+ (qs == null ? target : target + "?" + qs));
			} else if (errorCode == 403 && log.isWarnEnabled()) {
				String qs = request.getQueryString();
				log.warn("403 Forbidden: "
						+ (qs == null ? target : target + "?" + qs));
			} else if (log.isErrorEnabled()) {
				String qs = request.getQueryString();
				log.error(qs == null ? target : target + "?" + qs);
			}
			e.getErrorRender()
					.setContext(request, response, action.getViewPath())
					.render();
		} catch (Throwable t) {
			if (log.isErrorEnabled()) {
				String qs = request.getQueryString();
				log.error(qs == null ? target : target + "?" + qs, t);
			}
			renderFactory.getErrorRender(500)
					.setContext(request, response, action.getViewPath())
					.render();
		}
	}
}

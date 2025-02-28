package com.jfinal.core;

import java.util.List;

import javax.servlet.ServletContext;

import com.jfinal.config.Constants;
import com.jfinal.config.JFinalConfig;
import com.jfinal.handler.Handler;
import com.jfinal.handler.HandlerFactory;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.render.RenderFactory;
import com.jfinal.server.IServer;
import com.jfinal.server.ServerFactory;
import com.jfinal.token.ITokenCache;
import com.jfinal.token.TokenManager;
import com.jfinal.upload.OreillyCos;

/**
 * JFinal
 */
public final class JFinalExt {

	private Constants constants;
	private ActionMapping actionMapping;
	private Handler handler;
	private ServletContext servletContext;
	private String contextPath = "";
	private static IServer server;

	private static final JFinalExt me = new JFinalExt();

	private JFinalExt() {
	}

	public static JFinalExt me() {
		return me;
	}

	boolean init(JFinalConfig jfinalConfig, ServletContext servletContext) {
		this.servletContext = servletContext;
		this.contextPath = servletContext.getContextPath();

		initPathUtil();

		Config.configJFinal(jfinalConfig); // start plugin and init log factory
											// in this method
		constants = Config.getConstants();

		initActionMapping();
		initHandler();
		initRender();
		initOreillyCos();
		initTokenManager();

		return true;
	}

	private void initTokenManager() {
		ITokenCache tokenCache = constants.getTokenCache();
		if (tokenCache != null)
			TokenManager.init(tokenCache);
	}

	private void initHandler() {
		Handler actionHandler = new ActionExtHandler(actionMapping, constants);
		handler = HandlerFactory.getHandler(Config.getHandlers()
				.getHandlerList(), actionHandler);
	}

	private void initOreillyCos() {
		OreillyCos.init(constants.getBaseUploadPath(),
				constants.getMaxPostSize(), constants.getEncoding());
	}

	private void initPathUtil() {
		String path = servletContext.getRealPath("/");
		PathKit.setWebRootPath(path);
	}

	private void initRender() {
		RenderFactory.me().init(constants, servletContext);
	}

	private void initActionMapping() {
		actionMapping = new ActionMapping(Config.getRoutes(),
				Config.getInterceptors());
		actionMapping.buildActionMapping();
		Config.getRoutes().clear();
	}

	void stopPlugins() {
		List<IPlugin> plugins = Config.getPlugins().getPluginList();
		if (plugins != null) {
			for (int i = plugins.size() - 1; i >= 0; i--) { // stop plugins
				boolean success = false;
				try {
					success = plugins.get(i).stop();
				} catch (Exception e) {
					success = false;
					LogKit.error(e.getMessage(), e);
				}
				if (!success) {
					System.err.println("Plugin stop error: "
							+ plugins.get(i).getClass().getName());
				}
			}
		}
	}

	Handler getHandler() {
		return handler;
	}

	public Constants getConstants() {
		return Config.getConstants();
	}

	public String getContextPath() {
		return contextPath;
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	public Action getAction(String url, String[] urlPara) {
		return actionMapping.getAction(url, urlPara);
	}

	public List<String> getAllActionKeys() {
		return actionMapping.getAllActionKeys();
	}

	public static void start() {
		server = ServerFactory.getServer();
		server.start();
	}

	public static void start(String webAppDir, int port, String context,
			int scanIntervalSeconds) {
		server = ServerFactory.getServer(webAppDir, port, context,
				scanIntervalSeconds);
		server.start();
	}

	public static void stop() {
		server.stop();
	}

	/**
	 * Run JFinal Server with Debug Configurations or Run Configurations in
	 * Eclipse JavaEE args example: WebRoot 80 / 5
	 */
	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			server = ServerFactory.getServer();
			server.start();
		} else {
			String webAppDir = args[0];
			int port = Integer.parseInt(args[1]);
			String context = args[2];
			int scanIntervalSeconds = Integer.parseInt(args[3]);
			server = ServerFactory.getServer(webAppDir, port, context,
					scanIntervalSeconds);
			server.start();
		}
	}
}

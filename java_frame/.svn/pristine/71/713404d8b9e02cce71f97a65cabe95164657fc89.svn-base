package com.ram.server.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.jfinal.render.CaptchaRenderExt;
import com.jfinal.upload.UploadFile;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.BaseLog;
import com.ram.server.util.ConfigController;
import com.ram.server.util.RowUtils;
import com.ram.server.util.VerifyCodeUtils;

public class ViewController extends SuperController {

	protected static Gson gson = new Gson();

	public static final int EXTRA = AppConfig.EXTATTR;

	public void page() {
		this.keepParaExtAttr();
		String urlPara = this.getPara();
		if (Const.isEmpty(urlPara)) {
			this.renderError(404);
			return;
		}
		render(urlPara);
	}

	/* JSON 输出 */
	public void jmodal() {

		modal_action(true, false);
	}

	/* JSON 输出 需要数据库事务 */
	public void jmodald() {
		modal_action(true, false);
	}

	public void modal() {
		modal_action(false, false);
	}

	public void umodal() {
		modal_action(false, false);
	}

	/* 需要详细控件权限 需要数据库事务 */
	public void modald() {
		modal_action(false, false);
	}

	/* 不需要详细控件权限 需要数据库事务 */
	public void umodald() {
		modal_action(false, false);
	}

	public void modal_action(boolean isJson, boolean needMore) {
		this.keepParaExtAttr();
		String urlPara = this.getPara();
		BaseLog.debug(this.getRequest().getServletPath());

		if (Const.isEmpty(urlPara)) {
			if (isJson) {
				this.renderError(401);
			} else {
				this.renderError(402);
			}
			return;
		}
		try {
			String dataAction = this.getPara(AppConfig.DATAACTION);
			if (Const.isEmpty(dataAction)) {
				dataAction = urlPara;
			}

			DataController controller = ConfigController
					.getController(dataAction);
			if (controller == null) {
				if (isJson) {
					this.renderError(404);
				} else {
					this.renderError(405);
				}
				return;
			}

			RequestLocal req = new RequestLocal(this.getRequest(), 3);
			int reqLen = req.getExtIndexStart();
			if (needMore) {
				Map<String, String> params = new HashMap<String, String>();
				Map requestParams = this.getRequest().getParameterMap();
				for (Iterator iter = requestParams.keySet().iterator(); iter
						.hasNext();) {
					String name = (String) iter.next();
					String[] values = (String[]) requestParams.get(name);
					String valueStr = "";
					for (int i = 0; i < values.length; i++) {
						valueStr = (i == values.length - 1) ? valueStr
								+ values[i] : valueStr + values[i] + ",";
					}
					if ("action".equalsIgnoreCase(name)) {
						continue;
					}
					params.put(name, valueStr);
				}

				req.put(reqLen++, AppConfig.MAPPARRAY, params);

				InputStream inputStream;
				StringBuffer sb = new StringBuffer();
				inputStream = this.getRequest().getInputStream();
				String s;
				BufferedReader in = new BufferedReader(new InputStreamReader(
						inputStream, "UTF-8"));
				while ((s = in.readLine()) != null) {
					sb.append(s);
				}
				in.close();
				inputStream.close();

				req.put(reqLen++, AppConfig.INPUTSTREAM, sb.toString());
				req.put(reqLen++, AppConfig.RESPONSE, this.getResponse());
			}
			Object rRow = controller.getReturnRow(dataAction,
					req.getInputMeta(), req.getInputData());
			if (rRow == null) {
				if (isJson) {
					this.renderError(406);
				} else {
					this.renderError(407);
				}
				return;
			} else {
				if (isJson) {
					this.renderJson(rRow);
					return;
				} else {
					this.setAttr(AppConfig.DATAATTR, rRow);
				}
			}
		} catch (Exception e) {
			BaseLog.error(e);
			BaseLog.debug("ERRMSG:" + e.getMessage());
			this.setAttr(AppConfig.MESSAGE, e.getMessage());

			if (isJson) {
				this.renderError(406);
			} else {
				this.renderError(405);
			}
			return;
		}
		render(urlPara);
	}

	/**
	 * 下载文件
	 */
	public void downfile() {
		String urlPara = this.getPara();
		if (Const.isEmpty(urlPara)) {
			this.renderError(AppConfig.VIEWERRORPAGE);
			return;
		}
		DataController controller = ConfigController.getController(urlPara);
		if (controller == null) {
			this.renderError(AppConfig.VIEWERRORPAGE);
			return;
		}
		try {
			RequestLocal req = new RequestLocal(this.getRequest());
			Object rRow = controller.getReturnRow(null, req.getInputMeta(),
					req.getInputData());
			Object hadFile = RowUtils.findColumnValue(rRow, AppConfig.RESULT);
			if ((Boolean) hadFile) {
				Object flag = RowUtils.findColumnValue(rRow, AppConfig.FLAG);
				if ((Long) flag == 131201L) {
					Object fileName = RowUtils.findColumnValue(rRow,
							AppConfig.FILE);
					Object fileOutName = RowUtils.findColumnValue(rRow,
							AppConfig.FILENAME);
					this.renderFile(fileName + "", fileOutName + "");
					return;
				}
			}
			Object code = RowUtils.findColumnValue(rRow, AppConfig.CODE);
			this.setAttr(AppConfig.CODE, code);
			Object message = RowUtils.findColumnValue(rRow, AppConfig.MESSAGE);
			this.setAttr(AppConfig.MESSAGE, message);
			this.renderError(AppConfig.VIEWERRORPAGE);

		} catch (Exception e) {
			BaseLog.log(e);
			this.setAttr(AppConfig.CODE, -1);
			this.setAttr(AppConfig.MESSAGE, "获取下载文件失败");
			this.renderError(AppConfig.VIEWERRORPAGE);
		}

	}

	public void AuthImageByKtr(String ktrKey) {
		HttpServletResponse response = this.getResponse();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		DataController controller = ConfigController.getController(ktrKey);
		if (controller == null) {
			this.renderError(AppConfig.VIEWERRORPAGE);
			return;
		}
		try {
			RequestLocal req = new RequestLocal(this.getRequest());
			Object row = controller.getReturnRow(null, req.getInputMeta(),
					req.getInputData());
			Object hadVali = RowUtils.findColumnValue(row, AppConfig.RESULT);
			if ((Boolean) hadVali) {
				String verifyCode = (String) RowUtils.findColumnValue(row,
						AppConfig.CODE);
				// 生成图片
				int w = 100, h = 30;
				VerifyCodeUtils.outputImage(w, h, response.getOutputStream(),
						verifyCode);
				return;
			}
		} catch (Exception e) {
			// 生成错误代码
		}
		this.renderError(AppConfig.VIEWERRORPAGE);
		return;
	}

	/**
	 * 验证码
	 * 
	 * @throws IOException
	 * 
	 */
	public void AuthImage() throws IOException {
		String urlPara = this.getPara();
		if (Const.isEmpty(urlPara)) {
			this.renderError(AppConfig.VIEWERRORPAGE);
			return;
		}
		CaptchaRenderExt img = new CaptchaRenderExt();
		img.setRnd("zzzz");
		render(img);
	}

	public void uploadFile() {
		uploadFile(true);
	}

	public void uploadFile(boolean isJson) {
		String urlPara = this.getPara();
		if (Const.isEmpty(urlPara)) {

			if (isJson) {
				this.renderJson("ERROR", "1");
			} else {
				this.renderError(AppConfig.JSONERRORPAGE);
			}
			return;
		}
		DataController controller = ConfigController.getController(urlPara);
		if (controller == null) {
			this.renderError(AppConfig.JSONERRORPAGE);
			return;
		}
		try {
			UploadFile files = getFile(getPara(AppConfig.UPLOADFILE),
					AppConfig.filefolder);
			RequestLocal req = new RequestLocal(this.getRequest(), 5);

			int reqLen = req.getExtIndexStart();
			req.put(reqLen++, "parameterName", files.getParameterName());
			req.put(reqLen++, "uploadPath", files.getUploadPath());
			req.put(reqLen++, "fileName", files.getFileName());
			req.put(reqLen++, "originalFileName", files.getOriginalFileName());
			req.put(reqLen++, "contentType", files.getContentType());
			Object row = controller.getReturnRow(null, req.getInputMeta(),
					req.getInputData());

			if (isJson) {
				this.renderJson(row);
			} else {
				this.render(urlPara);
			}
		} catch (Exception e) {
			BaseLog.log(e);
			if (isJson) {
				this.renderJson("ERROR", "1");
			} else {
				this.renderError(AppConfig.JSONERRORPAGE);
			}
		}
	}

	// 加载ktr
	public void loadTrans() {
		loadTrans(true);
	}

	public void loadTrans(boolean isJson) {
		String urlPara = this.getPara();
		if (Const.isEmpty(urlPara)) {
			this.renderError(AppConfig.JSONERRORPAGE);
			return;
		}

		// MConfig.add(configer.getControllers(), urlPara,
		// EmContentListener.path
		// + "/" + MConfig.ktrFolder + urlPara + ".ktr", true);
		// BaseLog.debug("fileurl:" + EmContentListener.path + "/"
		// + MConfig.ktrFolder + urlPara + ".ktr");
		// this.renderText("LOAD TRANS OK,ktrFolder");
	}

}

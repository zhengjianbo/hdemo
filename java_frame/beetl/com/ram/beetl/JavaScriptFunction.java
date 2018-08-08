package com.ram.beetl;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import com.google.gson.Gson;
import com.jfinal.render.CaptchaRenderExt;
import com.ram.server.jfinal.MConfig;

public class JavaScriptFunction {

	Gson gson = new Gson();
	public String getJsonToString(Object paras) {
		try { 
			 return gson.toJson(paras);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object getJavaScript(Object paras) {
		try { 
			 if (paras.getClass().isArray()) {
				JSONArray jsonObject = JSONArray.fromObject(paras);
				return jsonObject;
			} else if (paras.getClass().getName().equals(HashMap.class.getName())) {
				  return JSONObject.fromObject(gson.toJson(paras));
			} else {
				Object json = new JSONTokener(paras + "").nextValue();
				if (json instanceof JSONObject) {
					return (JSONObject) json;
				} else if (json instanceof JSONArray) {
					return (JSONArray) json;
				}
//				return JSONObject.fromObject(paras);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean sendSms(final String mobile, final String message) {
		// 发送短信 异步
		Future<Boolean> future = MConfig.taskExecutor
				.submit(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						System.out.println("mobile:" + mobile + "," + message);
						return true;
					}
				});

		// future加入队列 防止执行或者等待超时

		return true;
	}

	public boolean sendMail(final String email, final String message) {
		// 发送邮件 异步
		Future<Boolean> future = MConfig.taskExecutor
				.submit(new Callable<Boolean>() {
					public Boolean call() throws Exception {
						System.out.println("email:" + email + "," + message);
						return true;
					}
				});
		return true;
	}

	private static final String[] strArr = CaptchaRenderExt.strArr;

	/**
	 * 生成验证码
	 * 
	 */
	public String getValiCode() {
		return getValiCode(4);
	}

	public String getValiCode(int num) {
		String sRand = "";
		Random random = new Random();
		for (int i = 0; i < num; i++) {
			String rand = String.valueOf(strArr[random.nextInt(strArr.length)]);
			sRand += rand;
		}
		return sRand;
	}

	public static void main(String[] args) {
		MConfig.loadThreadPool();

		JavaScriptFunction jF = new JavaScriptFunction();
		for (int i = 0; i < 100; i++) {
			jF.sendSms("mobile", "message");
		}
		Object t = jF.getJavaScript(new String[] { "1", "f" });
		// System.out.println(t + "");
	}
}

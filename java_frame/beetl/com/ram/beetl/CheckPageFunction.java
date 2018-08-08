package com.ram.beetl;

import net.sf.json.JSONObject;

import org.beetl.core.Context;
import org.beetl.core.Function;

import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.BaseLog;

/***
 * 校验data flag 正常否
 */
public class CheckPageFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		if (Const.isEmpty(paras)) {
			ctx.set(AppConfig.CODE, -1);
			return false;
		}
		Object flag = paras[0];
		if (flag == null) {
			ctx.set(AppConfig.CODE, -2);
			return false;
		}
		Object url = ctx.getGlobal(AppConfig.URLPARA);
		Object data = ctx.getGlobal(AppConfig.DATAATTR);
		if (data == null) {
			ctx.set(AppConfig.CODE, 01);
			return false;
		}
		if (url == null) {
			ctx.set(AppConfig.CODE, 02);
			return false;
		}
		try {
			JSONObject securityObject = JSONObject.fromObject(data);

			Object resultObj = securityObject.get(AppConfig.RESULT);

//			if (resultObj == null || !(Boolean) resultObj) { 
//				ctx.set(AppConfig.CODE, 03);
//				return false;
//			}

			Object flagObj = securityObject.get(AppConfig.FLAG);
			if (flagObj == null) {

				ctx.set(AppConfig.CODE, 04);
				return false;
			}
			if (Const.compare(flagObj, flag)) {
				return true;
			}
			ctx.set(AppConfig.CODE, 05);

		} catch (Exception e) {
			ctx.set(AppConfig.CODE, 06);
			BaseLog.debug("CheckPageFunction ERROR:" + e.getMessage());
		}
		return false;
	}

}

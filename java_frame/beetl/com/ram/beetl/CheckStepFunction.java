package com.ram.beetl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.beetl.core.Context;
import org.beetl.core.Function;

import com.ram.kettle.util.Const;
import com.ram.server.jfinal.AppConfig;
import com.ram.server.util.BaseLog;

/***
 * 判断权限统一放一个里面，方便使用 判断单独某个组件的权限
 */
public class CheckStepFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {

		Object stepItem = paras[0];

		if (stepItem == null) {
			return false;
		}

		Object isAdmin = ctx.getGlobal(AppConfig.ISADMIN);
		if (Const.compare(isAdmin, "Y")) {
			return true;
		}

		Object stepSecObj = ctx.getGlobal(AppConfig.STEPARRAY);

		try {
			String nullJsonObj = "[]";
			if (stepSecObj == null) {
				// 一个页面 只需要取一次数据，优化。。。
				// 不需要多次读取 简化操作 
				Object securityContext = ctx.getGlobal(AppConfig.STEPSEC);
				JSONObject securityObject = JSONObject
						.fromObject(securityContext);

				Object resultObj = securityObject.get(AppConfig.RESULT);
				if (resultObj == null || !(Boolean) resultObj) {
					ctx.set(AppConfig.STEPARRAY, nullJsonObj);
					BaseLog.debug("CheckStepFunction  error #1");
					return false;
				}

				stepSecObj = securityObject.get(AppConfig.STEPSEC);
				if (stepSecObj == null) {
					ctx.set(AppConfig.STEPARRAY, nullJsonObj);
					BaseLog.debug("CheckStepFunction error #2");
					return false;
				}
				ctx.set(AppConfig.STEPARRAY, stepSecObj);
				BaseLog.debug("一个页面中执行一次后传入了属性中，这样可以将之前（备注说明）的内容简化成一个方法");
			}
			// 这里可以优化（后续处理） 把stepSecArray放入ctx
			JSONArray stepSecArray = JSONArray.fromObject(stepSecObj);
			// 判断权限
			int length = stepSecArray.size();

			for (int i = 0; i < length; i++) {
				String step = stepSecArray.getString(i);
				//String step = oj.getString(AppConfig.STEP);
				if (Const.compare(step, stepItem)) {
					return true;
				}
			}
		} catch (Exception e) {
			BaseLog.debug("CheckStepFunction ERROR:" + e.getMessage());
		}
		return false;
	}

}

package com.ram.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.ram.kettle.controller.CtrlDb;
import com.ram.kettle.controller.DataController;
import com.ram.kettle.element.RequestLocal;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.util.Const;
import com.ram.server.jfinal.MConfig;
import com.ram.server.util.BaseLog;

@PersistJobDataAfterExecution 
@DisallowConcurrentExecution
public class CacheJob implements Job {

	private static final CtrlDb controllers = new CtrlDb();

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		BaseLog.debug("CacheJob execute");
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String initktr = null;
		if (context.getMergedJobDataMap().size() > 0) {
//			Set<String> keys = context.getMergedJobDataMap().keySet();
			initktr = context.getMergedJobDataMap().getString(
					MConfig.INITRUNKTR);
//			for (String key : keys) {
//				String val = context.getMergedJobDataMap().getString(key);
//				BaseLog.debug(" - jobDataMap entry: " + key + " = " + val);
//			}
		}

		ConstLog.message("cachejob initktr:"+initktr);
		if (Const.isEmpty(initktr)) {
			return;
		}
		String[] iKtr = initktr.split(",");
		for (String ktr : iKtr) {
			if (Const.isEmpty(ktr)) {
				continue;
			}
			DataController controller = controllers.getController(ktr);
			if (controller == null) {
				BaseLog.debug("NO FOUND LOAD INITRUNKTR:" + ktr);
				continue;
			}
			RequestLocal req = new RequestLocal(1);
			req.put(0, "SID", "");
			try {
				controller.getReturnRow(null, req.getInputMeta(),
						req.getInputData());
				BaseLog.debug("LOAD INITRUNKTR:" + ktr);
			} catch (Exception e) {
				BaseLog.debug("LOAD DIM KTR ERROR:" + e.getMessage());
			}
		}
		context.setResult("hello");
	}
}

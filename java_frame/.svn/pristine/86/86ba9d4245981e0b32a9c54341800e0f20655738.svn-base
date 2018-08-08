package com.ram.quartz;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ram.quartz.job.CacheJob;

/**
 * web-info web.xml中配置quartz 不走代码
 *
 */
public class QFactory {
	private static SchedulerFactory schedulerfactory = null;
	private static Scheduler scheduler = null;

	static {
		schedulerfactory = new StdSchedulerFactory();
		try {
			// 通过schedulerFactory获取一个调度器
			scheduler = schedulerfactory.getScheduler();
			// 启动调度
			scheduler.start();
		} catch (Exception e) {
		}
	}

	public static void shutDown() {
		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}

	public static void load() throws SchedulerException {
		JobDetail jb = JobBuilder.newJob(CacheJob.class)
				.withDescription("this is a ram job") // job的描述
				.withIdentity("ramJob", "ramGroup") // job 的name和group
				.build();

		// 任务运行的时间，SimpleSchedle类型触发器有效
		long time = System.currentTimeMillis() + 3 * 1000L; // 3秒后启动任务
		Date startTime = new Date(time);

		// 4.创建Trigger
		SimpleScheduleBuilder builder = SimpleScheduleBuilder
				.repeatSecondlyForTotalCount(1);
		builder.repeatForever();
		builder.withIntervalInSeconds(60);// 间隔60秒

		// 使用SimpleScheduleBuilder或者CronScheduleBuilder
		Trigger t = TriggerBuilder.newTrigger().withDescription("")
				.withIdentity("ramTrigger", "ramTriggerGroup")
				.withSchedule(builder).startAt(startTime)
				// 默认当前时间启动
				// .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?"))
				// // 两秒执行一次
				.build();
		// 5.注册任务和定时器
		scheduler.scheduleJob(jb, t);

	}
}

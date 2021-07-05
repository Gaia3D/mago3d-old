package gaia3d.service;

import gaia3d.domain.JobExecutor;
import gaia3d.domain.quartz.SimpleTrigger;
import gaia3d.quartz.MyJobListener;
import gaia3d.quartz.MyTriggerListener;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 쿼츠
 * @author hansang
 *
 */
public interface QuartzService {

	/**
	 * SimpleTrigger 목록
	 * @return
	 */
	List<SimpleTrigger> getListSimpleTrigger();

	default <T> void addSchedule(Class<? extends Job> job, SimpleTrigger simpleTrigger, T t) {
		try {
			// Scheduler 생성
			SchedulerFactory factory = new StdSchedulerFactory();
			Scheduler scheduler = factory.getScheduler();

			// Listener 설정
			ListenerManager listenrManager = scheduler.getListenerManager();
			listenrManager.addJobListener(new MyJobListener());
			listenrManager.addTriggerListener(new MyTriggerListener());

			// Scheduler 실행
			//scheduler.start();

			// JOB Executor Class
			Class<? extends Job> jobClass = Enum.valueOf(JobExecutor.class,  simpleTrigger.getExecutorName()).getValue();


			// JOB Data 객체 생성
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("message", "Hello, Quartz!!!");

			String jobName = "JOB_"+simpleTrigger.getExecutorName()+"_"+simpleTrigger.getQrtzSimpleTriggerId();
			String jobGroupName = "JOB_"+simpleTrigger.getExecutorName();

			// JOB 생성
			JobDetail jobDetail = JobBuilder.newJob(jobClass)
					.withIdentity(jobName, jobGroupName)
					.setJobData(jobDataMap)
					.build();

			Trigger trigger = newTrigger()
					.withIdentity(simpleTrigger.getQrtzSimpleTriggerName(), simpleTrigger.getQrtzSimpleTriggerGroup())
					.withSchedule(cronSchedule("0 0/1 8-19 * * ?"))
					.forJob(jobName, jobGroupName)
					.build();

			// Schedule 등록
			scheduler.scheduleJob(jobDetail, trigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	default void addSchedule(Class<? extends Job> job) {
		try {
			// Scheduler 생성
			SchedulerFactory factory = new StdSchedulerFactory();
			Scheduler scheduler = factory.getScheduler();

			// Listener 설정
			ListenerManager listenrManager = scheduler.getListenerManager();
			listenrManager.addJobListener(new MyJobListener());
			listenrManager.addTriggerListener(new MyTriggerListener());

			// Scheduler 실행
			//scheduler.start();

			// JOB Executor Class
			Class<? extends Job> jobClass = job;

			// JOB Data 객체 생성
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put("message", "Hello, Quartz!!!");

			// JOB 생성
			JobDetail jobDetail = JobBuilder.newJob(jobClass)
					.withIdentity("job_name", "job_group")
					.setJobData(jobDataMap)
					.build();

			Trigger trigger = newTrigger()
					.withIdentity("trigger3", "group1")
					.withSchedule(cronSchedule("0/10 * 8-19 * * ?"))
					.forJob("job_name", "job_group")
					.build();

			// Schedule 등록
			scheduler.scheduleJob(jobDetail, trigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}

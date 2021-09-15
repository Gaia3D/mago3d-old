package gaia3d.quartz;

import gaia3d.domain.quartz.ScheduleInfo;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class ScheduleManager {

    public <T> void addSchedule(Class<? extends Job> job, ScheduleInfo scheduleInfo, T t) {
        try {
            // Scheduler 생성
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            // Listener 설정
            ListenerManager listenrManager = scheduler.getListenerManager();
            listenrManager.addJobListener(new MyJobListener());
            listenrManager.addTriggerListener(new MyTriggerListener());

            // Scheduler 실행
            scheduler.start();

            // JOB Executor Class (수행할 작업을 기술하는 부분)
            Class<? extends Job> jobClass = job;

            // JOB Data 객체 생성 (수행할 작업에 넘길 데이터)
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("message", "Hello, Quartz!!!");
            jobDataMap.put("data", scheduleInfo.getData());

            // JOB 생성(작업 생성)
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(scheduleInfo.getJobName(), scheduleInfo.getJobGroup())
                    .setJobData(jobDataMap)
                    .build();

            // Trigger 생성
            Trigger trigger = newTrigger()
                    .withIdentity(scheduleInfo.getTriggerName(), scheduleInfo.getTriggerGroup())
                    .withSchedule(cronSchedule(scheduleInfo.getCronSchedule()))
                    .forJob(scheduleInfo.getJobName(), scheduleInfo.getJobGroup())
                    .build();

            // Schedule 등록
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void addSchedule(Class<? extends Job> job) {
        try {
            // Scheduler 생성
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            // Listener 설정
            ListenerManager listenrManager = scheduler.getListenerManager();
            listenrManager.addJobListener(new MyJobListener());
            listenrManager.addTriggerListener(new MyTriggerListener());

            // Scheduler 실행
            scheduler.start();

            // JOB Executor Class
            Class<? extends Job> jobClass = job;

            // JOB Data 객체 생성
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("message", "health check!!!");

            // JOB 생성
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity("healthJob", "healthJobGroup")
                    .setJobData(jobDataMap)
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("healthTrigger", "healthTriggerGroup")
                    .withSchedule(cronSchedule("0/10 * 8-19 * * ?"))
                    .forJob("healthJob", "healthJobGroup")
                    .build();

            // Schedule 등록
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}




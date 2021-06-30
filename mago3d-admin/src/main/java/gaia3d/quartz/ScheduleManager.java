package gaia3d.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class ScheduleManager {

    public <T> void addSchedule(Class<? extends Job> job, T t) {
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
            jobDataMap.put("message", "Hello, Quartz!!!");
            jobDataMap.put("data", t);

            // JOB 생성
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity("job_name", "job_group")
                    .setJobData(jobDataMap)
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("trigger3", "group1")
                    .withSchedule(cronSchedule("0 0/1 8-19 * * ?"))
                    .forJob("job_name", "job_group")
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




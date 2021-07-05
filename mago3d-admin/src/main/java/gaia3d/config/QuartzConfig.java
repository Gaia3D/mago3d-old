package gaia3d.config;

import gaia3d.domain.JobExecutor;
import gaia3d.domain.quartz.SimpleTrigger;
import gaia3d.quartz.MyJobListener;
import gaia3d.quartz.MyTriggerListener;
import gaia3d.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Component
public class QuartzConfig {

    @Autowired
    QuartzService quartzService;

    @PostConstruct
    public void start() {
        try {
            List<SimpleTrigger> simpleTriggers = quartzService.getListSimpleTrigger();
            // Scheduler 생성
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            // Listener 설정
            ListenerManager listenrManager = scheduler.getListenerManager();
            listenrManager.addJobListener(new MyJobListener());
            listenrManager.addTriggerListener(new MyTriggerListener());

            // Scheduler 실행
            scheduler.start();

            for (SimpleTrigger simpleTrigger: simpleTriggers) {

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

                /*Trigger trigger = newTrigger()
                        .withIdentity("trigger3", "group1")
                        .withSchedule(cronSchedule("0/10 * 8-19 * * ?"))
                        .forJob("job_name", "job_group")
                        .build();*/

                Trigger trigger = newTrigger()
                        .withIdentity(simpleTrigger.getQrtzSimpleTriggerName(), simpleTrigger.getQrtzSimpleTriggerGroup())
                        //.startAt()  // 시간지정 안하면 현재시간
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(simpleTrigger.getRepeatInterval()) // 반복 주기
                                .withRepeatCount(simpleTrigger.getRepeatCount()))      // 반복 횟수
                        .forJob(jobName, jobGroupName)                 //JobDetail의 핸들을 사용하여 작업을 지정함
                        .build();

                // Schedule 등록
                scheduler.scheduleJob(jobDetail, trigger);
            }


        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
}
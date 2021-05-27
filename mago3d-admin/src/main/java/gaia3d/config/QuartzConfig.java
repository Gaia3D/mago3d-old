package gaia3d.config;//package gaia3d.config;
//
//import gaia3d.schedule.TestJob;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.quartz.JobBuilder.newJob;
//
//@Slf4j
//@Component
//public class QuartzConfig {
//
//    @Autowired
//    private Scheduler scheduler;
//
//    @PostConstruct
//    public void start() {
//        JobDetail testJobDetail = buildJobDetail(TestJob.class, "testJob", "test", new HashMap());
//        try {
//            scheduler.scheduleJob(testJobDetail, buildJobTrigger("0 5 * * * ?"));
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     *
//     * @param expression 0 10 11 * * ? 초 분 시 일 월 ?
//     * @return
//     */
//    public Trigger buildJobTrigger(String expression) {
//        // [ 0 10 * * * ? ] 10분 0초마다 실행
//        // [ 0 0 5 3 * ? ] 매달 3일 새벽 5시에 실행
//        return TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(expression)).build();
//    }
//
//    public JobDetail buildJobDetail(Class job, String name, String group, Map params) {
//        JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.putAll(params);
//
//        return newJob(job).withIdentity(name, group).usingJobData(jobDataMap).build();
//    }
//}

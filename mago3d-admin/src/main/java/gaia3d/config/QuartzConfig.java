package gaia3d.config;

import gaia3d.domain.quartz.ScheduleInfo;
import gaia3d.quartz.ScheduleManager;
import gaia3d.quartz.jobExecutor.HealthCheckJobExecutor;
import gaia3d.quartz.jobExecutor.MembershipJobExecutor;
import gaia3d.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class QuartzConfig {

    @Autowired
    private QuartzService quartzService;

    @PostConstruct
    public void init(){
        ScheduleManager scheduleManager = new ScheduleManager();

        List<ScheduleInfo> scheduleInfoList = quartzService.getListScheduleInfo();

        for(ScheduleInfo scheduleInfo : scheduleInfoList){
            scheduleManager.addSchedule(MembershipJobExecutor.class, scheduleInfo, "admin");
        }
        scheduleManager.addSchedule(HealthCheckJobExecutor.class);
    }
}
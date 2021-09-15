package gaia3d.quartz.jobExecutor;

import gaia3d.quartz.AutowiringSpringBeanJobFactory;
import gaia3d.service.UserService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class MembershipJobExecutor extends QuartzJobBean {

    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    //public static final String EXECUTION_COUNT = "EXECUTION_COUNT";

    @Autowired
    private static AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory;

    private static ApplicationContext ctx;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        ctx = autowiringSpringBeanJobFactory.getApplicationContext();

        System.out.println(context);
        JobDataMap map = context.getJobDetail().getJobDataMap();
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String message = map.getString("message");

        UserService userService = ctx.getBean(UserService.class);

        System.out.println(userService.getUser(map.getString("data")));
        System.out.println("======log======");

        System.out.println(String.format("[%-18s][%s] %s", "execute", currentDate, message ));
    }
}

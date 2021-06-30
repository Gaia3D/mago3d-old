package gaia3d.quartz.jobExecutor;

import gaia3d.domain.user.UserSession;
import gaia3d.service.UserService;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MembershipJobExecutor implements Job {

    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    public static final String EXECUTION_COUNT = "EXECUTION_COUNT";

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {

        ApplicationContext context = (ApplicationContext)ctx.getJobDetail().getJobDataMap().get("applicationContext");
        System.out.println(ctx);
        JobDataMap map = ctx.getJobDetail().getJobDataMap();
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String message = map.getString("message");


        UserService bean = context.getBean(UserService.class);

        System.out.println(bean.getUser(((UserSession) map.get("data")).getUserId()));

        System.out.println(String.format("[%-18s][%d][%s] %s", "execute", currentDate, message ));
    }
}
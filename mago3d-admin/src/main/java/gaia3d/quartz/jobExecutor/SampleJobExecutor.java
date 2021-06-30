package gaia3d.quartz.jobExecutor;

import gaia3d.service.UserService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class SampleJobExecutor implements Job {

    private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
    public static final String EXECUTION_COUNT = "EXECUTION_COUNT";

    @Autowired
    public UserService userService;


    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        System.out.println(ctx);
        JobDataMap map = ctx.getJobDetail().getJobDataMap();
        String currentDate = TIMESTAMP_FMT.format(new Date());
        String message = map.getString("message");

        int executeCount = 0;
        if (map.containsKey(EXECUTION_COUNT)) {
            executeCount = map.getInt(EXECUTION_COUNT);
        }
        executeCount += 1;
        map.put(EXECUTION_COUNT, executeCount);

        System.out.println(String.format("[%-18s][%d][%s] %s", "execute", executeCount, currentDate, message ));
    }
    
}




package gaia3d.quartz.jobExecutor;

import gaia3d.domain.Health;
import gaia3d.domain.cache.CacheName;
import gaia3d.domain.healthcheck.HealthCheckLog;
import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceType;
import gaia3d.quartz.AutowiringSpringBeanJobFactory;
import gaia3d.service.HealthCheckLogService;
import gaia3d.service.MicroServiceService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class HealthCheckJobExecutor extends QuartzJobBean {

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
        System.out.println("======log======");

        MicroServiceService microServiceService = ctx.getBean(MicroServiceService.class);
        HealthCheckLogService healthCheckLogService = ctx.getBean(HealthCheckLogService.class);

        List<MicroService> microServiceList = microServiceService.getListMicroService(new MicroService());

        for (MicroService microService : microServiceList) {

            String health = getHealthCheckService(microService);

            HealthCheckLog healthCheckLog = new HealthCheckLog();
            healthCheckLog.setMicroServiceId(microService.getMicroServiceId());
            healthCheckLog.setStatus(health);
            //healthCheckLogService.insertHealthCheckLog(healthCheckLog);

            microService.setStatus(health);
            microServiceService.updateMicroServiceStatus(microService);

        }

        System.out.println(String.format("[%-18s][%s] %s", "execute", currentDate, message ));
    }

    public String getHealthCheckService(MicroService microService) {
        //String serviceUrl = microService.getUrlScheme() + "://" + microService.getUrlHost() + ":" + microService.getUrlPort() + "/" + microService.getUrlPath();
        String serviceUrl = createServiceUrl(microService);
        RestTemplate restTemplate = ctx.getBean(RestTemplate.class);
        HttpStatus status = null;
        String health = null;
        try {
            if(MicroServiceType.CACHE == MicroServiceType.valueOf(microService.getMicroServiceType().toUpperCase())) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("cache_name=" + CacheName.MICRO_SERVICE.toString());
                String authData = stringBuilder.toString();

                ResponseEntity<String> result = restTemplate.postForEntity(serviceUrl, authData, String.class);
                status = result.getStatusCode();
            } else {
                ResponseEntity<String> response = restTemplate.getForEntity(serviceUrl, String.class);
                status = response.getStatusCode();
            }
            if (HttpStatus.OK == status) {
                health = Health.UP.name().toLowerCase();
            } else if (HttpStatus.SERVICE_UNAVAILABLE == status || HttpStatus.INTERNAL_SERVER_ERROR == status) {
                health = Health.DOWN.name().toLowerCase();
            } else {
                health = Health.UNKNOWN.name().toLowerCase();
            }
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            health = Health.UNKNOWN.name().toLowerCase();
            LogMessageSupport.printMessage(e, "@@@@@@@@@@@@ exception. message = {}", e.getClass().getName());
        }
        log.info("@@ httpStatus = {}, serviceUrl = {}", status, serviceUrl);
        return health;
    }

    private String createServiceUrl(MicroService microService) {
        return microService.getUrlScheme() + "://" + microService.getUrlHost() + ":" + microService.getUrlPort() + microService.getUrlPath();
    }
}

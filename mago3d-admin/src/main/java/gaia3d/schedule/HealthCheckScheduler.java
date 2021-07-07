package gaia3d.schedule;

import gaia3d.domain.Health;
import gaia3d.domain.cache.CacheName;
import gaia3d.domain.healthcheck.HealthCheckLog;
import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceType;
import gaia3d.service.HealthCheckLogService;
import gaia3d.service.MicroServiceService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 디지털 트윈 서비스 Health Check
 */
@Slf4j
@Component
public class HealthCheckScheduler {

    @Autowired
    private HealthCheckLogService healthCheckLogService;
    @Autowired
    private MicroServiceService microServiceService;
    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "${gaia3d.schedule.health.check}")
    public void healthCheck() throws Exception {
        List<MicroService> microServiceList = microServiceService.getListMicroService(new MicroService());
        for (MicroService microService : microServiceList) {

            String health = getHealthCheckService(microService);

            HealthCheckLog healthCheckLog = new HealthCheckLog();
            healthCheckLog.setMicroServiceId(microService.getMicroServiceId());
            healthCheckLog.setStatus(health);
            healthCheckLogService.insertHealthCheckLog(healthCheckLog);

            microService.setStatus(health);
            microServiceService.updateMicroServiceStatus(microService);

        }
    }

    public String getHealthCheckService(MicroService microService) {
        //String serviceUrl = microService.getUrlScheme() + "://" + microService.getUrlHost() + ":" + microService.getUrlPort() + "/" + microService.getUrlPath();
        String serviceUrl = createServiceUrl(microService);
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

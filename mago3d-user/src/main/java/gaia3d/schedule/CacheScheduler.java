package gaia3d.schedule;

import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.microservice.MicroService;
import gaia3d.service.MicroServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cache 갱신 스케줄러
 */
@Slf4j
@Component
public class CacheScheduler {

    @Autowired
    private MicroServiceService microServiceService;

    /**
     * 5분 주기 캐시 갱신
     * @throws Exception
     */
    @Scheduled(cron = "${lhdt.schedule.five-minutes.cache}")
    public void oneHoursCache() throws Exception {
        microService();
    }

    private void microService() {
        List<MicroService> microServiceList = microServiceService.getListMicroService(new MicroService());

        Map<String, MicroService> microServiceMap = new HashMap<>();
        for(MicroService microService : microServiceList) {
            microServiceMap.put(microService.getMicroServiceKey(), microService);
        }

        CacheManager.setMicroServiceMap(microServiceMap);
    }
}

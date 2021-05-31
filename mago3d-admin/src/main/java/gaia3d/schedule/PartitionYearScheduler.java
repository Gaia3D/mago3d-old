package gaia3d.schedule;

import gaia3d.service.*;
import gaia3d.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 년 단위 파티션 테이블 생성
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PartitionYearScheduler {

    private final AccessLogService accessLogService;
    private final DataAdjustLogService dataAdjustLogService;
    private final DataLogService dataLogService;
    private final MicroServiceService microServiceService;
    private final SimulationLogService simulationLogService;
    private final HealthCheckLogService healthCheckLogService;
    private final ApiLogService apiLogService;

    @Scheduled(cron = "${gaia3d.schedule.year.partition}")
    public void yearPartition() throws Exception {
        // create table access_log_2021 partition of access_log for values from ('2021-01-01 00:00:00.000000') to ('2022-01-01 00:00:00.000000');
        int nextYear = Integer.parseInt(DateUtils.getToday().substring(0, 4)) + 1;
        int afterNextYear = nextYear + 1;

        String tableName = Integer.toString(nextYear);
        String startTime = nextYear + "-01-01 00:00:00.000000";
        String endTime = (afterNextYear) + "-01-01 00:00:00.000000";
        log.info(" @@@@@ tableName = {}, startTime = {}, endTime = {}", tableName, startTime, endTime);

        createAccessLog(tableName, startTime, endTime);
        createDataAdjustLog(tableName, startTime, endTime);
        createDataLog(tableName, startTime, endTime);
        createSimulationLog(tableName, startTime, endTime);
        createMicroServiceLog(tableName, startTime, endTime);
        createHealthCheckLog(tableName, startTime, endTime);
        createApiLog(tableName, startTime, endTime);
    }

    /**
     * access_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createAccessLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ access_log 파티션 테이블 생성");
        accessLogService.createPartitionTable(tableName, startTime, endTime);
    }

    /**
     * data_adjust_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createDataAdjustLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ data_adjust_log 파티션 테이블 생성");
        dataAdjustLogService.createPartitionTable(tableName, startTime, endTime);
    }

    /**
     * data_adjust_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createDataLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ data_log 파티션 테이블 생성");
        dataLogService.createPartitionTable(tableName, startTime, endTime);
    }

    /**
     * micro_service_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createMicroServiceLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ micro_service_log 파티션 테이블 생성");
        microServiceService.createPartitionTable(tableName, startTime, endTime);
    }

    /**
     * simulation_log, simulation_detail_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createSimulationLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ simulation_log 파티션 테이블 생성");
        simulationLogService.createPartitionTable(tableName, startTime, endTime);
    }

    /**
     * health_check_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createHealthCheckLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ health_check_log 파티션 테이블 생성");
        healthCheckLogService.createPartitionTable(tableName, startTime, endTime);
    }

    /**
     * api_log 내년 파티션 테이블 생성
     * @param tableName
     * @param startTime
     * @param endTime
     */
    private void createApiLog(String tableName, String startTime, String endTime) {
        log.info("@@@@@@@@@@@@@ api_log 파티션 테이블 생성");
        apiLogService.createPartitionTable(tableName, startTime, endTime);
    }

}

package gaia3d.service;

import gaia3d.domain.healthcheck.HealthCheckLog;

import java.util.List;

/**
 * Health check 이력
 * @author jeongdae
 *
 */
public interface HealthCheckLogService {

	/**
	 * HealthCheck Log 총 건수
	 * @param healthCheckLog
	 * @return
	 */
	Long getHealthCheckLogTotalCount(HealthCheckLog healthCheckLog);

	/**
	 * HealthCheck Log 목록
	 * @param healthCheckLog
	 * @return
	 */
	List<HealthCheckLog> getListHealthCheckLog(HealthCheckLog healthCheckLog);

	/**
	 * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
	 */
	int createPartitionTable(String tableName, String startTime, String endTime);

	/**
	 * HealthCheck Log 등록
	 * @param healthCheckLog
	 * @return
	 */
	int insertHealthCheckLog(HealthCheckLog healthCheckLog);
}

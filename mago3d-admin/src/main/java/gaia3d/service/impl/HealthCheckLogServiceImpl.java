package gaia3d.service.impl;

import gaia3d.domain.healthcheck.HealthCheckLog;
import gaia3d.persistence.HealthCheckLogMapper;
import gaia3d.service.CommonService;
import gaia3d.service.HealthCheckLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Health Check Log
 * @author jeongdae
 *
 */
@Slf4j
@Service
public class HealthCheckLogServiceImpl implements HealthCheckLogService {

	@Autowired
	private CommonService commonService;
	@Autowired
	private HealthCheckLogMapper healthCheckLogMapper;

	/**
	 * Health Check Log 총 건수
	 * @param healthCheckLog
	 * @return
	 */
	@Transactional(readOnly=true)
	public Long getHealthCheckLogTotalCount(HealthCheckLog healthCheckLog) {
		return healthCheckLogMapper.getHealthCheckLogTotalCount(healthCheckLog);
	}
	
	/**
	 * Health Check Log 목록
	 * @param healthCheckLog
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<HealthCheckLog> getListHealthCheckLog(HealthCheckLog healthCheckLog) {
		return healthCheckLogMapper.getListHealthCheckLog(healthCheckLog);
	}

	/**
	 * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
	 * @param tableName
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional
	public int createPartitionTable(String tableName, String startTime, String endTime) {
		Boolean exist = commonService.isTableExist("access_log_" + tableName);
		log.info("@@@ accessLog tableName = {}, isTableExist = {}", tableName, exist);

		if(!exist) return healthCheckLogMapper.createPartitionTable(tableName, startTime, endTime);

		return 0;
	}
	
	/**
	 * Health Check 이력 저장
	 * @param healthCheckLog
	 * @return
	 */
	@Transactional
	public int insertHealthCheckLog(HealthCheckLog healthCheckLog) {
		return healthCheckLogMapper.insertHealthCheckLog(healthCheckLog);
	}
}

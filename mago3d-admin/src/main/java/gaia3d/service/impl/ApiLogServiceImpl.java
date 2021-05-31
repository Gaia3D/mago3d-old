package gaia3d.service.impl;

import gaia3d.domain.apilog.ApiLog;
import gaia3d.persistence.ApiLogMapper;
import gaia3d.service.ApiLogService;
import gaia3d.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 로그 처리
 * @author hansang
 *
 */
@Slf4j
@Service
public class ApiLogServiceImpl implements ApiLogService {

	@Autowired
	private ApiLogMapper apiLogMapper;
	@Autowired
	private CommonService commonService;

	/**
	 * API 요청 이력 총 건수
	 * @param apiLog
	 * @return
	 */
	@Transactional(readOnly=true)
	public Long getApiLogTotalCount(ApiLog apiLog) {
		return apiLogMapper.getApiLogTotalCount(apiLog);
	}
	
	/**
	 * API 요청 이력 목록
	 * @param apiLog
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<ApiLog> getListApiLog(ApiLog apiLog) {
		return apiLogMapper.getListApiLog(apiLog);
	}
	
	/**
	 * API 요청 이력 정보 취득
	 * @param apiLogId
	 * @return
	 */
	@Transactional(readOnly=true)
	public ApiLog getApiLog(Long apiLogId) {
		return apiLogMapper.getApiLog(apiLogId);
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
		Boolean exist = commonService.isTableExist("api_log_" + tableName);
		log.info("@@@ apiLog tableName = {}, isTableExist = {}", tableName, exist);

		if(!exist) return apiLogMapper.createPartitionTable(tableName, startTime, endTime);

		return 0;
	}
	
	/**
	 * 모든 API 요청에 대한 이력
	 * @param apiLog
	 * @return
	 */
	@Transactional
	public int insertApiLog(ApiLog apiLog) {
		return apiLogMapper.insertApiLog(apiLog);
	}
}

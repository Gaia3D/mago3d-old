package gaia3d.service.impl;

import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceLog;
import gaia3d.persistence.MicroServiceMapper;
import gaia3d.schedule.HealthCheckScheduler;
import gaia3d.service.CommonService;
import gaia3d.service.MicroServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Role
 * @author jeongdae
 *
 */
@Slf4j
@Service
public class MicroServiceServiceImpl implements MicroServiceService {

	@Autowired
	private CommonService commonService;

	@Autowired
	private MicroServiceMapper microServiceMapper;

	@Autowired
	private HealthCheckScheduler healthCheckScheduler;

	/**
	 * 마이크로 서비스 목록
	 * @param microService
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<MicroService> getListMicroService(MicroService microService) {
		return microServiceMapper.getListMicroService(microService);
	}

	/**
	 * 마이크로 서비스 로그 목록
	 * @param microServiceLog microServiceLog
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<MicroServiceLog> getListMicroServiceLog(MicroServiceLog microServiceLog) {
		return microServiceMapper.getListMicroServiceLog(microServiceLog);
	}

	/**
	 * 마이크로 서비스 정보
	 * @param microServiceId
	 * @return
	 */
	@Transactional(readOnly=true)
	public MicroService getMicroService(Integer microServiceId) {
		return microServiceMapper.getMicroService(microServiceId);
	}

	/**
	 * 마이크로 서비스 이용 연도
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Integer> getMicroServiceEnrollYear(){
		return microServiceMapper.getMicroServiceEnrollYear();
	};

	/**
	 * 마이크로 서비스 이력 총 건수
	 * @param microServiceLog
	 * @return
	 */
	@Transactional(readOnly = true)
	public Long getMicroServiceLogTotalCount(MicroServiceLog microServiceLog){
		return microServiceMapper.getMicroServiceLogTotalCount(microServiceLog);
	};

	/**
	 * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
	 * @param tableName
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional
	public int createPartitionTable(String tableName, String startTime, String endTime) {
		Boolean exist = commonService.isTableExist("micro_service_log_" + tableName);
		log.info("@@@ microServiceLog tableName = {}, isTableExist = {}", tableName, exist);

		if(!exist) return microServiceMapper.createPartitionTable(tableName, startTime, endTime);

		return 0;
	}

	/**
	 * 마이크로 서비스 등록
	 * @param microService
	 * @return
	 */
	@Transactional
	public int insertMicroService(MicroService microService) {
		String health = healthCheckScheduler.getHealthCheckService(microService);
		microService.setStatus(health);
		return microServiceMapper.insertMicroService(microService);
	}

	/**
	 * 마이크로 서비스 정보 수정
	 * @param microService
	 * @return
	 */
	@Transactional
	public int updateMicroService(MicroService microService) {
		return microServiceMapper.updateMicroService(microService);
	}

	/**
	 * 마이크로 서비스 Health Check 결과 수정
	 * @param microService
	 * @return
	 */
	@Transactional
	public int updateMicroServiceStatus(MicroService microService) {
		return microServiceMapper.updateMicroServiceStatus(microService);
	}

	/**
	 * 마이크로 서비스 삭제
	 * @param microServiceId
	 * @return
	 */
	@Transactional
	public int deleteMicroService(Integer microServiceId) {
		return microServiceMapper.deleteMicroService(microServiceId);
	}
}

package gaia3d.persistence;

import gaia3d.domain.apilog.ApiLog;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 로그 처리
 * @author jeongdae
 *
 */
@Repository
public interface ApiLogMapper {
	
	/**
	 * API 요청 이력 총 건수
	 * @param apiLog
	 * @return
	 */
	Long getApiLogTotalCount(ApiLog apiLog);
	
	/**
	 * API 요청 이력 목록
	 * @param apiLog
	 * @return
	 */
	List<ApiLog> getListApiLog(ApiLog apiLog);
	
	/**
	 * API 요청 이력 정보 취득
	 * @param apiLogId
	 * @return
	 */
	ApiLog getApiLog(Long apiLogId);

	/**
	 * API 요청 이력 등록
	 * @param apiLog
	 * @return
	 */
	int insertApiLog(ApiLog apiLog);

	/**
	 * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
	 * @param tableName
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	int createPartitionTable(String tableName, String startTime, String endTime);
}

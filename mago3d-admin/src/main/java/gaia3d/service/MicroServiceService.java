package gaia3d.service;

import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceLog;

import java.util.List;

public interface MicroServiceService {

    /**
     * 마이크로 서비스 목록
     * @param microService
     * @return
     */
    List<MicroService> getListMicroService(MicroService microService);

    /**
     * 마이크로 서비스 로그 목록
     * @param microServiceLog microServiceLog
     * @return
     */
    List<MicroServiceLog> getListMicroServiceLog(MicroServiceLog microServiceLog);

    /**
     * 마이크로 서비스 정보
     * @param microServiceId
     * @return
     */
    MicroService getMicroService(Integer microServiceId);

    /**
     * 마이크로 서비스 이용 연도
     * @return
     */
    List<Integer> getMicroServiceEnrollYear();

    /**
     * 마이크로 서비스 이력 총 건수
     * @param microServiceLog
     * @return
     */
    Long getMicroServiceLogTotalCount(MicroServiceLog microServiceLog);


    /**
     * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
     */
    int createPartitionTable(String tableName, String startTime, String endTime);

    /**
     * 마이크로 서비스 등록
     * @param microService
     * @return
     */
    int insertMicroService(MicroService microService);

    /**
     * 마이크로 서비스 정보 수정
     * @param microService
     * @return
     */
    int updateMicroService(MicroService microService);

    /**
     * 마이크로 서비스 Health Check 결과 수정
     * @param microService
     * @return
     */
    int updateMicroServiceStatus(MicroService microService);

    /**
     * 마이크로 서비스 삭제
     * @param microServiceId
     * @return
     */
    int deleteMicroService(Integer microServiceId);
}

package gaia3d.service;

public interface SimulationLogService {

    /**
     * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
     */
    int createPartitionTable(String tableName, String startTime, String endTime);

}

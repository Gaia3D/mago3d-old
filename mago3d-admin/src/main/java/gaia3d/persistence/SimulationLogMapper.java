package gaia3d.persistence;

public interface SimulationLogMapper {
    /**
     * 스케줄러에 의한 다음년도 파티션 테이블 자동 생성
     * @param tableName 테이블명
     * @param startTime 시작시간
     * @param endTime 종료시간
     * @return 파티션 테이블 생성 건수
     */
    int createPartitionTable(String tableName, String startTime, String endTime);
}

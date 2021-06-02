package gaia3d.domain.simulation;

import com.fasterxml.jackson.annotation.JsonFormat;
import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationLog extends Search {

    // 시뮬레이션 이력 고유번호
    private Long simulationLogId;
    // 시뮬레이션 이력 시계열 그룹 고유번호
    private Integer simulationTimeSeriesId;
    // 시뮬레이션 종류
    private String simulationType;
    // 시뮬레이션 명
    private String simulationName;
    // 사용자 아이디
    private String userId;
    // 시뮬레이션 일시
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime simulationDate;
    // 수정일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    // 등록일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;
    // 시뮬레이션 이력 고유번호
    private Long simulationDetailId;
    // 스냅샷
    private String contents;

//    @Getter(AccessLevel.NONE)
//    @Setter(AccessLevel.NONE)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
//    private LocalDateTime viewSimulationDate;
//
//    public LocalDateTime getViewSimulationDate() {
//        return viewSimulationDate;
//    }

    /**
     * ALL("all") 전체조회
     * TIME_SERIES("timeseries") 시계열조회
     * SIMULATION_ONLY("simulation") 시뮬레이션조회
     */
    // 시뮬레이션 검색조건
    private SimulationSearchType simulationSearchType;

    public void setSimulationSearchType(String type) {
        this.simulationSearchType = SimulationSearchType.findByType(type);
    }

}

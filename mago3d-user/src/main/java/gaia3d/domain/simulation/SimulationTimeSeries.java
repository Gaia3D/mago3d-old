package gaia3d.domain.simulation;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 시뮬레이션 이력 시계열 클래스
 *
 */
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationTimeSeries extends Search {

    // 고유번호
    private Integer simulationTimeSeriesId;
    // 시뮬레이션 시계열명
    @Size(max = 100)
    private String simulationTimeSeriesName;
    // 사용자 아이디
    private String userId;
    // 수정일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    // 등록일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;

    public SimulationTimeSeries(SimulationTimeSeries timeSeries) {
        this.simulationTimeSeriesId = timeSeries.getSimulationTimeSeriesId();
        this.simulationTimeSeriesName = timeSeries.getSimulationTimeSeriesName();
        this.userId = timeSeries.getUserId();
        this.updateDate = timeSeries.getUpdateDate();
        this.insertDate = timeSeries.getInsertDate();
    }

}

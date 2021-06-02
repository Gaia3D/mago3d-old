package gaia3d.domain.simulation;

import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "simulationTimeSeriesLog")
public class SimulationTimeSeriesLogDto extends SimulationTimeSeriesDto implements Serializable {

    private static final long serialVersionUID = 5677570055122200320L;

    // 시뮬레이션 이력 목록
    private List<SimulationLogDto> simulationLogs;

    public SimulationTimeSeriesLogDto(SimulationTimeSeriesDto timeSeries, List<SimulationLogDto> simulationLogs) {
        super(timeSeries);
        this.simulationLogs = simulationLogs;
    }

}
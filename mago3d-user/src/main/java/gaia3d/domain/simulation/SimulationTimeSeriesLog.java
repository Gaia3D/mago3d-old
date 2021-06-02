package gaia3d.domain.simulation;

import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationTimeSeriesLog extends SimulationTimeSeries {

    // 시뮬레이션 이력 목록
    private List<SimulationLog> simulationLogs;

    public SimulationTimeSeriesLog(SimulationTimeSeries timeSeries, List<SimulationLog> simulationLogs) {
        super(timeSeries);
        this.simulationLogs = simulationLogs;
    }

}
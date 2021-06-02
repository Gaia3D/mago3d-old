package gaia3d.service;

import gaia3d.domain.simulation.SimulationTimeSeriesLog;

public interface SimulationTimeSeriesLogService {
    SimulationTimeSeriesLog getSimulationTimeSeriesLog(Integer simulationTimeSeriesId);
    int insertSimulationTimeSeriesLog(SimulationTimeSeriesLog simulationTimeSeriesLog);
    int updateSimulationTimeSeriesLog(SimulationTimeSeriesLog simulationTimeSeriesLog);
    int deleteSimulationTimeSeriesLog(SimulationTimeSeriesLog simulationTimeSeriesLog);
}

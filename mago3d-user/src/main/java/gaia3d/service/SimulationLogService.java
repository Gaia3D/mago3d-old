package gaia3d.service;

import gaia3d.domain.simulation.SimulationLog;

import java.util.List;

public interface SimulationLogService {
    Long getSimulationLogTotalCount(SimulationLog simulationLog);
    List<SimulationLog> getListSimulationLog(SimulationLog simulationLog);
    SimulationLog getSimulationLog(Long simulationLogId);
    Long insertSimulationLog(SimulationLog simulationLog);
    int updateSimulationLog(SimulationLog simulationLog);
    int deleteSimulationLog(SimulationLog simulationLog);
}

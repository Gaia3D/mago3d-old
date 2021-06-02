package gaia3d.persistence;

import gaia3d.domain.simulation.SimulationLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationLogMapper {
    Long getSimulationLogTotalCount(SimulationLog simulationLog);
    List<SimulationLog> getListSimulationLog(SimulationLog simulationLog);
    SimulationLog getSimulationLog(Long simulationLogId);
    int insertSimulationLog(SimulationLog simulationLog);
    int insertSimulationDetailLog(SimulationLog simulationLog);
    int updateSimulationLog(SimulationLog simulationLog);
    int updateSimulationDetailLog(SimulationLog simulationLog);
    int deleteSimulationLog(SimulationLog simulationLog);
    int deleteSimulationDetailLog(SimulationLog simulationLog);
}

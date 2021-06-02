package gaia3d.service.impl;

import gaia3d.domain.simulation.SimulationLog;
import gaia3d.persistence.SimulationLogMapper;
import gaia3d.service.SimulationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationLogServiceImpl implements SimulationLogService {

    private final SimulationLogMapper simulationLogMapper;

    @Override
    @Transactional(readOnly = true)
    public Long getSimulationLogTotalCount(SimulationLog simulationLog) {
        return simulationLogMapper.getSimulationLogTotalCount(simulationLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulationLog> getListSimulationLog(SimulationLog simulationLog) {
        return simulationLogMapper.getListSimulationLog(simulationLog);
    }

    @Override
    @Transactional(readOnly = true)
    public SimulationLog getSimulationLog(Long simulationLogId) {
        return simulationLogMapper.getSimulationLog(simulationLogId);
    }

    @Override
    @Transactional
    public Long insertSimulationLog(SimulationLog simulationLog) {
        simulationLogMapper.insertSimulationLog(simulationLog);
        simulationLogMapper.insertSimulationDetailLog(simulationLog);
        return simulationLog.getSimulationLogId();
    }

    @Override
    @Transactional
    public int updateSimulationLog(SimulationLog simulationLog) {
        simulationLogMapper.updateSimulationLog(simulationLog);
        return simulationLogMapper.updateSimulationDetailLog(simulationLog);
    }

    @Override
    @Transactional
    public int deleteSimulationLog(SimulationLog simulationLog) {
        simulationLogMapper.deleteSimulationLog(simulationLog);
        return simulationLogMapper.deleteSimulationDetailLog(simulationLog);
    }

}

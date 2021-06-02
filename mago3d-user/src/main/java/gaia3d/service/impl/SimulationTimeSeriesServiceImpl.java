package gaia3d.service.impl;

import gaia3d.domain.simulation.SimulationLog;
import gaia3d.domain.simulation.SimulationSearchType;
import gaia3d.domain.simulation.SimulationTimeSeries;
import gaia3d.persistence.SimulationLogMapper;
import gaia3d.persistence.SimulationTimeSeriesMapper;
import gaia3d.service.SimulationTimeSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationTimeSeriesServiceImpl implements SimulationTimeSeriesService {

    private final SimulationTimeSeriesMapper simulationTimeSeriesMapper;
    private final SimulationLogMapper simulationLogMapper;

    @Override
    public int getSimulationTimeSeriesTotalCount(SimulationTimeSeries simulationTimeSeries) {
        return simulationTimeSeriesMapper.getSimulationTimeSeriesTotalCount(simulationTimeSeries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulationTimeSeries> getListSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries) {
        return simulationTimeSeriesMapper.getListSimulationTimeSeries(simulationTimeSeries);
    }

    @Override
    @Transactional(readOnly = true)
    public SimulationTimeSeries getSimulationTimeSeries(Integer simulationTimeSeriesId) {
        return simulationTimeSeriesMapper.getSimulationTimeSeries(simulationTimeSeriesId);
    }

    @Override
    @Transactional
    public int insertSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries) {
        simulationTimeSeriesMapper.insertSimulationTimeSeries(simulationTimeSeries);
        return simulationTimeSeries.getSimulationTimeSeriesId();
    }

    @Override
    @Transactional
    public int updateSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries) {
        return simulationTimeSeriesMapper.updateSimulationTimeSeries(simulationTimeSeries);
    }

    @Override
    @Transactional
    public int deleteSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries) {
        SimulationLog simulationLog = new SimulationLog();
        simulationLog.setSimulationSearchType(SimulationSearchType.TIME_SERIES.getType());
        simulationLog.setSimulationTimeSeriesId(simulationTimeSeries.getSimulationTimeSeriesId());
        List<SimulationLog> deleteSimulationLogs = simulationLogMapper.getListSimulationLog(simulationLog);
        for (SimulationLog deleteSimulationLog : deleteSimulationLogs) {
            simulationLogMapper.deleteSimulationLog(deleteSimulationLog);
            simulationLogMapper.deleteSimulationDetailLog(deleteSimulationLog);
        }
        return simulationTimeSeriesMapper.deleteSimulationTimeSeries(simulationTimeSeries);
    }

}

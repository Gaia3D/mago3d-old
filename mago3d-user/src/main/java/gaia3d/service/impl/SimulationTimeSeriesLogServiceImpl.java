package gaia3d.service.impl;

import gaia3d.domain.simulation.SimulationLog;
import gaia3d.domain.simulation.SimulationSearchType;
import gaia3d.domain.simulation.SimulationTimeSeries;
import gaia3d.domain.simulation.SimulationTimeSeriesLog;
import gaia3d.persistence.SimulationLogMapper;
import gaia3d.persistence.SimulationTimeSeriesMapper;
import gaia3d.service.SimulationTimeSeriesLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationTimeSeriesLogServiceImpl implements SimulationTimeSeriesLogService {

    private final SimulationTimeSeriesMapper simulationTimeSeriesMapper;
    private final SimulationLogMapper simulationLogMapper;

    @Override
    @Transactional(readOnly = true)
    public SimulationTimeSeriesLog getSimulationTimeSeriesLog(Integer simulationTimeSeriesId) {

        // 시계열 그룹 가져오기
        SimulationTimeSeries simulationTimeSeries = simulationTimeSeriesMapper.getSimulationTimeSeries(simulationTimeSeriesId);

        // 시뮬레이션 이력 목록 가져오기
        SimulationLog simulationLog = new SimulationLog();
        simulationLog.setSimulationSearchType(SimulationSearchType.TIME_SERIES.getType());
        simulationLog.setSimulationTimeSeriesId(simulationTimeSeriesId);
        simulationLog.setUserId(simulationTimeSeries.getUserId());
        List<SimulationLog> simulationLogs = simulationLogMapper.getListSimulationLog(simulationLog);

        // 시뮬레이션 이력 상세 가져오기
        List<SimulationLog> simulationDetailLogs = simulationLogs.stream()
                .map(log -> simulationLogMapper.getSimulationLog(log.getSimulationLogId()))
                .collect(Collectors.toList());

        return new SimulationTimeSeriesLog(simulationTimeSeries, simulationDetailLogs);
    }

    @Override
    @Transactional
    public int insertSimulationTimeSeriesLog(SimulationTimeSeriesLog simulationTimeSeriesLog) {

        // 시계열 그룹 저장
        //SimulationTimeSeries simulationTimeSeries = simulationTimeSeriesLog.getTimeSeries();
        simulationTimeSeriesMapper.insertSimulationTimeSeries(simulationTimeSeriesLog);
        int simulationTimeSeriesId = simulationTimeSeriesLog.getSimulationTimeSeriesId();
        String userId = simulationTimeSeriesLog.getUserId();

        // 시뮬레이션 이력 목록 저장
        List<SimulationLog> simulationLogs = simulationTimeSeriesLog.getSimulationLogs();
        for (SimulationLog simulationLog : simulationLogs) {
            simulationLog.setSimulationTimeSeriesId(simulationTimeSeriesId);
            simulationLog.setUserId(userId);
            //simulationLog.setSimulationDate(simulationLog.getViewSimulationDate());
            simulationLogMapper.insertSimulationLog(simulationLog);
            simulationLogMapper.insertSimulationDetailLog(simulationLog);
        }

        return simulationTimeSeriesId;
    }

    @Override
    @Transactional
    public int updateSimulationTimeSeriesLog(SimulationTimeSeriesLog simulationTimeSeriesLog) {

        // 시계열 그룹 수정
        //SimulationTimeSeries simulationTimeSeries = simulationTimeSeriesLog.getTimeSeries();
        int result = simulationTimeSeriesMapper.updateSimulationTimeSeries(simulationTimeSeriesLog);

        // 시뮬레이션 이력 목록 수정
        List<SimulationLog> simulationLogs = simulationTimeSeriesLog.getSimulationLogs();
        for (SimulationLog simulationLog : simulationLogs) {
            simulationLog.setUserId(simulationTimeSeriesLog.getUserId());
            //simulationLog.setSimulationDate(simulationLog.getViewSimulationDate());
            simulationLogMapper.updateSimulationLog(simulationLog);
            simulationLogMapper.updateSimulationDetailLog(simulationLog);
        }

        return result;
    }

    @Override
    @Transactional
    public int deleteSimulationTimeSeriesLog(SimulationTimeSeriesLog simulationTimeSeriesLog) {

        SimulationLog simulationLog = new SimulationLog();
        simulationLog.setSimulationSearchType(SimulationSearchType.TIME_SERIES.getType());
        simulationLog.setSimulationTimeSeriesId(simulationTimeSeriesLog.getSimulationTimeSeriesId());
        simulationLog.setUserId(simulationTimeSeriesLog.getUserId());

        // 시뮬레이션 이력 목록 삭제
        List<SimulationLog> deleteSimulationLogs = simulationLogMapper.getListSimulationLog(simulationLog);
        for (SimulationLog deleteSimulationLog : deleteSimulationLogs) {
            simulationLogMapper.deleteSimulationLog(deleteSimulationLog);
            simulationLogMapper.deleteSimulationDetailLog(deleteSimulationLog);
        }

        // 시계열 그룹 삭제
        return simulationTimeSeriesMapper.deleteSimulationTimeSeries(simulationTimeSeriesLog);
    }
}

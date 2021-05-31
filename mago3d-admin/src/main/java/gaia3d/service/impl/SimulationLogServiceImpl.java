package gaia3d.service.impl;

import gaia3d.persistence.SimulationLogMapper;
import gaia3d.service.CommonService;
import gaia3d.service.SimulationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimulationLogServiceImpl implements SimulationLogService {

    private final CommonService commonService;
    private final SimulationLogMapper simulationLogMapper;

    @Override
    public int createPartitionTable(String tableName, String startTime, String endTime) {

        return 0;
    }

}

package gaia3d.persistence;

import gaia3d.domain.simulation.SimulationTimeSeries;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationTimeSeriesMapper {
    int getSimulationTimeSeriesTotalCount(SimulationTimeSeries simulationTimeSeries);
    List<SimulationTimeSeries> getListSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
    SimulationTimeSeries getSimulationTimeSeries(Integer simulationTimeSeriesId);
    int insertSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
    int updateSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
    int deleteSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
}
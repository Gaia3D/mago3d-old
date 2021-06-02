package gaia3d.service;

import gaia3d.domain.simulation.SimulationTimeSeries;

import java.util.List;

public interface SimulationTimeSeriesService {
    int getSimulationTimeSeriesTotalCount(SimulationTimeSeries simulationTimeSeries);
    List<SimulationTimeSeries> getListSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
    SimulationTimeSeries getSimulationTimeSeries(Integer simulationTimeSeriesId);
    int insertSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
    int updateSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
    int deleteSimulationTimeSeries(SimulationTimeSeries simulationTimeSeries);
}

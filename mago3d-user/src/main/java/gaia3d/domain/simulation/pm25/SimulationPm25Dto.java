package gaia3d.domain.simulation.pm25;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import gaia3d.domain.simulation.Direction;

public class SimulationPm25Dto {
    private Causality causality;
    private Direction direction;

    public SimulationPm25 toSimulationPm25(PropertiesConfig propertiesConfig) {
        return new SimulationPm25(propertiesConfig, causality, direction);
    }

    public void setCausality(String name) {
        this.causality = Causality.findByName(name);
    }

    public void setDirection(String name) {
        this.direction = Direction.findByName(name);
    }
}

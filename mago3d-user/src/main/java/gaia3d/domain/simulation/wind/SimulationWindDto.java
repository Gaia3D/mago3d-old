package gaia3d.domain.simulation.wind;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import gaia3d.domain.simulation.Direction;

public class SimulationWindDto {
    private Causality causality;
    private Direction direction;

    public SimulationWind toSimulationWind(PropertiesConfig propertiesConfig) {
        return new SimulationWind(propertiesConfig, causality, direction);
    }

    public void setCausality(String name) {
        this.causality = Causality.findByName(name);
    }

    public void setDirection(String name) {
        this.direction = Direction.findByName(name);
    }

}

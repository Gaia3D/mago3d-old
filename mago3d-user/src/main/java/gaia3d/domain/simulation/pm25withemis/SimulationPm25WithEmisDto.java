package gaia3d.domain.simulation.pm25withemis;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import gaia3d.domain.simulation.Direction;

public class SimulationPm25WithEmisDto {
    private Causality causality;
    private Direction direction;

    public SimulationPm25WithEmis toSimulationPm25WithEmis(PropertiesConfig propertiesConfig) {
        return new SimulationPm25WithEmis(propertiesConfig, causality, direction);
    }

    public void setCausality(String name) {
        this.causality = Causality.findByName(name);
    }

    public void setDirection(String name) {
        this.direction = Direction.findByName(name);
    }
}

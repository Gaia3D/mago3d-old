package gaia3d.domain.simulation.wind;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import gaia3d.domain.simulation.Direction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulationWindDto {

    private Causality causality;
    private Direction direction;
    private String date;

    public SimulationWind toSimulationWind(PropertiesConfig propertiesConfig) {
        return new SimulationWind(propertiesConfig, causality, direction);
    }

    public SimulationWindDate toSimulationWindDate(PropertiesConfig propertiesConfig) {
        return new SimulationWindDate(propertiesConfig, date);
    }

    public void setCausality(String name) {
        this.causality = Causality.findByName(name);
    }

    public void setDirection(String name) {
        this.direction = Direction.findByName(name);
    }

}

package gaia3d.domain.simulation.stink;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimulationStinkDto {
    private Causality causality;

    public SimulationStink toSimulationStink(PropertiesConfig propertiesConfig) {
        return new SimulationStink(propertiesConfig, causality);
    }

    public void setCausality(String name) {
        this.causality = Causality.findByName(name);
    }
}

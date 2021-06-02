package gaia3d.domain.simulation;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SimulationSearchType {

    ALL("all"),
    TIME_SERIES("timeseries"),
    SIMULATION_ONLY("simulation");

    private final String type;
    SimulationSearchType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SimulationSearchType findByType(String type) {
        return Arrays.stream(values())
                .filter(t->t.getType().equalsIgnoreCase(type))
                .findAny()
                .orElse(ALL);
    }

}

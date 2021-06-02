package gaia3d.domain.simulation.wind;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import gaia3d.domain.simulation.Direction;
import gaia3d.domain.simulation.SimulationJsonImporter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class SimulationWind implements SimulationJsonImporter {

    private final PropertiesConfig propertiesConfig;
    private final Causality causality;
    private final Direction direction;

    public static final String FILE_NAME = "wind.json";
    public static final String UNDER_SCORE = "_";
    public static final String PATH_SEPARATOR = "/";

    @Override
    public Path getJsonPath() {
        String serviceDir = propertiesConfig.getAdminWindServiceDir();
        return Paths.get(serviceDir, causality.getName() + UNDER_SCORE + direction.getName(), FILE_NAME);
    }

    @Override
    public String getServicePath() {
        String servicePath = propertiesConfig.getAdminWindServicePath();
        return "/f4d/" + servicePath + causality.getName() + UNDER_SCORE + direction.getName() + PATH_SEPARATOR;
    }

}
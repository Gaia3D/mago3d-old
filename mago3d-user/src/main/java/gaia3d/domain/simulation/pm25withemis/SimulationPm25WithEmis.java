package gaia3d.domain.simulation.pm25withemis;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.Causality;
import gaia3d.domain.simulation.Direction;
import gaia3d.domain.simulation.SimulationJsonImporter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class SimulationPm25WithEmis implements SimulationJsonImporter {

    private final PropertiesConfig propertiesConfig;
    private final Causality causality;
    private final Direction direction;

    public static final String FILE_NAME = "pm2.5.json";
    public static final String UNDER_SCORE = "_";
    public static final String PATH_SEPARATOR = "/";

    @Override
    public Path getJsonPath() {
        String serviceDir = propertiesConfig.getAdminPm25WithEmisServiceDir();
        return Paths.get(serviceDir, causality.getName() + UNDER_SCORE + direction.getName(), FILE_NAME);
    }

    @Override
    public String getServicePath() {
        String servicePath = propertiesConfig.getAdminPm25WithEmisServicePath();
        return "/f4d/" + servicePath + causality.getName() + UNDER_SCORE + direction.getName() + PATH_SEPARATOR;
    }
}

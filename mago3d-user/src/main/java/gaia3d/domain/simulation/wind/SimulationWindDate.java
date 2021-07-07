package gaia3d.domain.simulation.wind;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.SimulationJsonImporter;
import gaia3d.domain.uploaddata.UploadDirectoryType;
import gaia3d.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

import static gaia3d.domain.simulation.wind.SimulationWind.PATH_SEPARATOR;

@RequiredArgsConstructor
public class SimulationWindDate implements SimulationJsonImporter {

    private final PropertiesConfig propertiesConfig;
    private final String date;

    @Override
    public Path getJsonPath() {
        String serviceDir = propertiesConfig.getAdminWindServiceDir();
        String fileName = String.format("OBS-QWM_%s.json", date);
        String makeDirectory = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, serviceDir, date);
        return Paths.get(makeDirectory, fileName);
    }

    @Override
    public String getServicePath() {
        String servicePath = propertiesConfig.getAdminWindServicePath();
        String makeDirectory = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, servicePath, date);
        return "/f4d/" + makeDirectory + PATH_SEPARATOR;
    }

}

package gaia3d.weather.wind.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
class WindServiceTest {

    @Disabled
    void generateByFile() {
        String location = "src/test/resources/OBS-QWM_2019090809.grib2";
        WindService windService = new WindService();
        windService.generate(location);
    }

    @Disabled
    void generatedByDir() throws IOException {

        WindService windService = new WindService();
        String directory = "C:\\data\\SNU_DSM_before_build_10x10";
        //String directory = "C:\\data\\SNU_DSM_after_build_10x10";

        Files.list(Paths.get(directory))
                //.filter(path -> path.toString().endsWith(".grb2"))
                .filter(path -> path.toString().endsWith(".grib2"))
                .map(Path::toFile)
                .forEach(file -> {
                    String location = file.getAbsolutePath();
                    windService.generate(location);
                });

    }

}
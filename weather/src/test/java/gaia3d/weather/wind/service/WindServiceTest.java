package gaia3d.weather.wind.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class WindServiceTest {

    @Disabled
    void generate() {
        String location = "src/test/resources/OBS-QWM_2019090809.grib2";
        WindService windService = new WindService();
        windService.generate(location);
    }

    @Test
    void generateByDir() throws IOException {

        WindService windService = new WindService();
        //String directory = "C:\\data\\SNU_Siheung_WIND_20190907";
        //String directory = "C:\\data\\AlphaMet_SNU_DSM_Plan_Wind10m";
        //String directory = "C:\\data\\Wind_Seoul_DSM_10m_0907";
        //String directory = "C:\\data\\SNU_DSM_WIND_AlphaMet_OF.tar[2735]";
        String directory = "C:\\data\\SNU_DSM_after_build_10x10";

        Files.list(Paths.get(directory))
                .filter(path -> path.toString().endsWith(".grib2"))
                //.filter(path -> path.toString().endsWith(".grb2"))
                .map(Path::toFile)
                .forEach(file -> {
                    String location = file.getAbsolutePath();
                    windService.generate(location);
                });

    }

}
package gaia3d.weather.wind.service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class WindServiceTest {

    @Test
    void generate() {
        String location = "src/test/resources/OBS-QWM_2019090809.grib2";
        WindService windService = new WindService();
        windService.generate(location);
    }

    @Test
    void generateByDir() throws IOException {

        WindService windService = new WindService();
        String directory = "src/test/resources/";

        Files.list(Paths.get(directory))
                .filter(path -> path.toString().endsWith(".grib2"))
                .map(Path::toFile)
                .forEach(file -> {
                    String location = file.getAbsolutePath();
                    windService.generate(location);
                });

        /*
        File directoryPath = new File(directoryPathName);
        String fileNameList[] = directoryPath.list();

        for (int i = 0; i < fileNameList.length; i++) {
            String format = fileNameList[i].split("\\.")[1];
            if (format.equals("grib2")) {
                String location = directoryPathName + "\\" + fileNameList[i];
                windService.generate(location);
            }
        }
        */

    }

}
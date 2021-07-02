package gaia3d.weather.wind.service;

import org.junit.jupiter.api.Test;

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
        //String directory = "C:\\data\\SNU_Siheung_WIND_20190907";
        String directory = "C:\\data\\SNU_DSM_WIND_AlphaMet_OF.tar[2735]";

        Files.list(Paths.get(directory))
                //.filter(path -> path.toString().endsWith(".grib2"))
                .filter(path -> path.toString().endsWith(".grb2"))
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
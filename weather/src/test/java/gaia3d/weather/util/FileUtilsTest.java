package gaia3d.weather.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
class FileUtilsTest {

    @Test
    void makeDirectory() throws IOException {

        Properties properties = new Properties();
        String location = "src/main/resources/mago3d.properties";
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(location));
        properties.load(bufferedReader);

        String path = properties.getProperty("mago3d.admin-wind-service-dir");

        String makeDirectory = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, path, "2019090809");
        log.info("@@@@@@@ = {}", makeDirectory);

    }

}
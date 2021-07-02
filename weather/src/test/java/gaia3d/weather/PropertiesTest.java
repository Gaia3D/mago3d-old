package gaia3d.weather;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PropertiesTest {

    @Test
    void getProperties() throws IOException {

        Properties properties = new Properties();
        String location = "src/main/resources/mago3d.properties";
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(location));
        properties.load(bufferedReader);

        String path = properties.getProperty("mago3d.admin-wind-service-dir");
        log.info(path);
        assertThat(path).isEqualTo("C:\\data\\mago3d\\f4d\\infra\\wind\\");

    }

}

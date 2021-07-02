package gaia3d.weather.json;

import gaia3d.weather.wind.domain.Wind;
import gaia3d.weather.wind.service.Grib2Reader;
import gaia3d.weather.wind.service.Reader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ucar.nc2.dt.GridCoordSystem;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FeatureTest {

    @Test
    void valueOf() {

        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        Reader reader = new Grib2Reader();
        Wind wind = reader.read(file);

        List<Color> bands = wind.getBands();
        Image image = wind.getImage();
        GridCoordSystem coordinateSystem = wind.getCoordinateSystem();
        String fileName = "OBS-QWM_2019090809_10.png";

        image.setUri(fileName);
        Feature feature = Feature.valueOf(coordinateSystem, 10.0f, image, bands.get(0));

        Map<String, Object> properties = new HashMap<>();
        properties.put("altitude", 10.0f);
        properties.put("image", new Image(471, 369, fileName));
        properties.put("value", bands.get(0));

        double[] bbox = new double[] {126.69918823242188, 37.34914779663086, 10.0, 126.74713897705078, 37.38668441772461, 10.0};
        Feature expected = Feature.builder()
                .type("Feature")
                .bbox(bbox)
                .geometry(Geometry.valueOf(bbox))
                .properties(properties)
                .build();

        assertThat(feature).isEqualTo(expected);

    }

}
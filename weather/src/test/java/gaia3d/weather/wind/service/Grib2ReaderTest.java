package gaia3d.weather.wind.service;

import gaia3d.weather.json.Band;
import gaia3d.weather.json.Color;
import gaia3d.weather.json.Image;
import gaia3d.weather.wind.domain.Wind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.dt.GridCoordSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Grib2ReaderTest {

    private Color[] expected;

    @BeforeEach
    void setUp() {
        Band defaultChannel = Band.builder().build();
        expected = new Color[]{
                Color.builder()
                        .r(Band.builder().min(0.279804f).max(1.274296f).build())
                        .g(Band.builder().min(0.905217f).max(1.186429f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.279804f).max(1.434681f).build())
                        .g(Band.builder().min(0.905217f).max(1.207608f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.279804f).max(1.702015f).build())
                        .g(Band.builder().min(0.905217f).max(1.242909f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.279804f).max(2.236805f).build())
                        .g(Band.builder().min(0.905217f).max(1.313596f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.279804f).max(3.003193f).build())
                        .g(Band.builder().min(0.905217f).max(1.380094f).build())
                        .b(defaultChannel)
                        .build()
        };
    }

    @Test
    void read() {
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        Reader reader = new Grib2Reader();
        Wind wind = reader.read(file);

        String fileName = wind.getFileName();
        List<Color> bands = wind.getBands();
        float[] zValues = wind.getZValues();
        Image image = wind.getImage();
        GridCoordSystem coordinateSystem = wind.getCoordinateSystem();
        int[][][] u = wind.getU();
        int[][][] v = wind.getV();

        assertThat(fileName).isEqualTo("OBS-QWM_2019090809");
        assertThat(bands.size()).isEqualTo(5);
        assertThat(bands).contains(expected);
        assertThat(zValues).isEqualTo(new float[] {10.0f, 30.0f, 50.0f, 100.0f, 150.0f});
        assertThat(image).isEqualTo(new Image(471, 369));
    }

    @Test
    void readVariable() {
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        String location = file.getAbsolutePath();

        try (NetcdfFile netcdfFile = NetcdfFiles.open(location)) {
            // read variables
            Grib2Reader reader = new Grib2Reader();
            reader.readVariable(netcdfFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
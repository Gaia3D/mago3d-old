package gaia3d.weather.wind.service;

import gaia3d.weather.json.Band;
import gaia3d.weather.json.Color;
import gaia3d.weather.json.Image;
import gaia3d.weather.util.WindImageUtil;
import gaia3d.weather.wind.domain.Wind;
import gaia3d.weather.wind.domain.WindVariable;
import gaia3d.weather.wind.service.Grib2Reader;
import gaia3d.weather.wind.service.Reader;
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
                        .r(Band.builder().min(0.285952f).max(1.176989f).build())
                        .g(Band.builder().min(0.913442f).max(1.183233f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.285952f).max(1.336336f).build())
                        .g(Band.builder().min(0.913442f).max(1.203603f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.285952f).max(1.601961f).build())
                        .g(Band.builder().min(0.913442f).max(1.237539f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.285952f).max(2.133272f).build())
                        .g(Band.builder().min(0.913442f).max(1.305425f).build())
                        .b(defaultChannel)
                        .build(),
                Color.builder()
                        .r(Band.builder().min(0.285952f).max(2.905336f).build())
                        .g(Band.builder().min(0.913442f).max(1.379514f).build())
                        .b(defaultChannel)
                        .build()
        };
    }

    @Test
    void read() {
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090700.grib2").toFile();
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
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019100700.grb2").toFile();
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
package gaia3d.weather.json;

import gaia3d.weather.util.Floats;
import gaia3d.weather.util.VariableUtil;
import gaia3d.weather.wind.domain.WindVariable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class BandsTest {

    private Bands[] expected;

    @BeforeEach
    void setUp() {
        Band defaultChannel = Band.builder().build();
        expected = new Bands[]{
                Bands.builder()
                        .r(Band.builder().min(0.285952f).max(1.176989f).build())
                        .g(Band.builder().min(0.913442f).max(1.183233f).build())
                        .b(defaultChannel)
                        .build(),
                Bands.builder()
                        .r(Band.builder().min(0.285952f).max(1.336336f).build())
                        .g(Band.builder().min(0.913442f).max(1.203603f).build())
                        .b(defaultChannel)
                        .build(),
                Bands.builder()
                        .r(Band.builder().min(0.285952f).max(1.601961f).build())
                        .g(Band.builder().min(0.913442f).max(1.237539f).build())
                        .b(defaultChannel)
                        .build(),
                Bands.builder()
                        .r(Band.builder().min(0.285952f).max(2.133272f).build())
                        .g(Band.builder().min(0.913442f).max(1.305425f).build())
                        .b(defaultChannel)
                        .build(),
                Bands.builder()
                        .r(Band.builder().min(0.285952f).max(2.905336f).build())
                        .g(Band.builder().min(0.913442f).max(1.379514f).build())
                        .b(defaultChannel)
                        .build()
        };
    }

    @Test
    void mapToList() throws IOException {
        // given
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        String location = file.getAbsolutePath();
        NetcdfFile netcdfFile = NetcdfFiles.open(location);
        GridDataset gridDataset = GridDataset.open(location);

        Map<WindVariable, List<Band>> bandMap = new HashMap<>();
        for (WindVariable windVariable : WindVariable.values()) {
            GridDatatype gridDataType = gridDataset.findGridDatatype(windVariable.getName());
            if (gridDataType == null) continue;
            float[] zValues = VariableUtil.getZVariableValues(gridDataType, netcdfFile);

            List<Band> bands = new ArrayList<>();
            for (int z = 0; z < Objects.requireNonNull(zValues).length; z++) {
                Array array = gridDataType.readDataSlice(0, z, -1, -1);
                float[][] datas = (float[][]) array.copyToNDJavaArray();
                Band bandInfo = Band.builder().min(Floats.min(datas)).max(Floats.max(datas)).build();
                log.info(String.format("variable: %s, isobaric: %f, min : %f, max: %f%n",
                        windVariable.getName(), zValues[z], bandInfo.getMin(), bandInfo.getMax()));
                bands.add(bandInfo);
            }
            bandMap.put(windVariable, bands);
        }
        // when
        List<Bands> bands = Bands.mapToList(bandMap);
        // then
        assertThat(bands.size()).isEqualTo(5);
        assertThat(bands).contains(expected);
    }

}
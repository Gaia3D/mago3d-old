package gaia3d.weather.util;

import gaia3d.weather.wind.domain.WindVariable;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class VariableUtilTest {

    @Test
    void getZVariableValues() throws IOException {
        // given
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        //File file = Paths.get("src", "test", "resources", "OBS-QWM_2019100700.grb2").toFile();
        String location = file.getAbsolutePath();
        NetcdfFile netcdfFile = NetcdfFiles.open(location);
        GridDataset gridDataset = GridDataset.open(location);
        GridDatatype gridDataType = gridDataset.findGridDatatype(WindVariable.U.getName());
        // when
        float[] zValues = VariableUtil.getZVariableValues(gridDataType, netcdfFile);
        // then
        assertThat(zValues).isEqualTo(new float[] {10.0f, 30.0f, 50.0f, 100.0f, 150.0f});
    }

}
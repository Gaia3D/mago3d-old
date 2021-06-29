package gaia3d.weather.json;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.unidata.geoloc.LatLonRect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CoordinateReferenceSystemTest {

    @Test
    void valueOf() {

        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        String location = file.getAbsolutePath();

        try {
            GridDataset gridDataset = GridDataset.open(location);
            GridDatatype gridDataType = gridDataset.findGridDatatype("u-component_of_wind_isobaric");
            GridCoordSystem coordinateSystem = gridDataType.getCoordinateSystem();

            CoordinateReferenceSystem crs = CoordinateReferenceSystem.valueOf(coordinateSystem);
            assertThat(crs.getProperties().get("name")).isEqualTo("urn:ogc:def:crs:EPSG::4326");

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
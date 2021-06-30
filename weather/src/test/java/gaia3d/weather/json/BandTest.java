package gaia3d.weather.json;

import gaia3d.weather.util.Floats;
import gaia3d.weather.wind.WindVariable;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class BandTest {

    @Test
    void mapToList() {
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        String location = file.getAbsolutePath();
        try {
            NetcdfFile netcdfFile = NetcdfFiles.open(location);
            GridDataset gridDataset = GridDataset.open(location);
            Map<WindVariable, List<BandInfo>> bandMap = new HashMap<>();
            for (WindVariable windVariable : WindVariable.values()) {
                GridDatatype gridDataType = gridDataset.findGridDatatype(windVariable.getName());
                Dimension zDimension = gridDataType.getZDimension();
                Variable zVariable = netcdfFile.findVariable(zDimension.getShortName());
                float[] zValues = getZVariableValues(zVariable);
                List<BandInfo> bands = new ArrayList<>();
                for (int z = 0; z < Objects.requireNonNull(zValues).length; z++) {
                    Array array = gridDataType.readDataSlice(0, z, -1, -1);
                    float[][] datas = (float[][]) array.copyToNDJavaArray();
                    BandInfo bandInfo = new BandInfo();
                    bandInfo.setMin(Floats.min(datas));
                    bandInfo.setMax(Floats.max(datas));
                    bands.add(bandInfo);
                }
                bandMap.put(windVariable, bands);
            }
            List<Band> bands = Band.mapToList(bandMap);
            assertThat(bands.size()).isEqualTo(5);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private float[] getZVariableValues(Variable zVariable) {
        float[] zValues = null;
        if (zVariable == null) return null;
        try {
            zValues = (float[]) zVariable.read().get1DJavaArray(zVariable.getDataType());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return zValues;
    }

}
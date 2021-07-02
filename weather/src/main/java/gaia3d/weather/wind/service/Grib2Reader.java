package gaia3d.weather.wind.service;

import gaia3d.weather.json.Band;
import gaia3d.weather.json.Color;
import gaia3d.weather.util.Floats;
import gaia3d.weather.util.VariableUtil;
import gaia3d.weather.util.WindImageUtil;
import gaia3d.weather.wind.domain.Wind;
import gaia3d.weather.wind.domain.WindVariable;
import lombok.extern.slf4j.Slf4j;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class Grib2Reader implements Reader {

    @Override
    public Wind read(File file) {
        log.info("======================> Data Read start");

        Wind wind = new Wind();
        wind.getFileNameExceptExtension(file);

        String location = file.getAbsolutePath();

        try {

            NetcdfFile netcdfFile = NetcdfFiles.open(location);
            GridDataset gridDataset = GridDataset.open(location);

            Map<WindVariable, List<Band>> bandMap = new HashMap<>();
            int[][][] u = null, v = null;

            for (WindVariable windVariable : WindVariable.values()) {

                // find grid by windVariable
                GridDatatype gridDataType = gridDataset.findGridDatatype(windVariable.getName());
                if (gridDataType == null) continue;

                // get z(height) values
                float[] zValues = wind.getZValues();
                if (zValues == null) wind.setZValues(VariableUtil.getZVariableValues(gridDataType, netcdfFile));
                zValues = wind.getZValues();

                // initialize
                wind.init(gridDataType);
                u = wind.getU();
                v = wind.getV();

                // add bands
                List<Band> bands = new ArrayList<>();
                for (int z = 0; z < Objects.requireNonNull(zValues).length; z++) {
                    // read data (index < 0, get all values)
                    Array array = gridDataType.readDataSlice(0, z, -1, -1);
                    float[][] datas = (float[][]) array.copyToNDJavaArray();

                    // create band
                    Band band = Band.builder().min(Floats.min(datas)).max(Floats.max(datas)).build();
                    bands.add(band);

                    log.info(String.format("variable: %s, isobaric: %f, min : %f, max: %f",
                            gridDataType.getShortName(), zValues[z], band.getMin(), band.getMax()));

                    // normalize (0-255)
                    for (int y = 0; y < datas.length; y++) {
                        for (int x = 0; x < datas[0].length; x++) {
                            float value = datas[y][x];
                            float normalize = WindImageUtil.normalize(band.getMin(), band.getMax(), value, 255);
                            if (WindVariable.U == windVariable) {
                                u[z][y][x] = Math.round(normalize);
                            } else if (WindVariable.V == windVariable) {
                                v[z][y][x] = Math.round(normalize);
                            }
                        }
                    }
                }
                // put bandMap
                bandMap.put(windVariable, bands);
            }

            List<Color> bands = Color.mapToList(bandMap);
            wind.setBands(bands);
            wind.setU(u);
            wind.setV(v);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return wind;
    }

    public void readVariable(NetcdfFile netcdfFile) {
        log.info("======================> NetcdfFile Variable List");
        List<Variable> variables = netcdfFile.getVariables();
        for (Variable variable : variables) {
            log.info(variable.toString());
        }
    }

}

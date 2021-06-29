package gaia3d.weather.wind;

import gaia3d.weather.util.Floats;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.*;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GeoGrid;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.util.NamedObject;
import ucar.unidata.geoloc.LatLonPointImpl;
import ucar.unidata.geoloc.LatLonRect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class Grib2ReaderTest {

    @Test
    void read() {
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        Reader reader = new Grib2Reader();
        reader.read(file);
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

    @Test
    void readGridDataSet() {
        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        String location = file.getAbsolutePath();

        try {
            NetcdfFile netcdfFile = NetcdfFiles.open(location);
            GridDataset gridDataset = GridDataset.open(location);
            GridDatatype gridDataType = gridDataset.findGridDatatype("u-component_of_wind_isobaric");
            GridCoordSystem coordinateSystem = gridDataType.getCoordinateSystem();
            LatLonRect latLonBoundingBox = coordinateSystem.getLatLonBoundingBox();

            Dimension zDimension = gridDataType.getZDimension();
            Variable zVariable = netcdfFile.findVariable(zDimension.getShortName());
            float[] zValues = new float[0];
            if (zVariable != null) {
                zValues = (float[]) zVariable.read().get1DJavaArray(zVariable.getDataType());
            }

            for (int z = 0; z < zValues.length; z++) {
                float isobaric = zValues[z];
                double[] bbox = new double[] {
                        latLonBoundingBox.getLonMin(), latLonBoundingBox.getLatMin(), isobaric,
                        latLonBoundingBox.getLonMax(), latLonBoundingBox.getLatMax(), isobaric,
                };
                System.out.println(Arrays.toString(bbox));

                Array array = gridDataType.readDataSlice(0, z, -1, -1);

                float[][] datas = (float[][]) array.copyToNDJavaArray();
                System.out.println("height : " + datas.length);
                System.out.println("width : " + datas[0].length);

                for (int y = 0; y < datas.length; y++) {
                    for (int x = 0; x < datas[0].length; x++) {
                        //System.out.print(datas[j][k] + " ");
                    }
                    //System.out.println();
                }

                //DataType dataType = array.getDataType();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
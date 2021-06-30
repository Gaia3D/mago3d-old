package gaia3d.weather.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.weather.util.Floats;
import gaia3d.weather.wind.WindVariable;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

class FeatureTest {

    @Test
    void valueOf() {

        File file = Paths.get("src", "test", "resources", "OBS-QWM_2019090809.grib2").toFile();
        String location = file.getAbsolutePath();

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        fileName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);

        try {
            NetcdfFile netcdfFile = NetcdfFiles.open(location);
            GridDataset gridDataset = GridDataset.open(location);

            Map<WindVariable, List<BandInfo>> bandMap = new HashMap<>();
            GridCoordSystem coordinateSystem = null;
            Image image = null;
            float[] zValues = null;

            for (WindVariable windVariable : WindVariable.values()) {

                GridDatatype gridDataType = gridDataset.findGridDatatype(windVariable.getName());
                if (coordinateSystem == null) coordinateSystem = gridDataType.getCoordinateSystem();

                int width = gridDataType.getXDimension().getLength();
                int height = gridDataType.getYDimension().getLength();
                if (image == null) image = new Image(width, height);

                Dimension zDimension = gridDataType.getZDimension();
                Variable zVariable = netcdfFile.findVariable(zDimension.getShortName());

                if (zValues == null) zValues = getZVariableValues(zVariable);
                List<BandInfo> bands = new ArrayList<>();
                for (int z = 0; z < Objects.requireNonNull(zValues).length; z++) {
                    Array array = gridDataType.readDataSlice(0, z, -1, -1);
                    float[][] datas = (float[][]) array.copyToNDJavaArray();
                    BandInfo bandInfo = new BandInfo();
                    bandInfo.setMin(Floats.min(datas));
                    bandInfo.setMax(Floats.max(datas));
                    System.out.println("min : " + bandInfo.getMin());
                    System.out.println("max : " + bandInfo.getMax());
                    bands.add(bandInfo);
                }
                bandMap.put(windVariable, bands);

            }

            ObjectMapper objectMapper = new ObjectMapper();
            List<Band> bands = Band.mapToList(bandMap);
            List<Feature> features = new ArrayList<>();
            if (zValues == null) return;
            for (int z = 0; z < zValues.length; z++) {
                float isobaric = zValues[z];
                Band band = bands.get(z);
                String partFileName = fileName + "_" + isobaric;
                image.setUri(partFileName + ".png");
                Feature feature = Feature.valueOf(coordinateSystem, isobaric, image, band);
                objectMapper.writeValue(Paths.get(partFileName + ".json").toFile(), feature);
                features.add(feature);
            }

            System.out.println("size : " + features.size());
            FeatureCollection featureCollection = new FeatureCollection();
            featureCollection.setCrs(CoordinateReferenceSystem.valueOf(coordinateSystem));
            featureCollection.setFeatures(features);
            objectMapper.writeValue(Paths.get(fileName + ".json").toFile(), featureCollection);

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
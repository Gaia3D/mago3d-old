package gaia3d.weather.wind.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.weather.json.*;
import gaia3d.weather.util.FileUtils;
import gaia3d.weather.util.UploadDirectoryType;
import gaia3d.weather.wind.domain.FilePattern;
import gaia3d.weather.wind.domain.Wind;
import lombok.extern.slf4j.Slf4j;
import ucar.nc2.dt.GridCoordSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class GeoJsonWriter implements Writer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Properties properties = new Properties();

    public GeoJsonWriter() {
        String location = "src/main/resources/mago3d.properties";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newBufferedReader(Paths.get(location));
            properties.load(bufferedReader);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void write(Wind wind, FilePattern filePattern) {

        float[] zValues = wind.getZValues();
        List<Color> bands = wind.getBands();
        Image image = wind.getImage();
        String fileName = wind.getFileName();
        GridCoordSystem coordinateSystem = wind.getCoordinateSystem();
        String date = filePattern.getDate(fileName);

        String path = properties.getProperty("mago3d.admin-wind-service-path");
        String servicePath = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, path, date);

        String directory = properties.getProperty("mago3d.admin-wind-service-dir");
        String serviceDir = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, directory, date);

        List<Feature> features = new ArrayList<>();
        if (zValues == null) return;
        for (int z = 0; z < zValues.length; z++) {
            float isobaric = zValues[z];
            Color band = bands.get(z);
            //image = new Image(image.getWidth(), image.getHeight(), String.format("%s%s_%d.png", servicePath, fileName, (int) isobaric));
            image = new Image(image.getWidth(), image.getHeight(), String.format("%s_%d.png", fileName, (int) isobaric));
            Feature feature = Feature.valueOf(coordinateSystem, isobaric, image, band);
            //String partFileName = fileName + "_" + isobaric;
            //objectMapper.writeValue(Paths.get(partFileName + ".json").toFile(), feature);
            features.add(feature);
        }

        log.info("features size : " + features.size());
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setCrs(CoordinateReferenceSystem.valueOf(coordinateSystem));
        featureCollection.setFeatures(features);

        try {
            objectMapper.writeValue(Paths.get(serviceDir, fileName + ".json").toFile(), featureCollection);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

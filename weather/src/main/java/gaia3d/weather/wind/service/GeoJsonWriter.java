package gaia3d.weather.wind.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.weather.json.*;
import gaia3d.weather.wind.domain.Wind;
import lombok.extern.slf4j.Slf4j;
import ucar.nc2.dt.GridCoordSystem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GeoJsonWriter implements Writer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(Wind wind) {

        float[] zValues = wind.getZValues();
        List<Color> bands = wind.getBands();
        Image image = wind.getImage();
        String fileName = wind.getFileName();
        GridCoordSystem coordinateSystem = wind.getCoordinateSystem();

        List<Feature> features = new ArrayList<>();
        if (zValues == null) return;
        for (int z = 0; z < zValues.length; z++) {
            float isobaric = zValues[z];
            Color band = bands.get(z);
            //String partFileName = fileName + "_" + isobaric;
            image.setUri(String.format("%s_%d.png", fileName, (int) isobaric));
            Feature feature = Feature.valueOf(coordinateSystem, isobaric, image, band);
            //objectMapper.writeValue(Paths.get(partFileName + ".json").toFile(), feature);
            features.add(feature);
        }

        log.info("features size : " + features.size());
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setCrs(CoordinateReferenceSystem.valueOf(coordinateSystem));
        featureCollection.setFeatures(features);

        try {
            objectMapper.writeValue(Paths.get(fileName + ".json").toFile(), featureCollection);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

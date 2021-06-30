package gaia3d.weather.json;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ucar.nc2.dt.GridCoordSystem;
import ucar.unidata.geoloc.LatLonRect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class Feature {

    private String type = "Feature";
    private double[] bbox;
    private Geometry geometry;
    private Map<String, Object> properties;

    public static Feature valueOf(GridCoordSystem coordinateSystem, float isobaric, Image image, Band band) {
        Feature feature = new Feature();
        // create bbox
        LatLonRect latLonBoundingBox = coordinateSystem.getLatLonBoundingBox();
        double[] bbox = new double[]{
                latLonBoundingBox.getLonMin(), latLonBoundingBox.getLatMin(), isobaric,
                latLonBoundingBox.getLonMax(), latLonBoundingBox.getLatMax(), isobaric,
        };
        log.info(Arrays.toString(bbox));
        feature.setBbox(bbox);
        feature.setGeometry(Geometry.valueOf(bbox));

        // crate properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("altitude", isobaric);
        properties.put("image", image);
        properties.put("value", band);
        feature.setProperties(properties);
        return feature;
    }

}

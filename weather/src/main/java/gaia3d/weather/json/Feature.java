package gaia3d.weather.json;

import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@Builder
@EqualsAndHashCode
public class Feature {

    private String type;
    private double[] bbox;
    private Geometry geometry;
    private Map<String, Object> properties;

    public static Feature valueOf(GridCoordSystem coordinateSystem, float isobaric, Image image, Bands band) {

        // create bbox
        LatLonRect latLonBoundingBox = coordinateSystem.getLatLonBoundingBox();
        double[] bbox = new double[]{
                latLonBoundingBox.getLonMin(), latLonBoundingBox.getLatMin(), isobaric,
                latLonBoundingBox.getLonMax(), latLonBoundingBox.getLatMax(), isobaric,
        };
        log.info(Arrays.toString(bbox));

        // crate properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("altitude", isobaric);
        properties.put("image", image);
        properties.put("value", band);

        return Feature.builder()
                .type("Feature")
                .bbox(bbox)
                .geometry(Geometry.valueOf(bbox))
                .properties(properties)
                .build();
    }

}

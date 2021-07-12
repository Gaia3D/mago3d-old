package gaia3d.weather.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FeatureCollection {
    private String type = "FeatureCollection";
    private CoordinateReferenceSystem crs;
    private Map<String, Object> style;
    private List<Feature> features;
}

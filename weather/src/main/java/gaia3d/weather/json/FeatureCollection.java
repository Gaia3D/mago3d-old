package gaia3d.weather.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeatureCollection {
    private String type = "FeatureCollection";
    private CoordinateReferenceSystem crs;
    private List<Feature> features;
}

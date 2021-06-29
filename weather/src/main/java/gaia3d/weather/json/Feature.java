package gaia3d.weather.json;

import java.util.Map;

public class Feature {
    private String type = "Feature";
    private double[] bbox;
    private Geometry geometry;
    private Map<String, Object> properties;
}

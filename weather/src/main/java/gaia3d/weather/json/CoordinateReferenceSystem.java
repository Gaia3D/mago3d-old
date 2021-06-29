package gaia3d.weather.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ucar.nc2.Attribute;
import ucar.nc2.AttributeContainer;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dt.GridCoordSystem;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ToString
@Getter
@AllArgsConstructor
public class CoordinateReferenceSystem {
    private String type;
    private Map<String, Object> properties;

    public static CoordinateReferenceSystem valueOf(GridCoordSystem coordinateSystem) {
        String type = "name";
        Map<String, Object> properties = new HashMap<>();

        if (coordinateSystem.isLatLon()) {
            properties.put("name", "urn:ogc:def:crs:EPSG::4326");
        }
        return new CoordinateReferenceSystem(type, properties);
    }

}

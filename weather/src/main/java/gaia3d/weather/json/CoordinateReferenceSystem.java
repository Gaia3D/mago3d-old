package gaia3d.weather.json;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ucar.nc2.dt.GridCoordSystem;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@ToString
public class CoordinateReferenceSystem {

    public static final String NAME = "name";
    private final String type;
    private final Map<String, Object> properties;

    /**
     * CoordinateReferenceSystem(CRS) 생성
     * @param coordinateSystem GridCoordSystem
     * @return CoordinateReferenceSystem 지리좌표체계
     */
    public static CoordinateReferenceSystem valueOf(GridCoordSystem coordinateSystem) {
        Map<String, Object> properties = new HashMap<>();
        if (coordinateSystem.isLatLon()) {
            properties.put(NAME, "urn:ogc:def:crs:EPSG::4326");
        }
        return CoordinateReferenceSystem.builder()
                .type(NAME)
                .properties(properties)
                .build();
    }

}

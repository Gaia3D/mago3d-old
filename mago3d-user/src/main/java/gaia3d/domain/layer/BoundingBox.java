package gaia3d.domain.layer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geotools.ows.wms.CRSEnvelope;
import org.opengis.geometry.DirectPosition;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BoundingBox {

    private double minx;
    private double miny;
    private double maxx;
    private double maxy;

    public static BoundingBox from(String bboxWkt) {
        bboxWkt = bboxWkt.replace("POLYGON((", "");
        bboxWkt = bboxWkt.replace("))", "");
        String[] points = bboxWkt.split(",");
        return new BoundingBox(
                Double.parseDouble(points[0].split(" ")[0]),
                Double.parseDouble(points[0].split(" ")[1]),
                Double.parseDouble(points[2].split(" ")[0]),
                Double.parseDouble(points[2].split(" ")[1])
        );
    }

    public static BoundingBox from(CRSEnvelope env) {
        DirectPosition lowerCorner = env.getLowerCorner();
        DirectPosition upperCorner = env.getUpperCorner();
        return new BoundingBox(
                lowerCorner.getCoordinate()[0],
                lowerCorner.getCoordinate()[1],
                upperCorner.getCoordinate()[0],
                upperCorner.getCoordinate()[1]
        );
    }

}

package gaia3d.weather.json;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class Geometry {
    private String type = "Polygon";
    private double[][][] coordinates;

    public static Geometry valueOf(double[] bbox) {

        Geometry geometry = new Geometry();

        double minX = bbox[0], minY = bbox[1], minZ = bbox[2];
        double maxX = bbox[3], maxY = bbox[4], maxZ = bbox[5];

        double[] leftBottom = new double[] {minX, minY, minZ};
        double[] rightBottom = new double[] {maxX, minY, minZ};

        double[] rightTop = new double[] {maxX, maxY, maxZ};
        double[] leftTop = new double[] {minX, maxY, maxZ};

        double[][][] coordinates = new double[1][5][3];
        coordinates[0] = new double[][]{ leftBottom, rightBottom, rightTop, leftTop, leftBottom };
        geometry.setCoordinates(coordinates);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(coordinates[0][i][j] + " ");
            }
            System.out.println();
        }

        return geometry;
    }

}

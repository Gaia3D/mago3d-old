package gaia3d.weather.json;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@Builder
public class Geometry {

    private final String type;
    private final double[][][] coordinates;

    public static Geometry valueOf(double[] bbox) {

        double minX = bbox[0], minY = bbox[1], minZ = bbox[2];
        double maxX = bbox[3], maxY = bbox[4], maxZ = bbox[5];

        double[] leftBottom = new double[] {minX, minY, minZ};
        double[] rightBottom = new double[] {maxX, minY, minZ};
        double[] rightTop = new double[] {maxX, maxY, maxZ};
        double[] leftTop = new double[] {minX, maxY, maxZ};

        double[][][] coordinates = new double[1][5][3];
        coordinates[0] = new double[][]{ leftBottom, rightBottom, rightTop, leftTop, leftBottom };

        return new Geometry("Polygon", coordinates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geometry geometry = (Geometry) o;
        double[][][] coordinates = geometry.coordinates;
        boolean flag = true;
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = 0; j < coordinates[i].length; j++) {
                for (int k = 0; k < coordinates[i][j].length; k++) {
                    flag = (this.coordinates[i][j][k] == coordinates[i][j][k]);
                }
            }
        }
        return Objects.equals(type, geometry.type) && flag;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(coordinates);
        return result;
    }

}

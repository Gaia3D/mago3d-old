package gaia3d.weather.json;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeometryTest {

    @Test
    void valueOf() {
        double[] bbox = new double[] {0, 0, 0, 100, 100, 0};
        Geometry actual = Geometry.valueOf(bbox);
        double[][][] coordinates = {
                {
                        {0, 0, 0},
                        {100, 0, 0},
                        {100, 100, 0},
                        {0, 100, 0},
                        {0, 0, 0}
                }
        };
        Geometry expected = new Geometry("Polygon", coordinates);
        assertThat(actual).isEqualTo(expected);
    }
}
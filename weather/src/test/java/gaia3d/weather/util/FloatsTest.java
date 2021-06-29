package gaia3d.weather.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FloatsTest {

    @Test
    void minmax() {
        float[] arrays = {1.1f, 2.5f, 82.4f, 82.4568748f, 72.4f, 17f, 0.41f, -44.5f, -44.4f};
        float min = Floats.min(arrays);
        float max = Floats.max(arrays);
        assertThat(min).isEqualTo(-44.5f);
        assertThat(max).isEqualTo(82.4568748f);
    }

}
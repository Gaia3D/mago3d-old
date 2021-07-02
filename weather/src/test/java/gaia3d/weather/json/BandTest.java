package gaia3d.weather.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BandTest {

    @Test
    void equals() {
        Band band1 = Band.builder().min(1.0001f).max(10.9999f).build();
        Band band2 = Band.builder().min(1.0001f).max(10.9999f).build();
        boolean actual = band1.equals(band2);
        assertThat(actual).isEqualTo(true);
    }

    @Test
    void equalsThreshold() {
        Band band1 = Band.builder().min(1.00001f).max(10.99999f).build();
        Band band2 = Band.builder().min(1.00002f).max(10.99998f).build();
        boolean actual = band1.equals(band2);
        assertThat(actual).isEqualTo(true);
    }

}
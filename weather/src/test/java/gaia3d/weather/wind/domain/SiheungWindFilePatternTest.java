package gaia3d.weather.wind.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SiheungWindFilePatternTest {

    @Test
    void getDate() {
        FilePattern filePattern = new SiheungWindFilePattern();
        String date = filePattern.getDate("OBS-QWM_2019090809");
        assertThat(date).isEqualTo("2019090809");
    }
}
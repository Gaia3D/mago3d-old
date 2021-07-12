package gaia3d.weather.json;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ColorRamp {
    private String color;
    private double value;
}

package gaia3d.weather.json;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class Band {

    private final double THRESHOLD = .0001;
    private float min;
    private float max;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Band bandInfo = (Band) o;
        return Math.abs(min - bandInfo.min) < THRESHOLD &&
                Math.abs(max - bandInfo.max) < THRESHOLD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(THRESHOLD, min, max);
    }
}
package gaia3d.weather.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(value = { "THRESHOLD" })
public class Band {

    private float min;
    private float max;
    private final double THRESHOLD = .0001;

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

    public String toString() {
        return String.format("(%s : %f, %s : %f)", "min", this.min, "max", this.max);
    }

}
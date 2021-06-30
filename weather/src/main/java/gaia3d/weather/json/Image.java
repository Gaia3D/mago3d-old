package gaia3d.weather.json;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Image {
    private final int width;
    private final int height;
    private String uri;
}

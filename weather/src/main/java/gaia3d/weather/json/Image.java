package gaia3d.weather.json;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Image {
    private final int width;
    private final int height;
    private String uri;
}

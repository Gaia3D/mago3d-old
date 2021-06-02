package gaia3d.domain.simulation;

import lombok.Getter;

import java.util.Arrays;

public enum Direction {

    SOUTH("s", "남"), // 12시
    SSW("ssw", "남남서"),
    SW("sw", "남서"),
    WSW("wsw", "서남서"),
    WEST("w", "서"),  // 3시
    WNW("wnw", "서북서"),
    NW("nw", "북서"),
    NNW("nnw", "북북서"),
    NORTH("n", "북"), // 6시
    NNE("nne", "북북동"),
    NE("ne", "북동"),
    ENE("ene", "동북동"),
    EAST("e", "동"),  // 9시
    ESE("ese", "동남동"),
    SE("se", "남동"),
    SSE("sse", "남남동");

    private @Getter String name;
    private @Getter String desc;
    Direction(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static Direction findByName(String name) {
        return Arrays.stream(values())
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

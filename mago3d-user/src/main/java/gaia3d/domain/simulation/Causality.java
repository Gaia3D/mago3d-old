package gaia3d.domain.simulation;

import lombok.Getter;

import java.util.Arrays;

public enum Causality {
    BEFORE("before"),   // 건설공정 전
    AFTER("after"),     // 건설공정 후
    CURRENT("current"); // 현재

    private @Getter String name;
    Causality(String name) {
        this.name = name;
    }

    public static Causality findByName(String name) {
        return Arrays.stream(values())
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(CURRENT);
    }

}

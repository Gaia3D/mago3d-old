package gaia3d.weather.wind.domain;

import lombok.Getter;

import java.util.Arrays;

public enum WindVariable {
//    U("u-component_of_wind_isobaric"),
//    V("v-component_of_wind_isobaric"),
//    W("w-component_of_wind_isobaric");

    U("v-component_of_wind_surface"),
    V("v-component_of_wind_surface"),
    W("w-component_of_wind_surface");

    private @Getter String name;
    WindVariable(String name) {
        this.name = name;
    }

    public WindVariable findByName(String name) {
        return Arrays.stream(WindVariable.values())
                .filter(variable -> variable.name.equals(name))
                .findFirst()
                .orElse(null);
    }

}

package gaia3d.weather.wind.service;

import gaia3d.weather.wind.domain.Wind;

import java.io.File;

public interface Reader {
    Wind read(File file);
}

package gaia3d.weather.wind.service;

import gaia3d.weather.wind.domain.FilePattern;
import gaia3d.weather.wind.domain.Wind;

public interface Writer {
    void write(Wind wind, FilePattern filePattern);
}

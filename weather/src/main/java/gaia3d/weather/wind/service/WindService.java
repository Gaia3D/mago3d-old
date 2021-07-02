package gaia3d.weather.wind.service;

import gaia3d.weather.wind.domain.Wind;

import java.io.File;
import java.nio.file.Paths;

public class WindService {

    public void generate(String location) {

        Reader reader = new Grib2Reader();
        Writer geoJsonWriter = new GeoJsonWriter();
        Writer imageWriter = new ImageWriter();

        File file = Paths.get(location).toFile();

        Wind wind = reader.read(file);
        geoJsonWriter.write(wind);
        imageWriter.write(wind);

    }

}

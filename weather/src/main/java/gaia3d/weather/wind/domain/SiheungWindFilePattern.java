package gaia3d.weather.wind.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SiheungWindFilePattern implements FilePattern {

    private String pattern = "OBS-QWM_(\\d+)";

    @Override
    public String getDate(String path) {
        String date = "";
        Matcher matcher = Pattern.compile(pattern).matcher(path);
        if (matcher.find()) {
            //log.info(matcher.group(0));
            //log.info(matcher.group(1));
            date = matcher.group(1);
        }
        return date;
    }

}

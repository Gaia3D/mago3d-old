# netcdf-java for developers
https://www.unidata.ucar.edu/software/netcdf-java/#developers

# netcdf-java tutorial
https://docs.unidata.ucar.edu/netcdf-java/7.0/userguide/index.html

# netcdf-java artifacts
https://docs.unidata.ucar.edu/netcdf-java/7.0/userguide/using_netcdf_java_artifacts.html

# gdal convert grib to netcdf
gdalwarp -overwrite -to SRC_METHOD=NO_GEOTRANSFORM -t_srs EPSG:4326 C:\Users\Ruby\Downloads\SNU_Siheung_WIND_20190907\OBS-QWM_2019090809.grib2 -of netCDF C:\Users\Ruby\Downloads\SNU_Siheung_WIND_20190907\OBS-QWM_2019090809.nc

# encoding and decoding of float to rg channel
https://titanwolf.org/Network/Articles/Article?AID=666e7443-0511-4210-b39c-db0bb6738246#gsc.tab=0

# air pressure to altitude calculator
https://www.mide.com/air-pressure-at-altitude-calculator

# given-when-then pattern
https://martinfowler.com/bliki/GivenWhenThen.html

# color-ramps json example
https://doc.arcgis.com/en/maps-for-microstrategy/install-and-configure/modify-default-symbols-and-color-ramps.htm
https://developers.arcgis.com/javascript/latest/visualization/symbols-color-ramps/esri-color-ramps/

```
List<ColorRamp> colorRamps = Arrays.asList(
        ColorRamp.builder().color("#feeedf").value(0.0).build(),
        ColorRamp.builder().color("#fdbe86").value(5.0).build(),
        ColorRamp.builder().color("#fd8c3d").value(10.0).build(),
        ColorRamp.builder().color("#e65507").value(15.0).build(),
        ColorRamp.builder().color("#a63700").value(20.0).build());
     
List<ColorRamp> colorRamps = Arrays.asList(
        ColorRamp.builder().color("#215587").value(0.0).build(),
        ColorRamp.builder().color("#245b91").value(2.0).build(),
        ColorRamp.builder().color("#26629b").value(4.0).build(),
        ColorRamp.builder().color("#2968a5").value(6.0).build(),
        ColorRamp.builder().color("#2b6eaf").value(8.0).build(),
        ColorRamp.builder().color("#307bc3").value(10.0).build(),
        ColorRamp.builder().color("#3687d7").value(12.0).build(),
        ColorRamp.builder().color("#3b94eb").value(14.0).build(),
        ColorRamp.builder().color("#40a0ff").value(16.0).build(),
        ColorRamp.builder().color("#6db8ff").value(18.0).build(),
        ColorRamp.builder().color("#9bd0ff").value(20.0).build(),
        ColorRamp.builder().color("#c8e7ff").value(22.0).build(),
        ColorRamp.builder().color("#f5ffff").value(24.0).build());
```
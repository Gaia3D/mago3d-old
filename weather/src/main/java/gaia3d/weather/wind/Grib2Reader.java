package gaia3d.weather.wind;

import lombok.extern.slf4j.Slf4j;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class Grib2Reader implements Reader {

    @Override
    public void read(File file) {
        log.info("======================> Data Read start");
        String location = file.getAbsolutePath();
        try (NetcdfFile netcdfFile = NetcdfFiles.open(location)) {
            // read variables
            readVariable(netcdfFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void readVariable(NetcdfFile netcdfFile) {
        log.info("======================> NetcdfFile Variable List");
        List<Variable> variables = netcdfFile.getVariables();
        for (Variable variable : variables) {
            System.out.println(variable.toString());
        }
    }
}

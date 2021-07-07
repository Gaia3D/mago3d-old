package gaia3d.weather.util;

import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dt.GridDatatype;

import java.io.IOException;

public class VariableUtil {

    public static float[] getZVariableValues(GridDatatype gridDataType, NetcdfFile netcdfFile) {
        Dimension zDimension = gridDataType.getZDimension();
        if (zDimension == null) return new float[]{ 10.0f };
        Variable zVariable = netcdfFile.findVariable(zDimension.getShortName());
        return getZVariableValues(zVariable);
    }

    public static float[] getZVariableValues(Variable zVariable) {
        if (zVariable == null) return null;
        float[] zValues = null;
        try {
            DataType dataType = zVariable.getDataType();
            if (dataType.isFloatingPoint()) {
                zValues = (float[]) zVariable.read().get1DJavaArray(dataType);
                /*
                for (int i = 0; i < (zValues.length / 2); i++) {
                    float temp = zValues[i];
                    zValues[i] = zValues[zValues.length - i - 1];
                    zValues[zValues.length - i - 1] = temp;
                }
                */
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return zValues;
    }

}

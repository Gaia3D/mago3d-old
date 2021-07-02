package gaia3d.weather.wind.domain;

import gaia3d.weather.json.Color;
import gaia3d.weather.json.Image;
import lombok.Getter;
import lombok.Setter;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class Wind {

    private String fileName;
    List<Color> bands;
    private GridCoordSystem coordinateSystem = null;
    private Image image = null;
    private float[] zValues = null;
    private int[][][] u = null;
    private int[][][] v = null;

    public void getFileNameExceptExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        fileName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        this.fileName = fileName;
    }

    public void init(GridDatatype gridDataType) {
        if (this.coordinateSystem == null) this.coordinateSystem = gridDataType.getCoordinateSystem();
        int width = gridDataType.getXDimension().getLength();
        int height = gridDataType.getYDimension().getLength();
        if (this.image == null) this.image = new Image(width, height);
        u = new int[zValues.length][height][width];
        v = new int[zValues.length][height][width];
    }

}

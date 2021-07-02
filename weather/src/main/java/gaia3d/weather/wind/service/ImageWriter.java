package gaia3d.weather.wind.service;

import gaia3d.weather.json.Image;
import gaia3d.weather.util.FileUtils;
import gaia3d.weather.util.UploadDirectoryType;
import gaia3d.weather.wind.domain.FilePattern;
import gaia3d.weather.wind.domain.Wind;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ImageWriter implements Writer {

    private final Properties properties = new Properties();

    public ImageWriter() {
        String location = "src/main/resources/mago3d.properties";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newBufferedReader(Paths.get(location));
            properties.load(bufferedReader);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void write(Wind wind, FilePattern filePattern) {

        float[] zValues = wind.getZValues();
        Image image = wind.getImage();
        String fileName = wind.getFileName();
        int[][][] u = wind.getU();
        int[][][] v = wind.getV();
        String directory = properties.getProperty("mago3d.admin-wind-service-dir");
        String date = filePattern.getDate(fileName);
        String makeDirectory = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, directory, date);

        if (zValues == null) return;
        for (int z = 0; z < zValues.length; z++) {
            float isobaric = zValues[z];

            BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            WritableRaster raster = bi.getRaster();
            int data[]  = new int[4];
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    data[0] = u[z][y][x];   // (uvec >> 8) & 0x0ff;
                    data[1] = v[z][y][x];   // uvec & 0x0ff;
                    data[2] = 0;            // (vvec >> 8) & 0x0ff;
                    data[3] = 255;          // vvec & 0x0ff;
                    raster.setPixel(x, y, data);
                }
            }
            bi.flush();
            File ffn = Paths.get(makeDirectory, String.format("%s_%d.png", fileName, (int) isobaric)).toFile();

            try {
                ImageIO.write(bi, "png", ffn);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}

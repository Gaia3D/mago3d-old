package gaia3d.weather.wind.service;

import gaia3d.weather.json.Image;
import gaia3d.weather.wind.domain.Wind;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class ImageWriter implements Writer {

    @Override
    public void write(Wind wind) {

        float[] zValues = wind.getZValues();
        Image image = wind.getImage();
        String fileName = wind.getFileName();
        int[][][] u = wind.getU();
        int[][][] v = wind.getV();

        if (zValues == null) return;
        for (int z = 0; z < zValues.length; z++) {
            float isobaric = zValues[z];
            image.setUri(String.format("%s_%d.png", fileName, (int) isobaric));

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
            File ffn = new File(image.getUri());

            try {
                ImageIO.write(bi, "png", ffn);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}

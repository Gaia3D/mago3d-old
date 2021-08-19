package gaia3d;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.weather.json.*;
import gaia3d.weather.util.FileUtils;
import gaia3d.weather.util.UploadDirectoryType;
import gaia3d.weather.util.WindImageUtil;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Mago3dWeatherApplication {
    public static void main(String... args){
        log.info("@@@@ Mago3dWeatherApplication start");

//        String uFile = "src/test/resources/2017_M05_D02_0000(UTC+0900)_L01_1HR.usp";
//        String vFile = "src/test/resources/2017_M05_D02_0000(UTC+0900)_L01_1HR.vsp";
//        String outputFilePath = "C:\\data\\mago3d\\f4d\\infra\\wind\\";
//        String date = "201705020000";

        String uFile = args[0];
        String vFile = args[1];
        String outputFilePath = args[2];
        String date = args[3];
        String makeDirectory = FileUtils.makeDirectory(null, UploadDirectoryType.YEAR_MONTH, outputFilePath, date);

        float[] zValues = new float[]{ 0.0f };
        int height = 40;
        int width = 40;

        int[][][] u = new int[zValues.length][height][width];
        int[][][] v = new int[zValues.length][height][width];

        // read files
        List<String> uLines = null;
        List<String> vLines = null;
        try {
            log.info("@@@@ files read start");
            uLines = Files.readAllLines(Paths.get(uFile), StandardCharsets.UTF_8);
            vLines = Files.readAllLines(Paths.get(vFile), StandardCharsets.UTF_8);
            log.info("@@@@ files read end");
        } catch (IOException e) {
            log.error("IOException... {}", e);
        }

        int metaInfoEndLineNumber = 4;

        // set meta values
        String[] uminmax = uLines.get(metaInfoEndLineNumber).trim().split("\\s+");
        float uMin = Float.parseFloat(uminmax[0]);
        float uMax = Float.parseFloat(uminmax[1]);

        String[] vminmax = vLines.get(metaInfoEndLineNumber).trim().split("\\s+");
        float vMin = Float.parseFloat(vminmax[0]);
        float vMax = Float.parseFloat(vminmax[1]);

        Band r = Band.builder().min(uMin).max(uMax).build();
        Band g = Band.builder().min(vMin).max(vMax).build();
        Bands bands = Bands.builder().r(r).g(g).b(Band.builder().build()).build();
        log.info("@@@@ parse meta values u = {}, v = {}", r.toString(), g.toString());

        // parse datas
        List<String> uDataLines = uLines.subList(metaInfoEndLineNumber + 1, uLines.size());
        List<String> vDataLines = uLines.subList(metaInfoEndLineNumber + 1, vLines.size());

        log.info("@@@@ data normalize");
        for (int i = 0; i < height; i++) {
            String[] uDatas = uDataLines.get(i).trim().split("\\s+");
            String[] vDatas = vDataLines.get(i).trim().split("\\s+");
            for (int j = 0; j < width; j++) {
                float uValue = Float.parseFloat(uDatas[j]);
                float vValue = Float.parseFloat(vDatas[j]);
                // normalize values
                float uNormalize = WindImageUtil.normalize(uMin, uMax, uValue, 255);
                float vNormalize = WindImageUtil.normalize(vMin, vMax, vValue, 255);
                u[0][i][j] = Math.round(uNormalize);
                v[0][i][j] = Math.round(vNormalize);
            }
        }

        File inputFile = Paths.get(uFile).toFile();
        String fileName = inputFile.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0 && pos < (fileName.length() - 1)) {
            // If '.' is not the first or last character.
            fileName = fileName.substring(0, pos);
        }
        List<Feature> features = new ArrayList<>();

        for (int z = 0; z < zValues.length; z++) {
            // write png file
            float isobaric = zValues[z];
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            WritableRaster raster = bi.getRaster();
            int[] data = new int[4];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
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
                log.info("@@@@ write png files : {}", ffn.getAbsolutePath());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Image image = new Image(width, height);
            image.setUri(String.format("%s_%d.png", fileName, (int) isobaric));
            // minX, minY, minZ, maxX, maxY, maxZ
            double[] bbox = new double[]{ 126.937685, 37.370411, isobaric, 126.982666, 37.406303, isobaric };
            // crate properties
            Map<String, Object> properties = new HashMap<>();
            properties.put("altitude", isobaric);
            properties.put("image", image);
            properties.put("value", bands);
            // create feature
            Feature feature = Feature.builder()
                    .type("Feature")
                    .bbox(bbox)
                    .geometry(Geometry.valueOf(bbox))
                    .properties(properties)
                    .build();
            features.add(feature);

        }

        // write geojson file
        FeatureCollection featureCollection = new FeatureCollection();
        Map<String, Object> properties = new HashMap<>();
        properties.put(CoordinateReferenceSystem.NAME, "urn:ogc:def:crs:EPSG::4326");
        CoordinateReferenceSystem crs = CoordinateReferenceSystem.builder()
                .type(CoordinateReferenceSystem.NAME)
                .properties(properties)
                .build();
        featureCollection.setCrs(crs);
        featureCollection.setFeatures(features);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = Paths.get(makeDirectory, String.format("wind_%s.json", date)).toFile();
            objectMapper.writeValue(jsonFile, featureCollection);
            log.info("@@@@ write png files : {}", jsonFile.getAbsolutePath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

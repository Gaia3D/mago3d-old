package gaia3d.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DatainfoTest {
    /**
     * datainfo.sql 파일 만들기
     *
     * @throws Exception
     */
    @Test
    void writeSqlFile() throws Exception {

        // 데이터 정보 (꼭 변경해야 되는 정보)
        String dataGroup = "factory";
        int dataGroupId = 10003;
        final int[] userId = {10000};
        String dataType = "collada";

        // 파일 경로 설정
        File jsonFile = new File("C:\\data\\mago3d\\f4d\\infra\\factory\\factory.json");
        File dataGroupPath = new File("C:\\data\\mago3d\\f4d\\infra\\factory");
        OutputStream outputStream= new FileOutputStream("C:\\data\\mago3d\\f4d\\infra\\factory\\insert_datainfo.sql");

        // json 파일 파싱
        List<Map<String, Object>> datasInfo = new ArrayList<Map<String, Object>>();
        datasInfo = jsonFile(dataGroup, jsonFile);

        // insert.sql 파일 쓰기
        Writer outputStreamWriter = new OutputStreamWriter(outputStream);
        StringBuffer stringBuffer  = new StringBuffer();

        stringBuffer.append("insert into data_info ( \n")
                .append("data_id, data_group_id, data_key, data_name, data_type, sharing, user_id, mapping_type, assemble, \n")
                .append("location, altitude, heading, pitch, roll, metainfo, status, label\n")
                .append(") VALUES \n");

        File[] files = dataGroupPath.listFiles();
        Stream<File> stream = null;
        if (files != null) {
            stream = Arrays.stream(files);
        }
        List<Map<String, Object>> DataInfos = datasInfo;

        if (stream != null) {
            stream.forEach(file -> {
                if (file.isDirectory()) {
                    int id = ++userId[0];
                    String dataName = file.getName().substring(4);
                    List<Map<String, Object>> dataInfo= DataInfos.stream().filter(t -> t.get("data_name").equals(dataName)).collect(Collectors.toList());
                    stringBuffer.append("(")
                            .append(id)
                            .append(", ")
                            .append(dataGroupId)
                            .append(", '")
                            .append(dataName)
                            .append("', '")
                            .append(dataName)
                            .append("', '")
                            .append(dataType)
                            .append("', 'public', 'admin', 'origin', 'false', \n")
                            .append("ST_GeomFromText('POINT(")
                            .append(dataInfo.get(0).get("longitude"))
                            .append(" ")
                            .append(dataInfo.get(0).get("latitude"))
                            .append(")', 4326), ")
                            .append(dataInfo.get(0).get("altitude"))
                            .append(", ")
                            .append(dataInfo.get(0).get("heading"))
                            .append(", ")
                            .append(dataInfo.get(0).get("pitch"))
                            .append(", ")
                            .append(dataInfo.get(0).get("roll"))
                            .append(", '")
                            .append(dataInfo.get(0).get("metainfo"))
                            .append("'::jsonb, 'use', '")
                            .append(dataName)
                            .append("'),\n");
                }
            });
        }
        stringBuffer.deleteCharAt(stringBuffer.length()-2);
        stringBuffer.deleteCharAt(stringBuffer.length()-1);
        stringBuffer.append(";");
        outputStreamWriter.write(stringBuffer.toString());
        outputStreamWriter.close();
    }

    /**
     * jsonFile을 읽어서, 데이터 단위로 저장
     *
     * @param dataGroup
     * @param jsonFile
     * @return
     * @throws IOException
     */
    private List<Map<String, Object>> jsonFile(String dataGroup, File jsonFile) throws IOException {
//        String dataGroup = "sea_port";
//        File jsonFile = new File("C:\\data\\mago3d\\f4d\\infra\\sea_port\\sea_port.json");

        List<Map<String, Object>> datasInfo = new ArrayList<Map<String, Object>>();

        byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile.getPath()));
        String encodingData = new String(jsonData, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();

        //read JSON like DOM Parser
        JsonNode jsonNode = mapper.readTree(encodingData);
        String groupDataKey = jsonNode.path("data_key").asText();

        if (groupDataKey.equals(dataGroup)) {
            JsonNode childrenNode = jsonNode.path("children");
            if (childrenNode.isArray() && childrenNode.size() != 0) {
                datasInfo = parseChildren(0, childrenNode);
            }
        } else {
            log.info("다른 그룹의 데이터이거나 json 파일입니다. 다시 확인해 주세요!!");
        }
        return datasInfo;
    }

    /**
     * 자식 data 들을 재귀적으로 파싱
     *
     * @param depth
     * @param childrenNode
     * @return
     */
    private List<Map<String, Object>> parseChildren(int depth, JsonNode childrenNode) {
        depth++;
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        int viewOrder = 0;
        for (JsonNode jsonNode : childrenNode) {
            Map<String, Object> datainfo = new HashMap<String, Object>();
            Long dataId = jsonNode.path("data_id").asLong();
            String dataName = jsonNode.path("data_name").asText();
            String dataKey = jsonNode.path("data_key").asText();
            String longitude = jsonNode.path("longitude").asText().trim();
            String latitude = jsonNode.path("latitude").asText().trim();
            String altitude = jsonNode.path("height").asText().trim();
            String heading = jsonNode.path("heading").asText().trim();
            String pitch = jsonNode.path("pitch").asText().trim();
            String roll = jsonNode.path("roll").asText().trim();
            String mappingType = jsonNode.path("mapping_type").asText();
            JsonNode metainfo = jsonNode.path("attributes");
            JsonNode childrene = jsonNode.path("children");
            datainfo.put("data_id", dataId);
            datainfo.put("data_name", dataName);
            datainfo.put("data_key", dataKey);
            datainfo.put("longitude", longitude);
            datainfo.put("latitude", latitude);
            datainfo.put("altitude", altitude);
            datainfo.put("heading", heading);
            datainfo.put("pitch", pitch);
            datainfo.put("roll", roll);
            datainfo.put("mappingType", mappingType);
            datainfo.put("metainfo", metainfo);
            datas.add(datainfo);
            viewOrder++;
        }
        log.info("childrenNode.size = {}, viewOrder = {}", childrenNode.size(), viewOrder);

        return datas;
    }

    /**
     * Json 파일 읽기 테스트
     *
     * @throws IOException
     */
    @Test
    void jsonFileTest() throws IOException {
        String dataGroup = "sea_port";
        File jsonFile = new File("C:\\data\\mago3d\\f4d\\infra\\sea_port\\sea_port.json");

        List<Map<String, Object>> datasInfo = new ArrayList<Map<String, Object>>();

        byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile.getPath()));
        String encodingData = new String(jsonData, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();

        //read JSON like DOM Parser
        JsonNode jsonNode = mapper.readTree(encodingData);
        String groupDataKey = jsonNode.path("data_key").asText();

        if (groupDataKey.equals(dataGroup)) {
            JsonNode childrenNode = jsonNode.path("children");
            if (childrenNode.isArray() && childrenNode.size() != 0) {
                datasInfo = parseChildren(0, childrenNode);
                System.out.println(datasInfo);
            }
        } else {
            log.info("다른 그룹의 데이터이거나 json 파일입니다. 다시 확인해 주세요!!");
        }
    }

    /**
     * 파일 읽기 테스트
     *
     * @throws Exception
     */
    @Test
    void readFileTest() throws Exception {
        File dataGroupPath = new File("C:\\data\\mago3d\\f4d\\infra\\sea_port");

        File[] files = dataGroupPath.listFiles();
        Stream<File> stream = Arrays.stream(files);
        stream.forEach(file -> {
            if (file.isDirectory()) {
                System.out.println(file.getName().substring(4));
            }
        });
    }

    /**
     * 파일 쓰기 테스트
     *
     * @throws IOException
     */
    @Test
    void writeFileTest() throws IOException {
        File dataGroupPath = new File("C:\\data\\mago3d\\f4d\\infra\\sea_port");
        OutputStream outputStream = new FileOutputStream("C:\\data\\mago3d\\f4d\\infra\\sea_port\\insert_datainfo.sql");

        Writer outputStreamWriter = new OutputStreamWriter(outputStream);
        File[] files = dataGroupPath.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                outputStreamWriter.write(file.getName().substring(4));
                outputStreamWriter.write("\n");
            }
        }
        outputStreamWriter.close();
    }
}
package gaia3d.domain.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.support.LogMessageSupport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface SimulationJsonImporter {
    Path getJsonPath();
    String getServicePath();
    default Map<?, ?> getJsonData(ObjectMapper objectMapper, Path filePath, String servicePath) {
        Map<?, ?> jsonData = null;
        try {
            jsonData = objectMapper.readValue(filePath.toFile(), Map.class);
            // serviceUri 추가
            List<Map<?, ?>> features = (List<Map<?, ?>>) jsonData.get("features");
            for (Map<?, ?> feature : features) {
                Map<?, ?> properties = (Map<?, ?>) feature.get("properties");
                Map<String, String> image = (Map<String, String>) properties.get("image");
                image.put("serviceUri", servicePath + image.get("uri"));
            }
        } catch (IOException e) {
            LogMessageSupport.printMessage(e);
        }
        return jsonData;
    }
}

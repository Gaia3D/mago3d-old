package gaia3d.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.domain.simulation.SimulationJsonImporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WeatherApiController {

    private final ObjectMapper objectMapper;

    protected ResponseEntity<?> getJsonData(SimulationJsonImporter importer) {
        Path filePath = importer.getJsonPath();
        String servicePath = importer.getServicePath();
        Map<?, ?> jsonData = importer.getJsonData(objectMapper, filePath, servicePath);
        if (jsonData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(jsonData);
    }

}

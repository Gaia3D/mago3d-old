package gaia3d.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.SimulationJsonImporter;
import gaia3d.domain.simulation.pm25.SimulationPm25Dto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/pm25", produces = MediaTypes.HAL_JSON_VALUE)
public class ParticulateMatterApiController extends WeatherApiController {

    private final PropertiesConfig propertiesConfig;

    public ParticulateMatterApiController(ObjectMapper objectMapper, PropertiesConfig propertiesConfig) {
        super(objectMapper);
        this.propertiesConfig = propertiesConfig;
    }

    @GetMapping
    public ResponseEntity<?> getSimulationPm25(SimulationPm25Dto param) {
        SimulationJsonImporter importer = param.toSimulationPm25(propertiesConfig);
        return getJsonData(importer);
    }

}

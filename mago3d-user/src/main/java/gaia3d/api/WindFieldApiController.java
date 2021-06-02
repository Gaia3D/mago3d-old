package gaia3d.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.SimulationJsonImporter;
import gaia3d.domain.simulation.wind.SimulationWindDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/wind", produces = MediaTypes.HAL_JSON_VALUE)
public class WindFieldApiController extends WeatherApiController {

    private final PropertiesConfig propertiesConfig;

    public WindFieldApiController(ObjectMapper objectMapper, PropertiesConfig propertiesConfig) {
        super(objectMapper);
        this.propertiesConfig = propertiesConfig;
    }

    @GetMapping
    public ResponseEntity<?> getSimulationWind(SimulationWindDto param) {
        SimulationJsonImporter importer = param.toSimulationWind(propertiesConfig);
        return getJsonData(importer);
    }

}

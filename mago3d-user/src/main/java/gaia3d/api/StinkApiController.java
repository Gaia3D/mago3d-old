package gaia3d.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.config.PropertiesConfig;
import gaia3d.domain.simulation.SimulationJsonImporter;
import gaia3d.domain.simulation.stink.SimulationStinkDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/stink", produces = MediaTypes.HAL_JSON_VALUE)
public class StinkApiController extends WeatherApiController {

    private final PropertiesConfig propertiesConfig;

    public StinkApiController(ObjectMapper objectMapper, PropertiesConfig propertiesConfig) {
        super(objectMapper);
        this.propertiesConfig = propertiesConfig;
    }

    @GetMapping
    public ResponseEntity<?> getSimulationStink(SimulationStinkDto param) {
        SimulationJsonImporter importer = param.toSimulationStink(propertiesConfig);
        return getJsonData(importer);
    }

}

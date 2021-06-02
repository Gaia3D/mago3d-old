package gaia3d.api;

import gaia3d.domain.Key;
import gaia3d.domain.simulation.SimulationTimeSeriesLog;
import gaia3d.domain.simulation.SimulationTimeSeriesLogDto;
import gaia3d.domain.user.UserSession;
import gaia3d.service.SimulationTimeSeriesLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/simulation-time-series-logs", produces = MediaTypes.HAL_JSON_VALUE)
public class SimulationTimeSeriesLogApiController {

    private final SimulationTimeSeriesLogService simulationTimeSeriesLogService;
    private final ModelMapper modelMapper;

    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<SimulationTimeSeriesLogDto>> getSimulationTimeSeriesLog(@PathVariable("id") Integer id) {

        SimulationTimeSeriesLog simulationTimeSeriesLog = simulationTimeSeriesLogService.getSimulationTimeSeriesLog(id);
        if (simulationTimeSeriesLog == null) {
            log.info("@@ getSimulationTimeSeries. SimulationTimeSeries not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        SimulationTimeSeriesLogDto dto = modelMapper.map(simulationTimeSeriesLog, SimulationTimeSeriesLogDto.class);
        EntityModel<SimulationTimeSeriesLogDto> entityModel = EntityModel.of(dto);

        entityModel.add(linkTo(SimulationTimeSeriesLogApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-logs-get").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> insertSimulationTimeSeriesLog(@RequestBody @Valid SimulationTimeSeriesLog simulationTimeSeriesLog,
                                                        HttpServletRequest request,
                                                        Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeriesLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        int id = simulationTimeSeriesLogService.insertSimulationTimeSeriesLog(simulationTimeSeriesLog);
        EntityModel<SimulationTimeSeriesLogDto> entityModel = EntityModel
                .of(modelMapper.map(simulationTimeSeriesLogService.getSimulationTimeSeriesLog(id), SimulationTimeSeriesLogDto.class))
                .add(linkTo(SimulationTimeSeriesLogApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(SimulationTimeSeriesLogApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-logs-create").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> updateSimulationTimeSeriesLog(@PathVariable("id") Integer id,
                                                        @RequestBody @Valid SimulationTimeSeriesLog simulationTimeSeriesLog,
                                                        HttpServletRequest request,
                                                        Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeriesLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        if (simulationTimeSeriesLogService.updateSimulationTimeSeriesLog(simulationTimeSeriesLog) == 0) {
            log.info("@@ updateSimulationTimeSeries. SimulationTimeSeries not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        SimulationTimeSeriesLog savedSimulationTimeSeriesLog = simulationTimeSeriesLogService.getSimulationTimeSeriesLog(id);
        EntityModel<SimulationTimeSeriesLogDto> entityModel = EntityModel
                .of(modelMapper.map(savedSimulationTimeSeriesLog, SimulationTimeSeriesLogDto.class))
                .add(linkTo(SimulationTimeSeriesLogApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(SimulationTimeSeriesLogApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-logs-update").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> deleteSimulationTimeSeriesLog(@PathVariable("id") Integer id, HttpServletRequest request) {

        SimulationTimeSeriesLog simulationTimeSeriesLog = new SimulationTimeSeriesLog();
        simulationTimeSeriesLog.setSimulationTimeSeriesId(id);

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeriesLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        if (simulationTimeSeriesLogService.deleteSimulationTimeSeriesLog(simulationTimeSeriesLog) == 0) {
            log.info("@@ deleteSimulationTimeSeries. SimulationTimeSeries not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> badRequest(Errors errors) {
        Map<String, Object> result = new HashMap<>();
        int statusCode = HttpStatus.BAD_REQUEST.value();
        String field = errors.getFieldErrors().get(0).getField();
        String message = errors.getFieldErrors().get(0).getDefaultMessage();

        result.put("statusCode", statusCode);
        result.put("errorCode", errors.getFieldErrors().get(0).getCode());
        result.put("message", "field: " + field + ", message: " + message);

        return ResponseEntity.badRequest().body(result);
    }

}

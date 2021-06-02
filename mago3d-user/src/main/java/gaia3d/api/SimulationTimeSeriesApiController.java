package gaia3d.api;

import gaia3d.domain.Key;
import gaia3d.domain.common.Pagination;
import gaia3d.domain.simulation.SimulationTimeSeries;
import gaia3d.domain.simulation.SimulationTimeSeriesDto;
import gaia3d.domain.user.UserSession;
import gaia3d.service.SimulationTimeSeriesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/simulation-time-series", produces = MediaTypes.HAL_JSON_VALUE)
public class SimulationTimeSeriesApiController {

    private final SimulationTimeSeriesService simulationTimeSeriesService;
    private final ModelMapper modelMapper;
    private static final long PAGE_LIST_COUNT = 5L;

    // TODO : 임시로 작성, 페이징 처리 완료 시 삭제 예정
//    @GetMapping(value = "count", produces = "application/json; charset=UTF-8")
//    public ResponseEntity<?> getTotalCount(SimulationTimeSeries simulationTimeSeries) {
//        int totalCount = simulationTimeSeriesService.getSimulationTimeSeriesTotalCount(simulationTimeSeries);
//
//        EntityModel<Integer> entityModel = EntityModel.of(totalCount);
//
//        entityModel.add(linkTo(SimulationTimeSeriesApiController.class).withSelfRel());
//        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-list-count").withRel("profile"));
//
//        return ResponseEntity.ok(entityModel);
//    }

    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<SimulationTimeSeriesDto>>> getListSimulationTimeSeries(
            SimulationTimeSeries simulationTimeSeries,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "10") Long pageRows) {

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeries.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        long totalCount = simulationTimeSeriesService.getSimulationTimeSeriesTotalCount(simulationTimeSeries);
        Pagination pagination = new Pagination(totalCount, pageNo, pageRows, PAGE_LIST_COUNT);
        log.info("@@ pagination = {}", pagination);

        simulationTimeSeries.setOffset(pagination.getOffset());
        simulationTimeSeries.setLimit(pagination.getPageRows());
        List<SimulationTimeSeries> simulationTimeSeriesList = new ArrayList<>();
        if (totalCount > 0L) {
            simulationTimeSeriesList = simulationTimeSeriesService.getListSimulationTimeSeries(simulationTimeSeries);
        }

        List<EntityModel<SimulationTimeSeriesDto>> entityModels = simulationTimeSeriesList.stream()
                .map(f -> EntityModel.of(modelMapper.map(f, SimulationTimeSeriesDto.class))
                        .add(linkTo(SimulationTimeSeriesApiController.class).slash(f.getSimulationTimeSeriesId()).withSelfRel()))
                .collect(Collectors.toList());

//        CollectionModel<EntityModel<SimulationTimeSeriesDto>> model = CollectionModel.of(entityModels);
        // size, number, totalElements, totalPages
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pageRows,
                pageNo,
                pagination.getTotalCount(),
                pagination.getLastPage()
        );
        PagedModel<EntityModel<SimulationTimeSeriesDto>> pagedModel = PagedModel.of(entityModels, metadata);

        pagedModel.add(linkTo(SimulationTimeSeriesApiController.class).withSelfRel());
        pagedModel.add(Link.of("/docs/index.html#resources-simulation-time-series-list").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<SimulationTimeSeriesDto>> getSimulationTimeSeries(@PathVariable("id") Integer id) {

        SimulationTimeSeries simulationTimeSeries = simulationTimeSeriesService.getSimulationTimeSeries(id);
        if (simulationTimeSeries == null) {
            log.info("@@ getSimulationTimeSeries. SimulationTimeSeries not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        SimulationTimeSeriesDto dto = modelMapper.map(simulationTimeSeries, SimulationTimeSeriesDto.class);
        EntityModel<SimulationTimeSeriesDto> entityModel = EntityModel.of(dto);

        entityModel.add(linkTo(SimulationTimeSeriesApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-get").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> insertSimulationTimeSeries(@RequestBody @Valid SimulationTimeSeries simulationTimeSeries,
                                                        HttpServletRequest request,
                                                        Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeries.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        int id = simulationTimeSeriesService.insertSimulationTimeSeries(simulationTimeSeries);
        EntityModel<SimulationTimeSeriesDto> entityModel = EntityModel
                .of(modelMapper.map(simulationTimeSeriesService.getSimulationTimeSeries(id), SimulationTimeSeriesDto.class))
                .add(linkTo(SimulationTimeSeriesApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(SimulationTimeSeriesApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-create").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> updateSimulationTimeSeries(@PathVariable("id") Integer id,
                                                        @RequestBody @Valid SimulationTimeSeries simulationTimeSeries,
                                                        HttpServletRequest request,
                                                        Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeries.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        if (simulationTimeSeriesService.updateSimulationTimeSeries(simulationTimeSeries) == 0) {
            log.info("@@ updateSimulationTimeSeries. SimulationTimeSeries not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        SimulationTimeSeries savedSimulationTimeSeries = simulationTimeSeriesService.getSimulationTimeSeries(id);
        EntityModel<SimulationTimeSeriesDto> entityModel = EntityModel
                .of(modelMapper.map(savedSimulationTimeSeries, SimulationTimeSeriesDto.class))
                .add(linkTo(SimulationTimeSeriesApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(SimulationTimeSeriesApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-time-series-update").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> deleteSimulationTimeSeries(@PathVariable("id") Integer id, HttpServletRequest request) {

        SimulationTimeSeries simulationTimeSeries = new SimulationTimeSeries();
        simulationTimeSeries.setSimulationTimeSeriesId(id);

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationTimeSeries.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        if (simulationTimeSeriesService.deleteSimulationTimeSeries(simulationTimeSeries) == 0) {
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
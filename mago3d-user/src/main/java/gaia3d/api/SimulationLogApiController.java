package gaia3d.api;

import gaia3d.domain.Key;
import gaia3d.domain.common.Pagination;
import gaia3d.domain.simulation.SimulationLog;
import gaia3d.domain.simulation.SimulationLogDto;
import gaia3d.domain.user.UserSession;
import gaia3d.service.SimulationLogService;
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
@RequestMapping(value = "/api/simulation-logs", produces = MediaTypes.HAL_JSON_VALUE)
public class SimulationLogApiController {

    private final SimulationLogService simulationLogService;
    private final ModelMapper modelMapper;
    private static final long PAGE_LIST_COUNT = 5L;

    // TODO : 임시로 작성, 페이징 처리 완료 시 삭제 예정
//    @GetMapping(value = "count", produces = "application/json; charset=UTF-8")
//    public ResponseEntity<?> getTotalCount(SimulationLog simulationLog) {
//        long totalCount = simulationLogService.getSimulationLogTotalCount(simulationLog);
//
//        EntityModel<Long> entityModel = EntityModel.of(totalCount);
//
//        entityModel.add(linkTo(SimulationLogApiController.class).withSelfRel());
//        entityModel.add(Link.of("/docs/index.html#resources-simulation-log-list-count").withRel("profile"));
//
//        return ResponseEntity.ok(entityModel);
//    }

    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<SimulationLogDto>>> getListSimulationLog(
            SimulationLog simulationLog,
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "10") Long pageRows) {

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        long totalCount = simulationLogService.getSimulationLogTotalCount(simulationLog);
        Pagination pagination = new Pagination(totalCount, pageNo, pageRows, PAGE_LIST_COUNT);
        log.info("@@ pagination = {}", pagination);

        simulationLog.setOffset(pagination.getOffset());
        simulationLog.setLimit(pagination.getPageRows());
        List<SimulationLog> simulationLogList = new ArrayList<>();
        if (totalCount > 0L) {
            simulationLogList = simulationLogService.getListSimulationLog(simulationLog);
        }

        List<EntityModel<SimulationLogDto>> entityModels = simulationLogList.stream()
                .map(f -> EntityModel.of(modelMapper.map(f, SimulationLogDto.class))
                        .add(linkTo(SimulationLogApiController.class).slash(f.getSimulationLogId()).withSelfRel()))
                .collect(Collectors.toList());

//        CollectionModel<EntityModel<SimulationLogDto>> collectionModel = CollectionModel.of(entityModels);
        // size, number, totalElements, totalPages
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pageRows,
                pageNo,
                pagination.getTotalCount(),
                pagination.getLastPage()
        );
        PagedModel<EntityModel<SimulationLogDto>> pagedModel = PagedModel.of(entityModels, metadata);

        pagedModel.add(linkTo(SimulationLogApiController.class).withSelfRel());
        pagedModel.add(Link.of("/docs/index.html#resources-simulation-log-list").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<SimulationLogDto>> getSimulationLog(@PathVariable("id") Long id) {

        SimulationLog simulationLog = simulationLogService.getSimulationLog(id);
        if (simulationLog == null) {
            log.info("@@ getSimulationLog. SimulationLog not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        SimulationLogDto dto = modelMapper.map(simulationLog, SimulationLogDto.class);
        EntityModel<SimulationLogDto> entityModel = EntityModel.of(dto);

        entityModel.add(linkTo(SimulationLogApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-log-get").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> insertSimulationLog(@RequestBody @Valid SimulationLog simulationLog,
                                                 HttpServletRequest request,
                                                 Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        Long id = simulationLogService.insertSimulationLog(simulationLog);
        EntityModel<SimulationLogDto> entityModel = EntityModel
                .of(modelMapper.map(simulationLogService.getSimulationLog(id), SimulationLogDto.class))
                .add(linkTo(SimulationLogApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(SimulationLogApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-log-create").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> updateSimulationLog(@PathVariable("id") Long id,
                                                 @RequestBody @Valid SimulationLog simulationLog,
                                                 HttpServletRequest request,
                                                 Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        if (simulationLogService.updateSimulationLog(simulationLog) == 0) {
            log.info("@@ updateSimulationLog. SimulationLog not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        SimulationLog savedSimulationLog = simulationLogService.getSimulationLog(id);
        EntityModel<SimulationLogDto> entityModel = EntityModel
                .of(modelMapper.map(savedSimulationLog, SimulationLogDto.class))
                .add(linkTo(SimulationLogApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(SimulationLogApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-simulation-log-update").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> deleteSimulationLog(@PathVariable("id") Long id, HttpServletRequest request) {

        SimulationLog simulationLog = new SimulationLog();
        simulationLog.setSimulationLogId(id);

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        simulationLog.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());

        if (simulationLogService.deleteSimulationLog(simulationLog) == 0) {
            log.info("@@ deleteSimulationLog. SimulationLog not found with id = {}", id);
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
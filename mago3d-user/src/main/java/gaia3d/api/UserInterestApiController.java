package gaia3d.api;

import gaia3d.domain.Key;
import gaia3d.domain.interest.UserInterest;
import gaia3d.domain.interest.UserInterestDto;
import gaia3d.domain.user.UserSession;
import gaia3d.service.UserInterestService;
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
@RequestMapping(value = "/api/user-interests", produces = MediaTypes.HAL_JSON_VALUE)
public class UserInterestApiController {

    private final UserInterestService userInterestService;
    private final ModelMapper modelMapper;
    private static final long PAGE_LIST_COUNT = 5L;

    /**
     * 사용자 즐겨찾기 목록 조회
     * @param userInterest 사용자 즐겨찾기
     * @param request request
     * @param pageNo 페이지 번호
     * @param pageRows 페이지 행갯수
     * @return 사용자 즐겨찾기 목록
     */
//    @GetMapping(produces = "application/json; charset=UTF-8")
//    public ResponseEntity<CollectionModel<EntityModel<UserInterestDto>>> getListUserInterest(UserInterest userInterest, HttpServletRequest request,
//            @RequestParam(defaultValue = "1") Long pageNo, @RequestParam(defaultValue = "10") Long pageRows) {
//
//        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
//        userInterest.setUserId(userSession.getUserId());
//        log.info("@@ session userId = {}", userSession.getUserId());
//
//        int totalCount = userInterestService.getListUserInterestTotalCount(userInterest);
//        Pagination pagination = new Pagination(totalCount, pageNo, pageRows, PAGE_LIST_COUNT);
//        log.info("@@ pagination = {}", pagination);
//
//        userInterest.setOffset(pagination.getOffset());
//        userInterest.setLimit(pagination.getPageRows());
//        List<UserInterest> userInterestList = new ArrayList<>();
//        if (totalCount > 0L) {
//            userInterestList = userInterestService.getListUserInterest(userInterest);
//        }
//
//        List<EntityModel<UserInterestDto>> entityModels = userInterestList.stream()
//                .map(f -> EntityModel.of(modelMapper.map(f, UserInterestDto.class))
//                        .add(linkTo(UserInterestApiController.class).slash(f.getUserInterestId()).withSelfRel()))
//                .collect(Collectors.toList());
//
//        // size, number, totalElements, totalPages
//        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(pageRows, pageNo, pagination.getTotalCount(), pagination.getLastPage());
//        PagedModel<EntityModel<UserInterestDto>> pagedModel = PagedModel.of(entityModels, metadata);
//
//        pagedModel.add(linkTo(UserInterestApiController.class).withSelfRel());
//        pagedModel.add(Link.of("/docs/index.html#resources-user-interest-list").withRel("profile"));
//
//        return ResponseEntity.ok(pagedModel);
//    }

    /**
     * 사용자 즐겨찾기 조회
     * @param userInterestSearch 검색용 파라미터
     * @return 사용자 즐겨찾기
     */
    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<UserInterestDto>> getUserInterest(UserInterest userInterestSearch) {

        UserInterest userInterest = userInterestService.getUserInterest(userInterestSearch);
        Integer id = userInterestSearch.getUserInterestId();
        if (userInterest == null) {
            log.info("@@ getUserInterest. getUserInterest not found with id = {}", id);
            return ResponseEntity.notFound().build();
        }

        UserInterestDto dto = modelMapper.map(userInterest, UserInterestDto.class);
        EntityModel<UserInterestDto> entityModel = EntityModel.of(dto);

        entityModel.add(linkTo(UserInterestApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-user-interest-get").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    /**
     * 사용자 즐겨찾기 등록
     * @param userInterest 등록할 사용자 즐겨찾기
     * @param request request
     * @param errors errors
     * @return 사용자 즐겨찾기 등록 결과
     */
    @PostMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<?> update(@RequestBody @Valid UserInterest userInterest, HttpServletRequest request, Errors errors) {

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        userInterest.setUserId(userSession.getUserId());
        log.info("@@ session userId = {}", userSession.getUserId());
        log.info("@@ userInterest = {}", userInterest);
        int id = userInterestService.update(userInterest);
        log.info("@@ updated id = {}", id);
        EntityModel<UserInterestDto> entityModel = EntityModel
                .of(modelMapper.map(userInterestService.getUserInterest(userInterest), UserInterestDto.class))
                .add(linkTo(UserInterestApiController.class).slash(id).withSelfRel());

        entityModel.add(linkTo(UserInterestApiController.class).slash(id).withSelfRel());
        entityModel.add(Link.of("/docs/index.html#resources-user-interest-create").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

//    /**
//     * 사용자 즐겨찾기 갱신
//     * @param id 사용자 즐겨찾기 고유번호
//     * @param userInterest 사용자 즐겨찾기
//     * @param request request
//     * @param errors errors
//     * @return 사용자 즐겨찾기 갱신 결과
//     */
//    @PutMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
//    public ResponseEntity<?> updateUserInterest(@PathVariable("id") Integer id, @RequestBody @Valid UserInterest userInterest, HttpServletRequest request, Errors errors) {
//
//        if (errors.hasErrors()) {
//            return badRequest(errors);
//        }
//
//        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
//        userInterest.setUserId(userSession.getUserId());
//        log.info("@@ session userId = {}", userSession.getUserId());
//
//        if (userInterestService.updateUserInterest(userInterest) == 0) {
//            log.info("@@ updateSimulationTimeSeries. SimulationTimeSeries not found with id = {}", id);
//            return ResponseEntity.notFound().build();
//        }
//
//        UserInterest savedUserInterest = userInterestService.getUserInterestById(id);
//        EntityModel<UserInterestDto> entityModel = EntityModel
//                .of(modelMapper.map(savedUserInterest, UserInterestDto.class))
//                .add(linkTo(UserInterestApiController.class).slash(id).withSelfRel());
//
//        entityModel.add(linkTo(UserInterestApiController.class).slash(id).withSelfRel());
//        entityModel.add(Link.of("/docs/index.html#resources-user-interest-update").withRel("profile"));
//
//        return ResponseEntity.ok(entityModel);
//    }
//
//    /**
//     * 사용자 즐겨찾기 삭제
//     * @param id 사용자 즐겨찾기 고유번호
//     * @param request request
//     * @return 사용자 즐겨찾기 삭제 결과
//     */
//    @DeleteMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
//    public ResponseEntity<?> deleteUserInterest(@PathVariable("id") Integer id, HttpServletRequest request) {
//
//        UserInterest userInterest = new UserInterest();
//        userInterest.setUserInterestId(id);
//
//        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
//        userInterest.setUserId(userSession.getUserId());
//        log.info("@@ session userId = {}", userSession.getUserId());
//
//        if (userInterestService.deleteUserInterest(userInterest) == 0) {
//            log.info("@@ deleteUserInterest. UserInterest not found with id = {}", id);
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.noContent().build();
//    }

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

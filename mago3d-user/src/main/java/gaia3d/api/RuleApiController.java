package gaia3d.api;

import gaia3d.domain.common.Pagination;
import gaia3d.domain.rule.Rule;
import gaia3d.service.RuleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/rules", produces = MediaTypes.HAL_JSON_VALUE)
public class RuleApiController {

    private final RuleService ruleService;
    private static final long PAGE_LIST_COUNT = 5L;

    /**
     * rule 목록 조회
     *
     * @return
     */
    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<Rule>>> getRules(
            Rule rule,
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "10") Long pageRows
    ) {

        long totalCount = ruleService.getRuleTotalCount(rule);
        Pagination pagination = new Pagination(totalCount, pageNo, pageRows, PAGE_LIST_COUNT);
        log.info("@@ pagination = {}", pagination);

        rule.setOffset(pagination.getOffset());
        rule.setLimit(pagination.getPageRows());
        List<Rule> ruleList = new ArrayList<>();
        if(totalCount > 0L) {
            ruleList = ruleService.getListRule(rule);
        }

        List<EntityModel<Rule>> ruleDtoList = ruleList.stream()
                .map(f -> EntityModel.of(f)
                        .add(linkTo(RuleApiController.class).slash(f.getRuleId()).withSelfRel()))
                .collect(Collectors.toList());

        // size, number, totalElements, totalPages
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pageRows,
                pageNo,
                pagination.getTotalCount(),
                pagination.getLastPage()
        );
        PagedModel<EntityModel<Rule>> pagedModel = PagedModel.of(ruleDtoList, metadata);

        pagedModel.add(linkTo(RuleApiController.class).withSelfRel());
        pagedModel.add(Link.of("/docs/index.html#resources-rule-list").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * rule 한건 조회
     *
     * @param id rule 아이디
     * @return
     */
    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<Rule>> getRuleById(@PathVariable("id") Integer id) {
        Rule rule = ruleService.getRule(id);
        EntityModel<Rule> ruleEntity = EntityModel.of(rule);
        ruleEntity.add(linkTo(RuleApiController.class).slash(id).withSelfRel());
        ruleEntity.add(Link.of("/docs/index.html#resources-rule-get").withRel("profile"));

        return ResponseEntity.ok(ruleEntity);
    }
}

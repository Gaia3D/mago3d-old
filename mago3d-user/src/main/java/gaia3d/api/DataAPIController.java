package gaia3d.api;


import gaia3d.domain.common.Pagination;
import gaia3d.domain.data.DataInfo;
import gaia3d.domain.data.DataInfoDto;
import gaia3d.service.DataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
@RequestMapping(value = "/api/datas", produces = MediaTypes.HAL_JSON_VALUE)
public class DataApiController {

    private final DataService dataService;
    private final ModelMapper modelMapper;
    private static final long PAGE_LIST_COUNT = 5L;

    /**
     * 데이터 목록 조회
     *
     * @return
     */
    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<DataInfoDto>>> getDatas(
            DataInfo dataInfo,
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "10") Long pageRows
            ) {

        long totalCount = dataService.getDataTotalCount(dataInfo);
        Pagination pagination = new Pagination(totalCount, pageNo, pageRows, PAGE_LIST_COUNT);
        log.info("@@ pagination = {}", pagination);

        dataInfo.setOffset(pagination.getOffset());
        dataInfo.setLimit(pagination.getPageRows());
        List<DataInfo> dataInfoList = new ArrayList<>();
        if(totalCount > 0L) {
            dataInfoList = dataService.getListData(dataInfo);
        }

        List<EntityModel<DataInfoDto>> dataInfoDtoList = dataInfoList.stream()
                .map(f -> EntityModel.of(modelMapper.map(f, DataInfoDto.class))
                        .add(linkTo(DataApiController.class).slash(f.getDataId()).withSelfRel()))
                .collect(Collectors.toList());

//        CollectionModel<EntityModel<DataInfoDto>> model = CollectionModel.of(dataInfoDtoList);
        // size, number, totalElements, totalPages
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pageRows,
                pageNo,
                pagination.getTotalCount(),
                pagination.getLastPage()
        );
        PagedModel<EntityModel<DataInfoDto>> pagedModel = PagedModel.of(dataInfoDtoList, metadata);

        pagedModel.add(linkTo(DataApiController.class).withSelfRel());
        pagedModel.add(Link.of("/docs/index.html#resources-data-info-list").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    /**
     * 데이터 한건 조회
     *
     * @param id 데이터 아이디
     * @return
     */
    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<DataInfoDto>> getDataById(@PathVariable("id") Long id) {
    	DataInfo dInfo = new DataInfo();
		dInfo.setDataId(id);
        DataInfoDto dto = modelMapper.map(dataService.getData(dInfo), DataInfoDto.class);
        EntityModel<DataInfoDto> dataInfo = EntityModel.of(dto);
        dataInfo.add(linkTo(DataApiController.class).slash(id).withSelfRel());
        dataInfo.add(Link.of("/docs/index.html#resources-data-info-get").withRel("profile"));

        return ResponseEntity.ok(dataInfo);
    }
}

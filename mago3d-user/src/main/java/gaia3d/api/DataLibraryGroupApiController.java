package gaia3d.api;

import gaia3d.domain.datalibrary.DataLibraryGroup;
import gaia3d.domain.datalibrary.DataLibraryGroupDto;
import gaia3d.service.DataLibraryGroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * 3D 라이브러리 그룹
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/data-library-groups", produces = MediaTypes.HAL_JSON_VALUE)
public class DataLibraryGroupApiController {

    private final DataLibraryGroupService dataLibraryGroupService;
    private final ModelMapper modelMapper;

    /**
     * 데이터 라이브러리 그룹 목록 조회
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<DataLibraryGroupDto>>> getDataLibraryGroups() {
        List<EntityModel<DataLibraryGroupDto>> dataLibraryGroupList  = dataLibraryGroupService.getListDataLibraryGroup(new DataLibraryGroup())
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, DataLibraryGroupDto.class))
                        .add(linkTo(DataLibraryGroupApiController.class).slash(f.getDataLibraryGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DataLibraryGroupDto>> model = CollectionModel.of(dataLibraryGroupList);

        model.add(linkTo(DataLibraryGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-data-library-group-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    /**
     * 데이터 라이브러리 그룹 한건 조회
     *
     * @param id 데이터 라이브러리 그룹 아이디
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DataLibraryGroupDto>> getDataLibraryGroupById(@PathVariable("id") Integer id) {
        DataLibraryGroupDto dto = modelMapper.map(dataLibraryGroupService.getDataLibraryGroup(
                DataLibraryGroup.builder().dataLibraryGroupId(id).build()), DataLibraryGroupDto.class);
        EntityModel<DataLibraryGroupDto> dataLibraryGroup = EntityModel.of(dto);
        dataLibraryGroup.add(linkTo(DataLibraryGroupApiController.class).slash(id).withSelfRel());
        dataLibraryGroup.add(Link.of("/docs/index.html#resources-data-library-group-get").withRel("profile"));

        return ResponseEntity.ok(dataLibraryGroup);
    }

    /**
     * depth 에 대한 데이터 라이브러리 그릅 목록 조회
     * @param id
     * @return
     */
    @GetMapping("/depth/{id}")
    public ResponseEntity<CollectionModel<EntityModel<DataLibraryGroupDto>>> getDataLibraryGroupByDepth(@PathVariable("id") Integer id) {
        List<EntityModel<DataLibraryGroupDto>> urbanGroupList = dataLibraryGroupService.getDataLibraryGroupListByDepth(
                DataLibraryGroup.builder().dataLibraryGroupId(id).build())
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, DataLibraryGroupDto.class))
                        .add(linkTo(DataLibraryGroupApiController.class).slash(f.getDataLibraryGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DataLibraryGroupDto>> model = CollectionModel.of(urbanGroupList);

        model.add(linkTo(DataLibraryGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-data-library-group-depth").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    /**
     * parent 에 대한 데이터 라이브러리 그룹 목록 조회
     * @param id
     * @return
     */
    @GetMapping("/parent/{id}")
    public ResponseEntity<CollectionModel<EntityModel<DataLibraryGroupDto>>> getDataLibraryGroupByParent(@PathVariable("id") Integer id) {
        List<EntityModel<DataLibraryGroupDto>> urbanGroupList = dataLibraryGroupService.getListDataLibraryGroup(
                DataLibraryGroup.builder().dataLibraryGroupId(id).build())
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, DataLibraryGroupDto.class))
                        .add(linkTo(DataLibraryGroupApiController.class).slash(f.getDataLibraryGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DataLibraryGroupDto>> model = CollectionModel.of(urbanGroupList);

        model.add(linkTo(DataLibraryGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-data-library-group-parent").withRel("profile"));

        return ResponseEntity.ok(model);
    }
}

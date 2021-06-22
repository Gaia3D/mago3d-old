package gaia3d.api;

import gaia3d.domain.Key;
import gaia3d.domain.data.DataGroup;
import gaia3d.domain.data.DataGroupDto;
import gaia3d.domain.user.UserSession;
import gaia3d.service.DataGroupService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/data-groups", produces = MediaTypes.HAL_JSON_VALUE)
public class DataGroupApiController {

    @Autowired
    private final DataGroupService dataGroupService;
    private final ModelMapper modelMapper;

    /**
     * 데이터 그룹 목록 조회
     *
     * @return
     */
    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<DataGroupDto>>> getDataGroups(HttpServletRequest request) {
        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        List<EntityModel<DataGroupDto>> dataGroupList = dataGroupService.getAllListDataGroup(DataGroup.builder()
                .userGroupId(userSession.getUserGroupId())
                .build())
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, DataGroupDto.class))
                        .add(linkTo(DataGroupApiController.class).slash(f.getDataGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DataGroupDto>> model = CollectionModel.of(dataGroupList);

        model.add(linkTo(DataGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-data-group-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    /**
     * 데이터 그룹 한건 조회
     *
     * @param id 데이터 그룹 아이디
     * @return
     */
    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<DataGroupDto>> getDataGroupById(@PathVariable("id") Integer id) {

        DataGroup dGroup = new DataGroup();
        dGroup.setDataGroupId(id);

        DataGroupDto dto = modelMapper.map(dataGroupService.getDataGroup(dGroup), DataGroupDto.class);
        EntityModel<DataGroupDto> dataGroup = EntityModel.of(dto);
        dataGroup.add(linkTo(DataGroupApiController.class).slash(id).withSelfRel());
        dataGroup.add(Link.of("/docs/index.html#resources-data-group-get").withRel("profile"));

        return ResponseEntity.ok(dataGroup);
    }

    /**
     * parent 에 대한 데이터 그룹 목록 조회
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/parent/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<DataGroupDto>>> getDataGroupByParent(@PathVariable("id") Integer id) {
        List<EntityModel<DataGroupDto>> dataGroupList = dataGroupService.getListDataGroupByPatent(
                DataGroup.builder().dataGroupId(id).build())
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, DataGroupDto.class))
                        .add(linkTo(DataGroupApiController.class).slash(f.getDataGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<DataGroupDto>> model = CollectionModel.of(dataGroupList);

        model.add(linkTo(DataGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-data-group-parent").withRel("profile"));

        return ResponseEntity.ok(model);
    }
}

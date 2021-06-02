package gaia3d.api;

import gaia3d.domain.Key;
import gaia3d.domain.layer.LayerGroup;
import gaia3d.domain.layer.LayerGroupDto;
import gaia3d.domain.user.UserSession;
import gaia3d.service.LayerGroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/layer-groups", produces = MediaTypes.HAL_JSON_VALUE)
public class LayerGroupApiController {

    private final LayerGroupService layerGroupService;
    private final ModelMapper modelMapper;

    /**
     * 레이어 그룹 목록 조회
     *
     * @return
     */
    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<LayerGroupDto>>> getLayerGroups(HttpServletRequest request, @RequestParam(required = false) Integer urbanGroupId) {
        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        CollectionModel<EntityModel<LayerGroupDto>> model = getLayerGroupList(LayerGroup.builder()
                //.urbanGroupId(urbanGroupId)
                .userGroupId(userSession.getUserGroupId())
                .build());

        model.add(linkTo(LayerGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-layer-group-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    /**
     * 레이어 그룹 목록 조회
     *
     * @return
     */
    @GetMapping(value = "/recursive", produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<LayerGroupDto>>> getLayerGroupsWithChild(HttpServletRequest request) {
        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        List<EntityModel<LayerGroupDto>> layerGroupList = layerGroupService.getListLayerGroupAndLayer(LayerGroup.builder()
                .userGroupId(userSession.getUserGroupId()).build())
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, LayerGroupDto.class))
                        .add(linkTo(LayerGroupApiController.class).slash(f.getLayerGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<LayerGroupDto>> model = CollectionModel.of(layerGroupList);

        model.add(linkTo(LayerGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-layer-group-list-recursive").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    /**
     * 레이어 그룹 한건 조회
     *
     * @param id 레이어 그룹 아이디
     * @return
     */
    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<LayerGroupDto>> getLayerGroupById(@PathVariable("id") Integer id) {
        LayerGroupDto dto = modelMapper.map(layerGroupService.getLayerGroup(id), LayerGroupDto.class);
        EntityModel<LayerGroupDto> layerGroup = EntityModel.of(dto);
        layerGroup.add(linkTo(LayerGroupApiController.class).slash(id).withSelfRel());
        layerGroup.add(Link.of("/docs/index.html#resources-layer-group-get").withRel("profile"));

        return ResponseEntity.ok(layerGroup);
    }

    /**
     * parent 에 대한 레이어 그룹 목록 조회
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/parent/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<LayerGroupDto>>> getLayerGroupByParent(HttpServletRequest request, @PathVariable("id") Integer id) {
        UserSession userSession = (UserSession) request.getSession().getAttribute(Key.USER_SESSION.name());
        CollectionModel<EntityModel<LayerGroupDto>> model = getLayerGroupList(LayerGroup.builder()
                .layerGroupId(id)
                .userGroupId(userSession.getUserGroupId())
                .build());

        model.add(linkTo(LayerGroupApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-layer-group-parent").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    private CollectionModel<EntityModel<LayerGroupDto>> getLayerGroupList(LayerGroup layerGroup) {
        List<EntityModel<LayerGroupDto>> groupList = layerGroupService.getListLayerGroup(layerGroup)
                .stream()
                .map(f -> EntityModel.of(modelMapper.map(f, LayerGroupDto.class))
                        .add(linkTo(LayerGroupApiController.class).slash(f.getLayerGroupId()).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(groupList);
    }
}

package gaia3d.api;

import gaia3d.domain.layer.Layer;
import gaia3d.domain.layer.LayerDto;
import gaia3d.service.LayerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/layers", produces = MediaTypes.HAL_JSON_VALUE)
public class LayerApiController {

    private final LayerService layerService;
    private final ModelMapper modelMapper;

    /**
     * 레이어 목록 조회
     *
     * @return
     */
    @GetMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity<CollectionModel<EntityModel<LayerDto>>> getLayers(@RequestParam(defaultValue = "0") Integer layerGroupId) {
        List<Layer> layerList = layerService.getListLayer(Layer.builder().layerGroupId(layerGroupId).build());
        List<EntityModel<LayerDto>> layerDtoList = layerList.stream()
                .map(f -> EntityModel.of(modelMapper.map(f, LayerDto.class))
                        .add(linkTo(LayerApiController.class).slash(f.getLayerId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<LayerDto>> model = CollectionModel.of(layerDtoList);

        model.add(linkTo(LayerApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-layer-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

    /**
     * 레이어 한건 조회
     *
     * @param id 레이어 아이디
     * @return
     */
    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<EntityModel<LayerDto>> getLayerById(@PathVariable("id") Integer id) {
        LayerDto dto = modelMapper.map(layerService.getLayer(id), LayerDto.class);
        EntityModel<LayerDto> layer = EntityModel.of(dto);
        layer.add(linkTo(LayerApiController.class).slash(id).withSelfRel());
        layer.add(Link.of("/docs/index.html#resources-layer-get").withRel("profile"));

        return ResponseEntity.ok(layer);
    }
}

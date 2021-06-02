package gaia3d.api;

import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceDto;
import gaia3d.service.MicroServiceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/micro-services", produces = MediaTypes.HAL_JSON_VALUE)
public class MicroServiceApiController {

    private final MicroServiceService microserviceService;
    private final ModelMapper modelMapper;

    /**
     * 데이터 라이브러리 목록 조회
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MicroServiceDto>>> getMicroServices(@RequestParam(defaultValue = "0") Integer microServiceId) {
        List<MicroService> microServiceList = microserviceService.getListMicroService(MicroService.builder().microServiceId(microServiceId).build());
        List<EntityModel<MicroServiceDto>> microServiceDtoList = microServiceList.stream()
                .map(f -> EntityModel.of(modelMapper.map(f, MicroServiceDto.class))
                        .add(linkTo(MicroServiceApiController.class).slash(f.getMicroServiceId()).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<MicroServiceDto>> model = CollectionModel.of(microServiceDtoList);

        model.add(linkTo(MicroServiceApiController.class).withSelfRel());
        model.add(Link.of("/docs/index.html#resources-data-library-list").withRel("profile"));

        return ResponseEntity.ok(model);
    }

}

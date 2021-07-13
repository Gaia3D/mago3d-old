package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.layer.Layer;
import gaia3d.persistence.LayerMapper;
import gaia3d.service.LayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 여기서는 Geoserver Rest API 결과를 가지고 파싱 하기 때문에 RestTemplate을 커스트마이징하면 안됨
 * @author Cheon JeongDae
 *
 */
@Service
public class LayerServiceImpl implements LayerService {

    private final LayerMapper layerMapper;
    private final PropertiesConfig propertiesConfig;
    
    public LayerServiceImpl(LayerMapper layerMapper, PropertiesConfig propertiesConfig) {
    	this.layerMapper = layerMapper;
        this.propertiesConfig = propertiesConfig;
    }

    /**
    * layer 목록
    * @return
    */
    @Transactional(readOnly=true)
    public List<Layer> getListLayer(Layer layer) {
        return layerMapper.getListLayer(layer);
    }

    /**
     * layer 정보 취득
     * @param layerId
     * @return
     */
    @Transactional(readOnly=true)
    public Layer getLayer(Integer layerId) {
        return layerMapper.getLayer(layerId);
    }

    /**
     * 레이어 최소 경계 영역을 wkt로 반환
     *
     * @param layerKey 레이어명
     * @return 최소 경계 영역 wkt
     */
    @Transactional(readOnly=true)
    public String getEnvelope(String layerKey) {
        String schema = propertiesConfig.getOgr2ogrSchema();
        return layerMapper.getEnvelope(schema, layerKey);
    }
}

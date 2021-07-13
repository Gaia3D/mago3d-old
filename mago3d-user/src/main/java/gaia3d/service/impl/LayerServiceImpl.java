package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.layer.BoundingBox;
import gaia3d.domain.layer.GeoserverLayer;
import gaia3d.domain.layer.Layer;
import gaia3d.domain.layer.LayerInsertType;
import gaia3d.persistence.LayerMapper;
import gaia3d.service.LayerService;
import lombok.extern.slf4j.Slf4j;
import org.geotools.ows.wms.CRSEnvelope;
import org.opengis.geometry.DirectPosition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 여기서는 Geoserver Rest API 결과를 가지고 파싱 하기 때문에 RestTemplate을 커스트마이징하면 안됨
 * @author Cheon JeongDae
 *
 */
@Slf4j
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
     * @param layer 레이어
     * @return 최소 경계 영역 wkt
     */
    @Transactional(readOnly=true)
    public BoundingBox getEnvelope(Layer layer) {

        LayerInsertType layerInsertType = LayerInsertType.valueOf(layer.getLayerInsertType().toUpperCase());
        log.info("@@@@@ insert type : {}", layerInsertType);

        String layerKey = layer.getLayerKey();
        log.info("@@@@@ layerKey : {}", layerKey);

        BoundingBox boundingBox;
        if (LayerInsertType.UPLOAD == layerInsertType) {
            String bboxWkt = "";
            String schema = propertiesConfig.getOgr2ogrSchema();
            bboxWkt = layerMapper.getEnvelope(schema, layerKey);
            if (bboxWkt == null) {
                bboxWkt = "POLYGON((126.70695082879526 37.3552906189018," +
                        "124.609708885853 38.6151323380178," +
                        "126.73972933011044 37.381863217601364," +
                        "131.872766214216 33.1137120723124," +
                        "124.609708885853 33.1137120723124))";
            }
            boundingBox = BoundingBox.from(bboxWkt);
        } else {
            GeoserverLayer geoserverLayer = new GeoserverLayer();
            CRSEnvelope env = geoserverLayer.getLayerBoundingBox("mago3d:" + layerKey);
            boundingBox = BoundingBox.from(env);
        }
        return boundingBox;

    }
}

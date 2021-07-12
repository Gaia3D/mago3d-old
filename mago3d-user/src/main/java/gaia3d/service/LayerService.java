package gaia3d.service;

import gaia3d.domain.layer.Layer;

import java.util.List;

public interface LayerService {

    /**
    * layer 목록
    * @return
    */
    List<Layer> getListLayer(Layer layer);
    
    /**
     * layer 정보 취득
     * @param layerId 레이어 아이디
     * @return
     */
    Layer getLayer(Integer layerId);

    /**
     * 레이어 최소 경계 영역을 wkt로 반환
     *
     * @param layerKey 레이어명
     * @return 최소 경계 영역 wkt
     */
    String getEnvelope(String layerKey);

}

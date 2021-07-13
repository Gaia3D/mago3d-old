package gaia3d.service;

import gaia3d.domain.layer.BoundingBox;
import gaia3d.domain.layer.Layer;
import gaia3d.domain.layer.LayerDto;
import org.opengis.geometry.DirectPosition;

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
     * @param layer 레이어
     * @return 최소 경계 영역 wkt
     */
    BoundingBox getEnvelope(Layer layer);

}

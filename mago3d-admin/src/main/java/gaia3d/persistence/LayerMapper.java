package gaia3d.persistence;

import gaia3d.domain.layer.Layer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayerMapper {
	
	/**
	 * Layer 총 건수
	 * @param layer
	 * @return
	 */
	Long getLayerTotalCount(Layer layer);

    /**
    * layer 목록
    * @param layer
    * @return
    */
    List<Layer> getListLayer(Layer layer);

    /**
    * layer 정보 취득
    * @param layerId
    * @return
    */
    Layer getLayer(Integer layerId);
    
    /**
     * layerKey 중복 체크 
     * @param layerKey
     * @return
     */
    Boolean isLayerKeyDuplication(String layerKey);

    /**
    * 자식 레이어 중 순서가 최대인 레이어를 검색
    * @param layerId
    * @return
    */
    Layer getMaxViewOrderChildLayer(Integer layerId);

    /**
    * 자식 레이어 개수
    * @param layerId
    * @return
    */
    int getChildLayerCount(Integer layerId);

    /**
    * 레이어 트리 부모와 순서로 그룹 정보 취득
    * @param layer
    * @return
    */
    Layer getLayerByParentAndViewOrder(Layer layer);

    /**
     * 레이어의 칼럼 목록을 조회
     * @param layerKey
     * @return
     */
    String getLayerColumn(String layerKey);

    /**
     * 테이블이 존재 하는지 확인
     * @param layerKey
     * @return
     */
    String isLayerExists(Layer layer);

//    /**
//    * 레이어 테이블의 컬럼 타입이 어떤 geometry 타입인지를 구함
//    * @param layerKey
//    * @return
//    */
//    String getGeometryType(String layerKey);

    /**
    * 레이어 등록
    * @param layer
    * @return
    */
    int insertLayer(Layer layer);

    /**
     * ogr2ogr로 실행된 테이블에 이력 관리를 위한 version_id, enable_yn 컬럼 추가
     * @param layer
     */
    void addColumnToLayer(Layer layer);

    /**
    * 레이어 트리 정보 수정
    * @param layer
    * @return
    */
    int updateTreeLayer(Layer layer);

    /**
    * 레이어 트리 순서 수정
    * @param layer
    * @return
    */
    int updateViewOrderLayer(Layer layer);

    /**
    * layer 정보 수정
    * @param layer
    * @return
    */
    int updateLayer(Layer layer);

    /**
     * 해당 레이어의 이전 데이터를 전부 비활성화 상태로 수정
     * @param layer
     * @return
     */
    int updateShapePreDataDisable(Layer layer);

    /**
     * org2org를 이용해서 생성한 테이블을 데이터 version 갱신
     * @param layer
     * @return
     */
    int updateOgr2OgrDataFileVersion(Layer layer);

    /**
     * shape 테이블 데이터 상태 변경
     * @param layer
     * @return
     */
    int updateOgr2OgrStatus(Layer layer);

    /**
    * 레이어 삭제
    * @param layerId
    * @return
    */
    int deleteLayer(Integer layerId);
    
    /**
     * ogr2ogr로 등록한 레이어 삭제
     * @param layer
     * @return
     */
    int dropLayerDetail(Layer layer);
}

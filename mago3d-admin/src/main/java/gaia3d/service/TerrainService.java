package gaia3d.service;

import gaia3d.domain.terrain.Terrain;

import java.util.List;

public interface TerrainService {

    /**
     * Terrain 목록
     * @return
     */
    List<Terrain> getListTerrain();

    /**
     * Terrain 정보
     * @return
     */
    Terrain getTerrain(Integer terrainId);

    /**
     * Terrain 저장
     * @param terrain
     * @return
     */
    int insertTerrain(Terrain terrain);

    /**
     * Terrain 수정
     * @param terrain
     * @return
     */
    int updateTerrain(Terrain terrain);

    /**
     * Terrain 삭제
     * @param terrainId
     * @return
     */
    int deleteTerrain(Integer terrainId);
}

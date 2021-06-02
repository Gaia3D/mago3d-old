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
}

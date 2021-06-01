package gaia3d.service.impl;

import gaia3d.domain.terrain.Terrain;
import gaia3d.persistence.TerrainMapper;
import gaia3d.service.TerrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Terrain
 */
@Service
public class TerrainServiceImpl implements TerrainService {
    @Autowired
    private TerrainMapper terrainMapper;

    /**
     * Terrain 목록
     * @return
     */
    @Transactional(readOnly = true)
    public List<Terrain> getListTerrain() {
        return terrainMapper.getListTerrain();
    }

    /**
     * Terrain 정보
     * @return
     */
    @Transactional(readOnly = true)
    public Terrain getTerrain(Integer terrainId) {
        return terrainMapper.getTerrain(terrainId);
    }

    /**
     * Terrain 저장
     * @param terrain
     * @return
     */
    @Transactional
    public int insertTerrain(Terrain terrain) {
        if(terrain.getBasic()) {
            // 현재 입력하는 것이 활성화 되면 나머지 Terrain 은 전부 비 활성화
            terrainMapper.updateAllTerrainDisable();
        }
        return terrainMapper.insertTerrain(terrain);
    }

    /**
     * Terrain 수정
     * @param terrain
     * @return
     */
    @Transactional
    public int updateTerrain(Terrain terrain) {
        if(terrain.getBasic()) {
            // 현재 입력하는 것이 활성화 되면 나머지 Terrain 은 전부 비 활성화
            terrainMapper.updateAllTerrainDisable();
        }
        return terrainMapper.updateTerrain(terrain);
    }

    /**
     * Terrain 삭제
     * @param terrainId
     * @return
     */
    @Transactional
    public int deleteTerrain(Integer terrainId) {
        return terrainMapper.deleteTerrain(terrainId);
    }
}

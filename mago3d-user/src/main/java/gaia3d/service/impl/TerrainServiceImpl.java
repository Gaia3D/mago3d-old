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
}

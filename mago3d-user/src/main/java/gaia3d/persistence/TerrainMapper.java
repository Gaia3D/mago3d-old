package gaia3d.persistence;

import gaia3d.domain.terrain.Terrain;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Terrain
 * @author jeongdae
 *
 */
@Repository
public interface TerrainMapper {

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

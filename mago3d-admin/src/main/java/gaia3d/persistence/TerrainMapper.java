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
	 * 모든 Terrain 비 활성화. basic = false
	 */
    int updateAllTerrainDisable();

	/**
	 * Terrain 삭제
	 * @param terrainId
	 * @return
	 */
	int deleteTerrain(Integer terrainId);
}

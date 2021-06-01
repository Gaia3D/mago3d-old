package gaia3d.controller.rest;

import gaia3d.config.CacheConfig;
import gaia3d.domain.terrain.Terrain;
import gaia3d.service.TerrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/terrains")
public class TerrainRestController {

	@Autowired
	private CacheConfig cacheConfig;

	@Autowired
	private TerrainService terrainService;

	/**
	 * Terrain 등록
	 * @param terrain
	 * @param bindingResult
	 * @return
	 */
	@PostMapping
	public Map<String, Object> insert(@Valid Terrain terrain, BindingResult bindingResult) {
		log.info("@@ terrain = {}", terrain);
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		if(bindingResult.hasErrors()) {
			message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			log.info("@@@@@ message = {}", message);
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", errorCode);
			result.put("message", message);
			return result;
		}

		terrainService.insertTerrain(terrain);
		int statusCode = HttpStatus.OK.value();

		reloadCache();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	@PutMapping(value = "/{terrainId:[0-9]+}")
	public Map<String, Object> update(HttpServletRequest request, @Valid Terrain terrain, BindingResult bindingResult) {
		log.info("@@ terrain = {}", terrain);
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		if(bindingResult.hasErrors()) {
			message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			log.info("@@@@@ message = {}", message);
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", errorCode);
			result.put("message", message);
			return result;
		}

		terrainService.updateTerrain(terrain);
		int statusCode = HttpStatus.OK.value();

		reloadCache();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	private void reloadCache() {
//		CacheParams cacheParams = new CacheParams();
//		cacheParams.setCacheName(CacheName.ROLE);
//		cacheParams.setCacheType(CacheType.BROADCAST);
//		cacheConfig.loadCache(cacheParams);
	}
}

package gaia3d.controller.rest;

import gaia3d.config.CacheConfig;
import gaia3d.domain.cache.CacheName;
import gaia3d.domain.cache.CacheParams;
import gaia3d.domain.cache.CacheType;
import gaia3d.domain.quartz.ScheduleInfo;
import gaia3d.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 스케줄 관리
 */
@Slf4j
@RestController
@RequestMapping("/schedules")
public class ScheduleRestController {

	@Autowired
	private CacheConfig cacheConfig;
	@Autowired
	private QuartzService quartzService;

	/**
	 * Schedule 등록
	 * @param scheduleInfo
	 * @param bindingResult
	 * @return
	 */
	@PostMapping
	public Map<String, Object> insert(@Valid ScheduleInfo scheduleInfo, BindingResult bindingResult) {
		log.info("@@ scheduleInfo = {}", scheduleInfo);
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

		quartzService.insertScheduleInfo(scheduleInfo);
		int statusCode = HttpStatus.OK.value();

		reloadCache();
		
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * MicroService 정보 수정
	 * @param microServiceId
	 * @param bindingResult
	 * @return
	 */
	/*@PostMapping("/{microServiceId:[0-9]+}")
	public Map<String, Object> update(HttpServletRequest request, @PathVariable Integer microServiceId, @Valid @ModelAttribute MicroService microService, BindingResult bindingResult) {
		log.info("@@ microService = {}", microService);
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

		microServiceService.updateMicroService(microService);
		int statusCode = HttpStatus.OK.value();

		reloadCache();
		
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}*/

	/**
	 * MicroService 삭제
	 * @param microServiceId
	 * @return
	 */
	/*@DeleteMapping("/{microServiceId:[0-9]+}")
	public Map<String, Object> delete(@PathVariable Integer microServiceId) {
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		microServiceService.deleteMicroService(microServiceId);
		int statusCode = HttpStatus.OK.value();

		reloadCache();
		
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}*/

	private void reloadCache() {
		CacheParams cacheParams = new CacheParams();
		cacheParams.setCacheName(CacheName.MICRO_SERVICE);
		cacheParams.setCacheType(CacheType.BROADCAST);
		cacheConfig.loadCache(cacheParams);
	}
}
package gaia3d.controller.rest;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.Key;
import gaia3d.domain.datalibrary.DataLibraryConverterJob;
import gaia3d.domain.user.UserSession;
import gaia3d.service.DataLibraryConverterService;
import gaia3d.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 라이브러리 파일 업로더
 * TODO 설계 파일 안의 texture 의 경우 설계 파일에서 참조하는 경우가 있으므로 이름 변경 불가.
 * @author jeongdae
 *
 */
@Slf4j
@RestController
@RequestMapping("/data-library-converters")
public class DataLibraryConverterRestController {
	
	// 파일 copy 시 버퍼 사이즈
	public static final int BUFFER_SIZE = 8192;
	
	@Autowired
	private PolicyService policyService;
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	@Autowired
	private DataLibraryConverterService dataLibraryConverterService;

	/**
	 * 변환
	 * @param request
	 * @param dataLibraryConverterJob
	 * @return
	 */
	@PostMapping
	public Map<String, Object> insert(HttpServletRequest request, DataLibraryConverterJob dataLibraryConverterJob) {
		log.info("@@@ dataLibraryConverterJob = {}", dataLibraryConverterJob);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		if(dataLibraryConverterJob.getConverterCheckIds().length() <= 0) {
			log.info("@@@@@ message = {}", message);
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "check.value.required");
			result.put("message", message);
			return result;
		}
		if(StringUtils.isEmpty(dataLibraryConverterJob.getTitle())) {
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "converter.title.empty");
			result.put("message", message);
			return result;
		}
		if(dataLibraryConverterJob.getUsf() == null) {
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "converter.usf.empty");
			result.put("message", message);
			return result;
		}

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		dataLibraryConverterJob.setUserId(userSession.getUserId());

		dataLibraryConverterService.insertDataLibraryConverter(dataLibraryConverterJob);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}
}

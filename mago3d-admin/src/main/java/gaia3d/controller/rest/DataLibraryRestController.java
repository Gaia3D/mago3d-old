package gaia3d.controller.rest;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.datalibrary.DataLibrary;
import gaia3d.service.DataLibraryService;
import gaia3d.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
@RequestMapping("/data-libraries")
public class DataLibraryRestController {
	
	// 파일 copy 시 버퍼 사이즈
	public static final int BUFFER_SIZE = 8192;
	
	@Autowired
	private PolicyService policyService;
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	@Autowired
	private DataLibraryService dataLibraryService;

	/**
	 * 데이터 라이브러리 수정
	 * @param request
	 * @param dataLibrary
	 * @param bindingResult
	 * @return
	 */
	@PutMapping("/{dataLibraryId:[0-9]+}")
	public Map<String, Object> update(HttpServletRequest request, @Valid DataLibrary dataLibrary, BindingResult bindingResult) {
		log.info("@@ dataLibrary = {}", dataLibrary);
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

		dataLibraryService.updateDataLibrary(dataLibrary);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}
}

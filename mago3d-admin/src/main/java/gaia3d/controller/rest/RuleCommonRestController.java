package gaia3d.controller.rest;

import gaia3d.service.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 룰 공통 관리
 */
@Slf4j
@RestController
@RequestMapping(value = "/rules")
public class RuleCommonRestController {

	@Autowired
	private RuleService ruleService;

	/**
	 * 룰 Key 중복 체크
	 * @param request
	 * @param ruleKey
	 * @return
	 */
	@GetMapping(value = "/duplication")
	public Map<String, Object> keyDuplicationCheck(HttpServletRequest request, String ruleKey) {
		log.info("@@ ruleKey = {}", ruleKey);
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		// TODO @Valid 로 구현해야 함
		if(StringUtils.isEmpty(ruleKey)) {
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "rule.key.empty");
			result.put("message", message);
			return result;
		}

		Boolean duplication = ruleService.isRuleKeyDuplication(ruleKey);
		log.info("@@ duplication = {}", duplication);
		int statusCode = HttpStatus.OK.value();

		result.put("duplication", duplication);
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);

		return result;
	}
}

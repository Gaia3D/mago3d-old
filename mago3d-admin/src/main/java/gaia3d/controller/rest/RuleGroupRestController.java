package gaia3d.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.domain.Key;
import gaia3d.domain.rule.RuleGroup;
import gaia3d.domain.rule.RuleType;
import gaia3d.domain.user.UserSession;
import gaia3d.service.RuleGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 룰 그룹 관리
 */
@Slf4j
@RestController
@RequestMapping(value = "/rule-groups")
public class RuleGroupRestController {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private RuleGroupService ruleGroupService;

	/**
	 * 룰 그룹 Key 중복 체크
	 * @param request
	 * @param ruleGroup
	 * @return
	 */
	@GetMapping(value = "/duplication")
	public Map<String, Object> keyDuplicationCheck(HttpServletRequest request, RuleGroup ruleGroup) {
		log.info("@@ ruleGroup = {}", ruleGroup);
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		// TODO @Valid 로 구현해야 함
		if(StringUtils.isEmpty(ruleGroup)) {
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "rule.group.key.empty");
			result.put("message", message);
			return result;
		}

		Boolean duplication = ruleGroupService.isRuleGroupKeyDuplication(ruleGroup);
		log.info("@@ duplication = {}", duplication);
		int statusCode = HttpStatus.OK.value();

		result.put("duplication", duplication);
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);

		return result;
	}

	/**
	 * 룰 그룹 등록
	 * @param request
	 * @param ruleGroup
	 * @return
	 */
	@PostMapping
	public Map<String, Object> insert(HttpServletRequest request, RuleGroup ruleGroup) {
		log.info("@@@@@ insert ruleGroup = {}", ruleGroup);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		if(RuleType.DATA_LIBRARY == RuleType.valueOf(ruleGroup.getRuleType().toUpperCase())) {
			log.info("@@@@@@@@@@@ 1 ruleGroup = {}", ruleGroup);
			// 상속을 하지 않음
			if( !ruleGroup.getRuleInheritType() ) {
				log.info("@@@@@@@@@@@ 2 ruleGroup");
				RuleGroup parentRuleGroup = ruleGroupService.getRuleGroup(RuleGroup.builder().ruleGroupId(ruleGroup.getParent()).build());
				if (parentRuleGroup.getDepth() > 1) {
					log.info("@@@@@@@@@@@ 3 ");
					boolean invalid = ruleInheritTypeValidate(parentRuleGroup);
					log.info("@@@@@@@@@@@ 4 invalid = {}", invalid);
					if(invalid) {
						log.info("@@ 데이터 라이브러리 최상위 부모가 Rule을 상속 한다고 선언 했으므로 자손들은 Override 불가능.");
						result.put("statusCode", HttpStatus.BAD_REQUEST.value());
						result.put("errorCode", "rule.group.inherit.type.invalid");
						result.put("message", message);
						return result;
					}
				}
			}
		}

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());

		ruleGroup.setUserId(userSession.getUserId());
		ruleGroupService.insertRuleGroup(ruleGroup);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * 데이터 라이브러리 그룹 2 dpeth 이상만, 최상위 룰 상속 여부를 설정 가능
	 * @param ruleGroup
	 * @return
	 */
	private boolean ruleInheritTypeValidate(RuleGroup ruleGroup) {
		log.info("@@@@@@@@@@@ 4-1 ruleGroup = {}", ruleGroup);
		// 무조건 최상위 룰 상속 false 상태인 것만 들어옴
		if(ruleGroup.getDepth() == 2) {
			if(ruleGroup.getRuleInheritType()) {
				// 최상위 부모가 상속을 선언 했는데, 자식이 오버라이딩 불가능
				return true;
			} else {
				return false;
			}
		} else {
			RuleGroup parentRuleGroup = ruleGroupService.getRuleGroup(RuleGroup.builder().ruleGroupId(ruleGroup.getParent()).build());
			return ruleInheritTypeValidate(parentRuleGroup);
		}
	}


	/**
	 * 그룹 수정
	 * @param request
	 * @param ruleGroup
	 * @return
	 */
	@PutMapping(value = "/{ruleGroupId}")
	public Map<String, Object> update(HttpServletRequest request, @PathVariable Integer ruleGroupId, RuleGroup ruleGroup) {
		log.info("@@@@@ ruleGroup = {}", ruleGroup);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		ruleGroup.setRuleGroupId(ruleGroupId);
		ruleGroupService.updateRuleGroup(ruleGroup);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * 룰 그룹 트리 순서 수정 (up/down)
	 * @param request
	 * @param ruleGroupId
	 * @param ruleGroup
	 * @return
	 */
	@PostMapping(value = "/view-order/{ruleGroupId:[0-9]+}")
	public Map<String, Object> moveLayerGroup(HttpServletRequest request, @PathVariable Integer ruleGroupId, @ModelAttribute RuleGroup ruleGroup) {
		log.info("@@ ruleGroup = {}", ruleGroup);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		int updateCount = ruleGroupService.updateRuleGroupViewOrder(ruleGroup);
		int statusCode = HttpStatus.OK.value();
		if(updateCount == 0) {
			statusCode = HttpStatus.BAD_REQUEST.value();
			errorCode = "rule.group.view-order.invalid";
		}

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}
}

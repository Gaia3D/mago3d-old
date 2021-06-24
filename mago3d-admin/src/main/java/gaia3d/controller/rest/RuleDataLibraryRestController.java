package gaia3d.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.rule.datalibrary.tree.Tree;
import gaia3d.domain.Key;
import gaia3d.domain.rule.RuleGroup;
import gaia3d.domain.user.UserSession;
import gaia3d.service.RuleGroupService;
import gaia3d.service.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 라이브러리 룰 관리
 */
@Slf4j
@RestController
@RequestMapping(value = "/rule/data-libraries")
public class RuleDataLibraryRestController extends RuleBaseRestController {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private RuleGroupService ruleGroupService;

	/**
	 * 나무 등록
	 * @param request
	 * @param tree
	 * @return
	 */
	@PostMapping(value = "/trees")
	public Map<String, Object> treesInsert(HttpServletRequest request, Tree tree) {
		log.info("@@@@@ insert tree = {}", tree);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		String userId = ((UserSession)request.getSession().getAttribute(Key.USER_SESSION.name())).getUserId();

		RuleGroup ruleGroup = getRuleGroup(ruleGroupService, tree.getRuleGroupId());
		tree.setRuleGroupId(ruleGroup.getRuleGroupId());
		tree.setRuleGroupKey(ruleGroup.getRuleGroupKey());
		tree.setRuleGroupName(ruleGroup.getRuleGroupName());

		String attributes = getAttributes(objectMapper, tree);
		insert(ruleService, userId, tree, attributes);

		result.put("statusCode", HttpStatus.OK.value());
		result.put("errorCode", null);
		result.put("message", null);

		return result;
	}

	/**
	 * 나무 수정
	 * @param request
	 * @param tree
	 * @return
	 */
	@PutMapping(value = "/trees/{ruleId}")
	public Map<String, Object> treesUpdate(HttpServletRequest request, @PathVariable Integer ruleId, Tree tree) {
		log.info("@@@@@ update tree = {}", tree);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		String userId = ((UserSession)request.getSession().getAttribute(Key.USER_SESSION.name())).getUserId();
		RuleGroup ruleGroup = getRuleGroup(ruleGroupService, tree.getRuleGroupId());
		tree.setRuleGroupId(ruleGroup.getRuleGroupId());
		tree.setRuleGroupKey(ruleGroup.getRuleGroupKey());
		tree.setRuleGroupName(ruleGroup.getRuleGroupName());

		String attributes = getAttributes(objectMapper, tree);
		update(ruleService, ruleId, tree.getRuleName(), tree.getAvailable(), attributes, tree.getDescription());

		result.put("statusCode", HttpStatus.OK.value());
		result.put("errorCode", null);
		result.put("message", null);

		return result;
	}
}

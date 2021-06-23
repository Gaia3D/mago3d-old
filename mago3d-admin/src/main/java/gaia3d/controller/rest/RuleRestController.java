//package gaia3d.controller.rest;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import gaia3d.datalibrary.BaseRule;
//import gaia3d.datalibrary.tree.Tree;
//import gaia3d.domain.Key;
//import gaia3d.domain.rule.Rule;
//import gaia3d.domain.rule.RuleGroup;
//import gaia3d.domain.user.UserSession;
//import gaia3d.service.RuleGroupService;
//import gaia3d.service.RuleService;
//import gaia3d.support.LogMessageSupport;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 룰 관리
// * TODO 여긴 네가지 rule 로 Controller를 분리 해야 할거 같음
// */
//@Slf4j
//@RestController
//@RequestMapping(value = "/rules")
//public class RuleRestController {
//
//	@Autowired
//	private ObjectMapper objectMapper;
//	@Autowired
//	private RuleService ruleService;
//	@Autowired
//	private RuleGroupService ruleGroupService;
//
//	/**
//	 * 나무 등록
//	 * @param request
//	 * @param tree
//	 * @return
//	 */
//	@PostMapping(value = "/trees")
//	public Map<String, Object> treesInsert(HttpServletRequest request, Tree tree) {
//		log.info("@@@@@ insert tree = {}", tree);
//
//		Map<String, Object> result = new HashMap<>();
//		String errorCode = null;
//		String message = null;
//
//		String userId = ((UserSession)request.getSession().getAttribute(Key.USER_SESSION.name())).getUserId();
//
//		RuleGroup ruleGroup = getRuleGroup(tree.getRuleGroupId());
//		tree.setRuleGroupId(ruleGroup.getRuleGroupId());
//		tree.setRuleGroupKey(ruleGroup.getRuleGroupKey());
//		tree.setRuleGroupName(ruleGroup.getRuleGroupName());
//
//		String attributes = getAttributes(tree);
//		insert(result, userId, tree, attributes);
//
//		return result;
//	}
//
//	private RuleGroup getRuleGroup(Integer ruleGroupId) {
//		RuleGroup ruleGroup = new RuleGroup();
//		ruleGroup.setRuleGroupId(ruleGroupId);
//		ruleGroup = ruleGroupService.getRuleGroup(ruleGroup);
//
//		return ruleGroup;
//	}
//
//	private String getAttributes(BaseRule baseRule) {
//		// string json 변환 처리
//		String attributes = null;
//		try {
//			attributes = objectMapper.writeValueAsString(baseRule);
//		} catch (JsonProcessingException e) {
//			log.info("@@@@@ streetlamp writeValueAsString error = {}", e.getMessage());
//			LogMessageSupport.printMessage(e);
//		}
//		return attributes;
//	}
//
//	// 등록 공통 처리
//	private void insert(Map<String, Object> result, String userId, BaseRule baseRule, String attributes) {
//		Rule rule = new Rule();
//		rule.setRuleGroupId(baseRule.getRuleGroupId());
//		rule.setRuleName(baseRule.getRuleName());
//		rule.setRuleKey(baseRule.getRuleKey());
//		rule.setRuleType(baseRule.getRuleType());
//		rule.setUserId(userId);
//		rule.setAvailable(baseRule.getAvailable());
//		rule.setAttributes(attributes);
//		rule.setDescription(baseRule.getDescription());
//
//		ruleService.insertRule(rule);
//
//		int statusCode = HttpStatus.OK.value();
//
//		result.put("statusCode", statusCode);
//		result.put("errorCode", null);
//		result.put("message", null);
//	}
//
//	/**
//	 * 나무 수정
//	 * @param request
//	 * @param tree
//	 * @return
//	 */
//	@PutMapping(value = "/trees/{ruleId}")
//	public Map<String, Object> treesUpdate(HttpServletRequest request, @PathVariable Integer ruleId, Tree tree) {
//		log.info("@@@@@ update tree = {}", tree);
//
//		Map<String, Object> result = new HashMap<>();
//		String errorCode = null;
//		String message = null;
//
//		String userId = ((UserSession)request.getSession().getAttribute(Key.USER_SESSION.name())).getUserId();
//		RuleGroup ruleGroup = getRuleGroup(tree.getRuleGroupId());
//		tree.setRuleGroupId(ruleGroup.getRuleGroupId());
//		tree.setRuleGroupKey(ruleGroup.getRuleGroupKey());
//		tree.setRuleGroupName(ruleGroup.getRuleGroupName());
//
//		String attributes = getAttributes(tree);
//		update(result, ruleId, tree.getRuleName(), tree.getAvailable(), attributes, tree.getDescription());
//
//		return result;
//	}
//
//	private void update(Map<String, Object> result, Integer ruleId, String ruleName, Boolean available, String attributes, String description) {
//		Rule rule = new Rule();
//		rule.setRuleId(ruleId);
//		rule.setRuleName(ruleName);
//		rule.setAvailable(available);
//		rule.setAttributes(attributes);
//		rule.setDescription(description);
//
//		ruleService.updateRule(rule);
//
//		int statusCode = HttpStatus.OK.value();
//
//		result.put("statusCode", statusCode);
//		result.put("errorCode", null);
//		result.put("message", null);
//	}
//}

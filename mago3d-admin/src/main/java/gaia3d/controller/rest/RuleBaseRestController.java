package gaia3d.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.domain.rule.Rule;
import gaia3d.domain.rule.RuleGroup;
import gaia3d.rule.BaseRule;
import gaia3d.service.RuleGroupService;
import gaia3d.service.RuleService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;

/**
 * Rule 공통 처리를 위한 클래스, interface를 할까 하다가 모호해서 우선 class 로 함
 */
@Slf4j
public class RuleBaseRestController {

    public RuleGroup getRuleGroup(RuleGroupService ruleGroupService, Integer ruleGroupId) {
        RuleGroup ruleGroup = new RuleGroup();
        ruleGroup.setRuleGroupId(ruleGroupId);
        return ruleGroupService.getRuleGroup(ruleGroup);
    }

    public String getAttributes(ObjectMapper objectMapper, BaseRule baseRule) {
        // string json 변환 처리
        String attributes = null;
        try {
            attributes = objectMapper.writeValueAsString(baseRule);
        } catch (JsonProcessingException e) {
            log.info("@@@@@ streetlamp writeValueAsString error = {}", e.getMessage());
            LogMessageSupport.printMessage(e);
        }
        return attributes;
    }

    // 등록 공통 처리
    public int insert(RuleService ruleService, String userId, BaseRule baseRule, String attributes) {
        Rule rule = new Rule();
        rule.setRuleGroupId(baseRule.getRuleGroupId());
        rule.setRuleName(baseRule.getRuleName());
        rule.setRuleKey(baseRule.getRuleKey());
        rule.setRuleType(baseRule.getRuleType());
        rule.setUserId(userId);
        rule.setAvailable(baseRule.getAvailable());
        rule.setAttributes(attributes);
        rule.setDescription(baseRule.getDescription());

        return ruleService.insertRule(rule);
    }

    public int update(RuleService ruleService, Integer ruleId, String ruleName, Boolean available, String attributes, String description) {
        Rule rule = new Rule();
        rule.setRuleId(ruleId);
        rule.setRuleName(ruleName);
        rule.setAvailable(available);
        rule.setAttributes(attributes);
        rule.setDescription(description);

        return ruleService.updateRule(rule);
    }
}

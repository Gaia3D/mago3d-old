package gaia3d.service;

import gaia3d.domain.rule.Rule;

import java.util.List;

public interface RuleService {

    Boolean isRuleKeyDuplication(String ruleKey);

    Long getRuleTotalCount(Rule rule);

    List<Rule> getListRule(Rule rule);

    /**
     * 룰 목록
     * @param ruleGroupId
     * @return
     */
    List<Rule> getListRuleByRuleGroupId(Integer ruleGroupId);


    Rule getRule(Integer ruleId);

    int insertRule(Rule rule);

    int updateRule(Rule rule);

    int deleteRule(Integer ruleId);
}

package gaia3d.service;

import gaia3d.domain.rule.Rule;

import java.util.List;

public interface RuleService {

    Long getRuleTotalCount(Rule rule);

    List<Rule> getListRule(Rule rule);

    Rule getRule(Integer ruleId);
}

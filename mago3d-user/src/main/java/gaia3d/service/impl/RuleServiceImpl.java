package gaia3d.service.impl;

import gaia3d.domain.rule.Rule;
import gaia3d.persistence.RuleMapper;
import gaia3d.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleMapper ruleMapper;

    @Override
    public Long getRuleTotalCount(Rule rule) {
        return ruleMapper.getRuleTotalCount(rule);
    }

    @Override
    public List<Rule> getListRule(Rule rule) {
        return ruleMapper.getListRule(rule);
    }

    @Override
    public Rule getRule(Integer ruleId) {
        return ruleMapper.getRule(ruleId);
    }
}

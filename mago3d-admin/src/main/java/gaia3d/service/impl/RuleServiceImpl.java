package gaia3d.service.impl;

import gaia3d.domain.rule.Rule;
import gaia3d.domain.rule.RuleType;
import gaia3d.persistence.RuleMapper;
import gaia3d.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gaia3d.domain.rule.RuleType.*;

@Service
public class RuleServiceImpl implements RuleService {
    @Autowired
    private RuleMapper ruleMapper;

    @Transactional(readOnly = true)
    public Boolean isRuleKeyDuplication(String ruleKey) {
        return ruleMapper.isRuleKeyDuplication(ruleKey);
    }

    @Transactional(readOnly = true)
    public Long getRuleTotalCount(Rule rule) {
        return ruleMapper.getRuleTotalCount(rule);
    }

    @Transactional(readOnly = true)
    public List<Rule> getListRule(Rule rule) {
        return ruleMapper.getListRule(rule);
    }

    /**
     * 룰 목록
     * @param ruleGroupId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Rule> getListRuleByRuleGroupId(Integer ruleGroupId) {
        return ruleMapper.getListRuleByRuleGroupId(ruleGroupId);
    }

    @Transactional(readOnly = true)
    public Rule getRule(Integer ruleId) {
        return ruleMapper.getRule(ruleId);
    }

    @Transactional
    public int insertRule(Rule rule) {
        RuleType ruleType = valueOf(rule.getRuleType().toUpperCase());

        if(DATA == ruleType) {
            return ruleMapper.insertRule(rule);
        } else if(DATA_LIBRARY == ruleType) {
            return ruleMapper.insertRule(rule);
        } else if(LAYER == ruleType) {
            return ruleMapper.insertRule(rule);
        } else {
            return ruleMapper.insertRule(rule);
        }
    }

    @Transactional
    public int updateRule(Rule rule) {
        return ruleMapper.updateRule(rule);
    }

    @Transactional
    public int deleteRule(Integer ruleId) {
        return ruleMapper.deleteRule(ruleId);
    }
}

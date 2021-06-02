package gaia3d.persistence;

import gaia3d.domain.rule.Rule;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleMapper {

    Long getRuleTotalCount(Rule rule);

    List<Rule> getListRule(Rule rule);

    Rule getRule(Integer ruleId);
}

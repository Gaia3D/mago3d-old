package gaia3d.persistence;

import gaia3d.domain.rule.Rule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 룰
 */
@Repository
public interface RuleMapper {

	Long getRuleTotalCount(Rule rule);

	/**
	 * 룰 목록
	 * @param rule
	 * @return
	 */
	List<Rule> getListRule(Rule rule);

	/**
	 * 룰 목록
	 */
	List<Rule> getListAllRuleByRuleType(Rule rule);

	/**
	 * 룰 목록
	 * @param ruleGroupId
	 * @return
	 */
	List<Rule> getListRuleByRuleGroupId(Integer ruleGroupId);

	/**
	 * 룰 정보 조회
	 * @param ruleId
	 * @return
	 */
	Rule getRule(Integer ruleId);

	/**
	 * 룰 Key 중복 확인
	 * @param ruleKey
	 * @return
	 */
	Boolean isRuleKeyDuplication(String ruleKey);

	/**
	 * 룰 등록
	 * @param rule
	 * @return
	 */
	int insertRule(Rule rule);

	/**
	 * 룰 수정
	 * @param rule
	 * @return
	 */
	int updateRule(Rule rule);

	/**
	 * 룰 삭제
	 * @param ruleId
	 * @return
	 */
	int deleteRule(Integer ruleId);
}

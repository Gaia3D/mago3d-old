package gaia3d.persistence;

import gaia3d.domain.rule.RuleGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 룰 그룹
 */
@Repository
public interface RuleGroupMapper {

	/**
	 * 룰 그룹 목록
	 * @param ruleGroup
	 * @return
	 */
	List<RuleGroup> getListRuleGroup(RuleGroup ruleGroup);

	/**
	 * 룰 그룹 정보 조회
	 * @param ruleGroup
	 * @return
	 */
	RuleGroup getRuleGroup(RuleGroup ruleGroup);

	/**
	 * 룰 그룹 Key 중복 확인
	 * @param ruleGroup
	 * @return
	 */
	Boolean isRuleGroupKeyDuplication(RuleGroup ruleGroup);

	/**
	 * 부모와 순서를 가지고 룰 그룹 정보를 취득
	 * @param ruleGroup
	 * @return
	 */
	RuleGroup getRuleGroupByParentAndViewOrder(RuleGroup ruleGroup);

	/**
	 * 최상위 룰 그룹 정보
	 * @param ruleGroupId
	 * @return
	 */
	RuleGroup getAncestorRuleGroupByRuleGroupId(Integer ruleGroupId);
	
	/**
	 * 룰 그룹 등록
	 * @param ruleGroup
	 * @return
	 */
	int insertRuleGroup(RuleGroup ruleGroup);

	/**
	 * 룰 그룹 수정
	 * @param ruleGroup
	 * @return
	 */
	int updateRuleGroup(RuleGroup ruleGroup);

	/**
	 * 룰 그룹 표시 순서 수정. UP, DOWN
	 * @param ruleGroup
	 * @return
	 */
	int updateRuleGroupViewOrder(RuleGroup ruleGroup);

	/**
	 * 룰 그룹 삭제
	 * @param ruleGroup
	 * @return
	 */
	int deleteRuleGroup(RuleGroup ruleGroup);
}

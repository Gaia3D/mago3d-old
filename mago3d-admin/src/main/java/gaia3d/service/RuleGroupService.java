package gaia3d.service;

import gaia3d.domain.rule.RuleGroup;

import java.util.List;

public interface RuleGroupService {

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
     * 룰 그룹 표시 순서 수정 (up/down)
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

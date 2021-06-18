package gaia3d.service.impl;

import gaia3d.domain.Move;
import gaia3d.domain.rule.Rule;
import gaia3d.domain.rule.RuleGroup;
import gaia3d.persistence.RuleGroupMapper;
import gaia3d.service.RuleGroupService;
import gaia3d.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RuleGroupServiceImpl implements RuleGroupService {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleGroupMapper ruleGroupMapper;

    /**
     * 룰 그룹 목록
     * @param ruleGroup
     * @return
     */
    @Transactional(readOnly = true)
    public List<RuleGroup> getListRuleGroup(RuleGroup ruleGroup) {
        return ruleGroupMapper.getListRuleGroup(ruleGroup);
    }

    /**
     * 룰 그룹 정보 조회
     * @param ruleGroup
     * @return
     */
    @Transactional(readOnly = true)
    public RuleGroup getRuleGroup(RuleGroup ruleGroup) {
        return ruleGroupMapper.getRuleGroup(ruleGroup);
    }

    /**
     * 룰 그룹 Key 중복 확인
     * @param ruleGroup
     * @return
     */
    @Transactional(readOnly = true)
    public Boolean isRuleGroupKeyDuplication(RuleGroup ruleGroup) {
        return ruleGroupMapper.isRuleGroupKeyDuplication(ruleGroup);
    }

    /**
     * 부모와 표시 순서로 룰 그룹 조회
     * @param ruleGroup
     * @return
     */
    @Transactional(readOnly = true)
    public RuleGroup getRuleGroupByParentAndViewOrder(RuleGroup ruleGroup) {
        return ruleGroupMapper.getRuleGroupByParentAndViewOrder(ruleGroup);
    }

    /**
     * 최상위 룰 그룹 정보
     * @param ruleGroupId
     * @return
     */
    @Transactional(readOnly = true)
    public RuleGroup getAncestorRuleGroupByRuleGroupId(Integer ruleGroupId) {
        return ruleGroupMapper.getAncestorRuleGroupByRuleGroupId(ruleGroupId);
    }

    /**
     * 룰 그룹 등록
     * @param ruleGroup
     * @return
     */
    @Transactional
    public int insertRuleGroup(RuleGroup ruleGroup) {
        return ruleGroupMapper.insertRuleGroup(ruleGroup);
    }

    /**
     * 룰 그룹 수정
     * @param ruleGroup
     * @return
     */
    @Transactional
    public int updateRuleGroup(RuleGroup ruleGroup) {
        return ruleGroupMapper.updateRuleGroup(ruleGroup);
    }

    /**
     * 룰 그룹 표시 순서 수정. UP, DOWN
     * @param ruleGroup
     * @return
     */
    @Transactional
    public int updateRuleGroupViewOrder(RuleGroup ruleGroup) {

        RuleGroup dbRuleGroup = ruleGroupMapper.getRuleGroup(ruleGroup);
        dbRuleGroup.setUpdateType(ruleGroup.getUpdateType());

        Integer modifyViewOrder = dbRuleGroup.getViewOrder();
        RuleGroup searchRuleGroup = new RuleGroup();
        //searchRuleGroup.setUserId(dataLibraryGroup.getUserId());
        searchRuleGroup.setUpdateType(dbRuleGroup.getUpdateType());
        searchRuleGroup.setParent(dbRuleGroup.getParent());

        if(Move.UP == Move.valueOf(dbRuleGroup.getUpdateType())) {
            // 바로 위 메뉴의 view_order 를 +1
            searchRuleGroup.setViewOrder(dbRuleGroup.getViewOrder());
            searchRuleGroup = getRuleGroupByParentAndViewOrder(searchRuleGroup);

            if(searchRuleGroup == null) return 0;

            dbRuleGroup.setViewOrder(searchRuleGroup.getViewOrder());
            searchRuleGroup.setViewOrder(modifyViewOrder);
        } else {
            // 바로 아래 메뉴의 view_order 를 -1 함
            searchRuleGroup.setViewOrder(dbRuleGroup.getViewOrder());
            searchRuleGroup = getRuleGroupByParentAndViewOrder(searchRuleGroup);

            if(searchRuleGroup == null) return 0;

            dbRuleGroup.setViewOrder(searchRuleGroup.getViewOrder());
            searchRuleGroup.setViewOrder(modifyViewOrder);
        }

        ruleGroupMapper.updateRuleGroupViewOrder(searchRuleGroup);
        return ruleGroupMapper.updateRuleGroupViewOrder(dbRuleGroup);
    }

    /**
     * 룰 그룹 삭제
     * @param ruleGroup
     * @return
     */
    @Transactional
    public int deleteRuleGroup(RuleGroup ruleGroup) {
        List<Rule> ruleList = ruleService.getListRuleByRuleGroupId(ruleGroup.getRuleGroupId());
        for(Rule rule : ruleList) {
            ruleService.deleteRule(rule.getRuleId());
        }

        return ruleGroupMapper.deleteRuleGroup(ruleGroup);
    }
}

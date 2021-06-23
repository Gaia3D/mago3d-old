package gaia3d.support;

import gaia3d.domain.rule.RuleGroup;
import gaia3d.domain.rule.RuleGroupType;
import gaia3d.domain.rule.RuleType;

/**
 * 룰 도움
 */
public class RuleSupport {

    /**
     *
     * @param ruleTypeValue
     * @return
     */
    public static String getViewDirectory(String ruleTypeValue) {
        RuleType ruleType = RuleType.valueOf(ruleTypeValue.toUpperCase());

        String viewDirectory;
        switch (ruleType) {
            case DATA:
                viewDirectory = "data";
                break;
            case DATA_LIBRARY:
                viewDirectory = "data-library";
                break;
            case LAYER:
                viewDirectory = "layer";
                break;
            case SIMULATION:
                viewDirectory = "simulation";
                break;
            default:
                viewDirectory = null;
        }

        return viewDirectory;
    }

    /**
     * 뷰이름
     * @param ancestorRuleGroup
     * @param ruleGroup
     * @return
     */
    public static String getViewName(RuleGroup ancestorRuleGroup, RuleGroup ruleGroup) {

        RuleType ruleType = RuleType.valueOf(ancestorRuleGroup.getRuleType().toUpperCase());

        // 룰 구분으로 1차 viewName
        String viewName;
        switch (ruleType) {
            case DATA:
                viewName = "data";
                break;
            case DATA_LIBRARY:
                viewName = getDataLibraryViewName(ruleGroup);
                break;
            case LAYER:
                viewName = getLayerViewName(ruleGroup);
                break;
            case SIMULATION:
                viewName = "simulation";
                break;
            default:
                viewName = null;
        }

        return viewName;
    }

    /**
     * 데이터 라이브러리 뷰 이름
     * @param ruleGroup
     * @return
     */
    private static String getDataLibraryViewName(RuleGroup ruleGroup) {
        String viewName = null;

        RuleGroupType ruleGroupType = RuleGroupType.valueOf(ruleGroup.getRuleGroupKey().toUpperCase());
        switch (ruleGroupType) {
            case TREE:
                viewName = "tree";
                break;
            default:
                viewName = null;
        }

        return viewName;
    }

    private static String getLayerViewName(RuleGroup ruleGroup) {
        String viewName = null;

        RuleGroupType ruleGroupType = RuleGroupType.valueOf(ruleGroup.getRuleGroupKey().toUpperCase());
        switch (ruleGroupType) {
            case LAYER_ATTRIBUTES:
                viewName = "layer_attributes";
                break;
            default:
                viewName = null;
        }

        return viewName;
    }
}

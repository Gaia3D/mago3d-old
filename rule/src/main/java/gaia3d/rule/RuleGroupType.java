package gaia3d.rule;

public enum RuleGroupType {
    // 나무
    TREE,
    // 레이어 속성
    LAYER_ATTRIBUTE;

    public static boolean contains(String test) {
        for(RuleGroupType ruleGroupType : RuleGroupType.values()) {
            if(ruleGroupType.name().equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }
}

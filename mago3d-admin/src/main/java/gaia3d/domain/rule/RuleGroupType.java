package gaia3d.domain.rule;

public enum RuleGroupType {
    // 나무
    TREE,
    // 가로수
    ROAD_SIDE_TREE,
    // 가로등
    STREETLAMP,
    // 교량
    BRIDGE,
    // 방음벽
    NOISEBARRIER,
    // 육교
    OVERPASS,
    // 아파트
    APARTMENT;


    public static boolean contains(String test) {
        for(RuleGroupType ruleGroupType : RuleGroupType.values()) {
            if(ruleGroupType.name().equalsIgnoreCase(test)) {
                return true;
            }
        }
        return false;
    }
}

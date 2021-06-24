package gaia3d.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString(callSuper = true)
public class BaseRule {

    // 룰 타입. data, data-library, layer, simulation 등
    private String ruleType;

    private Integer ruleGroupId;

    private String ruleGroupName;

    private String ruleGroupKey;

    private String ruleKey;

    private String ruleName;

    // true : 사용, false : 사용안함
    private Boolean available;

    // 설명
    private String description;
}

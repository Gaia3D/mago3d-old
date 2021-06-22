package gaia3d.datalibrary;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class BaseRule extends BaseDataLibrary {

    // 룰 타입. data, data-library, layer, design-layer 등
    private String ruleType;

    private Integer ruleGroupId;

    private String ruleGroupName;

    private String ruleGroupKey;

    private String ruleKey;

    private String ruleName;
}

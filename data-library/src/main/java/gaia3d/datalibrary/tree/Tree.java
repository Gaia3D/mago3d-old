package gaia3d.datalibrary.tree;

import gaia3d.datalibrary.BaseRule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 나무
 */
@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString(callSuper = true)
public class Tree extends BaseRule {
    // 계절
    private String season;

    private Integer colorR;
    private Integer colorG;
    private Integer colorB;
}

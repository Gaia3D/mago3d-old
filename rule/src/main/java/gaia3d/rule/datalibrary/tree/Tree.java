package gaia3d.rule.datalibrary.tree;

import gaia3d.rule.DataLibraryRule;
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
public class Tree extends DataLibraryRule {
    // 계절
    private String season;

    private Integer colorR;
    private Integer colorG;
    private Integer colorB;
}

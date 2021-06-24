package gaia3d.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString(callSuper = true)
public class LayerRule extends BaseRule {
    private Integer layerId;

    private Integer layerGroupId;

    private String layerGroupKey;

    private String layerGroupName;

    // 용도. 배경, extrusion
    private String usage;
}

package gaia3d.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class DataLibraryRule extends BaseRule {
    // 데이터 라이브러리 합체형인 경우 총 개수
    private Integer totalCount;

    private Integer dataLibraryId;

    private Integer dataLibraryGroupId;

    private String dataLibraryGroupKey;

    private String dataLibraryGroupName;

    // 합체 가능한 데이터 유무. true : 합체, false : 단일
    private Boolean assemble;

    // 그리는 타입. point, line, both
    private String drawType;
}

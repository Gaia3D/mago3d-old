package gaia3d.datalibrary;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class BaseDataLibrary {
    // 데이터 라이브러리 합체형인 경우 총 개수
    private Integer totalCount;

    private Integer dataLibraryId;

    private Integer dataLibraryGroupId;

    private String dataLibraryGroupKey;

    private String dataLibraryGroupName;

    // 합체 가능한 데이터 유무. true : 합체, false : 단일
    private Boolean assemble;

    // true : 사용, false : 사용안함
    private Boolean available;

    // 그리는 타입. point, line, both
    private String drawType;

    // 설명
    private String description;
}

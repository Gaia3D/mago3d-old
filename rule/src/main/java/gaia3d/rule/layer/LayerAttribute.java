package gaia3d.rule.layer;

import gaia3d.rule.LayerRule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 속성 관리
 */
@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@ToString(callSuper = true)
public class LayerAttribute extends LayerRule {

    // 적용 이벤트. labelDisplay = 정보 표시, verticalExtrusion = 3D 건물 가시화
    private String applyEvent;

    // 속성 파일 유형. shape = 단일 Shape 파일, shapeAndCsv = Shape 파일 + CSV(추가 속성) 파일
    private String attributeType;

    // Shape 매핑 CSV 파일 컬럼명(PK)
    private String csvMappingColumnForShape;

    // CSV 추가 속성 컬럼명
    private String additionalCsvColumns;

    // 속성 파일 확장자
    private String fileExtension;
}

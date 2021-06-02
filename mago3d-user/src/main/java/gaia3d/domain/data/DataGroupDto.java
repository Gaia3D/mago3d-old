package gaia3d.domain.data;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "dataGroups")
public class DataGroupDto implements Serializable {

    private static final long serialVersionUID = 4874076788913826887L;

    // true : 기본 그룹은 제외, false : 전체
    private Boolean exceptBasic;

    // 고유번호
    private Integer dataGroupId;
    private Integer userGroupId;

    // 링크 활용 등을 위한 확장 컬럼
    @Size(max = 60)
    private String dataGroupKey;

    // 그룹명
    @Size(max = 100)
    private String dataGroupName;

    // 서비스 경로
    private String dataGroupPath;

    // admin : 관리자용 데이터 그룹, user : 일반 사용자용 데이터 그룹
    private String dataGroupTarget;
    // 스마트 타일링 고유번호
    private Integer tileId;
    // 스마트 타일링 key
    private String tileKey;
    // 스마트 타일링 경로
    private String tilePath;
    // 상태. ready : 준비, success : 성공, waiting : 승인대기, fail : 실패
    private String tileStatus;

    // 공유 타입. common : 공통, public : 공개, private : 개인, group : 그룹
    private String sharing;
    // 사용자명
    private String userId;

    // 조상
    private Integer ancestor;
    // 부모
    private Integer parent;
    // 깊이
    private Integer depth;

    // 순서
    private Integer viewOrder;
    // 자식 존재 유무
    private Integer children;

    // true : 기본, false : 선택
    private Boolean basic;
    // true : 사용, false : 사용안함
    private Boolean available;
    // 스마트 타일링 사용유무. true : 사용, false : 사용안함(기본)
    private Boolean tiling;

    // 데이터 총 건수
    private Integer dataCount;

    // POINT(위도, 경도). 공간 검색 속도 때문에 altitude는 분리
    private String location;
    // 높이
    private BigDecimal altitude;
    // Map 이동시간
    private Integer duration;

    // location 업데이트 방법. auto : data 입력시 자동, user : 사용자가 직접 입력
    private String locationUpdateType;
    // 데이터 그룹 메타 정보. 그룹 control을 위해 인위적으로 만든 속성
    private String metainfo;
    // 설명
    private String description;

    // 수정일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    // 등록일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;
}

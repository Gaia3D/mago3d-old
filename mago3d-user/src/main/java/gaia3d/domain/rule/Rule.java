package gaia3d.domain.rule;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 룰
 * @author Cheon JeongDae
 *
 */
@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "rules")
public class Rule extends Search implements Serializable {

    // 룰 아이디
    private Integer ruleId;
    // 룰 그룹 아이디
    private Integer ruleGroupId;
    //
    private String ruleGroupName;
    // 룰 타입
    private String ruleType;
    // 룰  키
    private String ruleKey;
    // 룰  명
    private String ruleName;

    private Integer dataLibraryGroupId;
    private String dataLibraryGroupName;
    
    // 업로딩 아이디
    private String userId;

    // 속성
    private String attributes;

    /**
     * 나열 순서
     */
    private Integer viewOrder;
    // 사용 유무
    private Boolean available;
    // 설명
    private String description;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime insertDate;
}

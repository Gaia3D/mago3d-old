package gaia3d.domain.rule;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 룰셋
 * @author Cheon JeongDae
 *
 */
@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rule extends Search implements Serializable {

    // 룰셋 아이디
    private Integer ruleId;
    // 룰셋 그룹 아이디
    private Integer ruleGroupId;
    //
    private String ruleGroupName;
    private String ruleGroupKey;
    // 룰 타입
    private String ruleType;
    // 룰셋  키
    private String ruleKey;
    // 룰셋  명
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

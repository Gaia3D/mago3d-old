package gaia3d.domain.rule;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 룰 그룹
 */
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleGroup extends Search implements Serializable {

	/****** 화면 표시용 *******/
	private String parentName;
	// up : 위로, down : 아래로
	private String updateType;

	// data_group_key 중복 확인을 위한 화면 전용 값. true 중복
	private String duplication;

	// 그룹 Key 중복 확인 hidden 값
	private String duplicationValue;

	/**
	 * 룰셋 그룹 고유번호
	 */
	private Integer ruleGroupId;
	
	/**
	 * 룰셋 그룹명
	 */
	@Size(max = 256)
	private String ruleGroupName;

	// 룰 그룹 Key. 확장용
	private String ruleGroupKey;

	// 룰 구분. data, data_group, data_attributes, data_library, layer, simulation 등
	private String ruleType;


	// 최상위 룰 상속 여부. true : 상속, false : 상속 안함
	private Boolean ruleInheritType;

	/**
	 * 사용자 아이디
	 */
	private String userId;

	// 조상
	private Integer ancestor;
	// 부모
	private Integer parent;
	// 깊이
	private Integer depth;
	// 순서
	private Integer viewOrder;

	// true : 기본, false : 선택
	private Boolean basic;
	
	/**
	 * 사용 유무
	 */
	private Boolean available;

	/**
	 * 설명 
	 */
	private String description;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime insertDate;
}

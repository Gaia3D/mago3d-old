package gaia3d.domain.membership;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipUsage extends Search implements Serializable {

	private static final long serialVersionUID = 3124855436215551537L;

	// 고유번호
	@NotBlank
	private Long membershipUsageId;
	// 멤버십 고유번호
	private Integer membershipId;
	// 멤버십 이름
	private String membershipName;
	// 사용자 고유번호
	private String userId;
	// 사용 용량
	private Double useUploadFileSize;
	// 사용 횟수
	private Integer useConvertFileCount;

	// 허용 용량
	private Double uploadFileSize;
	// 허용 횟수
	private Integer convertFileCount;
	
	// 멤버십 변경 날짜
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

	// 등록일
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime insertDate;
}

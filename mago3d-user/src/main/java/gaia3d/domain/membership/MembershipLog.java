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
public class MembershipLog extends Search implements Serializable {

	private static final long serialVersionUID = 3124855436215551537L;

	// 고유번호
	@NotBlank
	private Long membershipLogId;
	// 현재 멤버십 고유번호
	private Integer currentMembershipId;
	private String currentMembershipName;
	// 요청 멤버십 고유번호
	private Integer requestMembershipId;
	private String requestMembershipName;
	// 사용자 고유번호
	private String userId;
	// 상태
	private String status;

	// 등록일
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime insertDate;
}

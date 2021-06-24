package gaia3d.domain.membership;

import gaia3d.domain.common.Search;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Membership extends Search implements Serializable {

	private static final long serialVersionUID = 5801519346881682798L;

	// 고유번호
	@NotBlank
	private Integer membershipId;
	// 멤버십 이름
	private String membershipName;
	// 허용 용량
	private Integer uploadFileSize;
	// 허용 횟수
	private Integer convertFileCount;
}

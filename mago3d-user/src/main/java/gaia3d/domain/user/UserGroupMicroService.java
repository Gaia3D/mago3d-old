package gaia3d.domain.user;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 사용자 그룹별 Micro Service
 * @author jeongdae
 *
 */
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupMicroService {
	
	/****** validator ********/
	private String methodMode;
	
	// 사용자ID
	private String userId;
	// check microServiceId
	private String checkIds;
	
	// 고유번호
	private Integer userGroupMicroServiceId;
	// 사용자 그룹 고유키
	private Integer userGroupId;
	// Micro Service 고유키
	private Integer microServiceId;
	// Micro Service 명
	private String microServiceName;
	// Micro Service KEY
	private String microServiceKey;
	// 서비스 유형. cache(캐시 Reload), simulation
	private String microServiceType;

	// 상태. up : 실행, down : 정지, unknown : 알수 없음
	private String status;
	// true : 사용, false : 사용안함
	private Boolean available;
	// 설명
	private String description;

	// 등록일
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime insertDate;
}

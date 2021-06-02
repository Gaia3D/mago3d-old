package gaia3d.domain.microservice;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MicroServce 정보
 * @author Cheon JeongDae
 *
 */
@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "microServices")
public class MicroServiceDto implements Serializable {

	
	// 고유키
    private Integer microServiceId;
    // micro service key
    private String microServiceKey;
    // micro service명
    private String microServiceName;
    // 서비스 유형. cache(캐시 Reload), simulation
    private String microServiceType;

    // 서버 IP
    private String serverIp;
    // 화면 표시용 서버 IP
    private String viewServerIp;
    // API KEY
    private String apiKey;

    // 사용할 프로토콜
    private String urlScheme;
    // 호스트
    private String urlHost;
    // 포트
    private Integer urlPort;
    // 경로, 리소스 위치
    private String urlPath;

    // 상태. up : 실행, down : 정지, unknown : 알수 없음
    private String status;
    // true : 사용, false : 사용안함
    private Boolean available;
    // 설명
    private String description;

    // 마지막 Health Check 시간
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastHealthDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;
}

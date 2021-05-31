package gaia3d.domain.healthcheck;

import com.fasterxml.jackson.annotation.JsonFormat;
import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Health Check 이력
 */
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckLog extends Search implements Serializable {

    private static final long serialVersionUID = -7197949062290456744L;
    // 고유키
    private Long healthCheckLogId;
    // Micro Service 고유키
    private Integer microServiceId;
    // Micro Service 이름
    private String microServiceName;

    // 상태. up : 실행, down : 정지, unknown : 알수 없음
    private String status;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewInsertDate;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;

    public LocalDateTime getViewInsertDate() {
        return this.insertDate;
    }
}

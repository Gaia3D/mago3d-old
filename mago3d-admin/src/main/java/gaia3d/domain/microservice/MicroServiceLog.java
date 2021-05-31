package gaia3d.domain.microservice;

import com.fasterxml.jackson.annotation.JsonFormat;
import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Micro Service 이력
 */
@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MicroServiceLog extends Search implements Serializable {

    private static final long serialVersionUID = -1392861981080131514L;
    // 고유키
    private Long microServiceLogId;
    // 마이크로 서비스 고유키
    private Integer microServiceId;
    // 마이크로 서비스명(중복)
    private String microServiceName;
    // 마이크로 서비스 타입9중복)
    private String microServiceType;
    // 호출 url
    private String urlPath;
    // http status
    private String status;
    // status 가 에러일때 에러 메시지(256자까지)
    private String errorMessage;

    // 년도
    private String year;
    // 월
    private String month;
    // 일
    private String day;
    // 일년중 몇주
    private String yearWeek;
    // 이번달 몇주
    private String week;
    // 시간
    private String hour;
    // 분
    private String minute;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewInsertDate;

    // 등록일
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;

    public LocalDateTime getViewInsertDate() {
        return this.insertDate;
    }
}

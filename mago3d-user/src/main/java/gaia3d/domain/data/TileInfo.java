package gaia3d.domain.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 스마트 타일 정보
 */
@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TileInfo extends Search {

    // 스마트 타일링 고유번호
    private Integer tileId;
    // 타일명
    private String tileName;
    // 타일 Key
    private String tileKey;
    // 스마트 타일 경로
    private String tilePath;
    // 사용자 아이디
    private String userId;
    // 상태. ready : 준비, success : 성공, waiting : 승인대기, fail : 실패
    private String status;

    // true : 사용, false : 사용안함
    private Boolean available;
    // 설명
    private String description;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewUpdateDate;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewInsertDate;

    public LocalDateTime getViewUpdateDate() {
        return this.updateDate;
    }
    public LocalDateTime getViewInsertDate() {
        return this.insertDate;
    }

    // 수정일
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    // 등록일
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;
}

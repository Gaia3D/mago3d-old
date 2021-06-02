package gaia3d.domain.interest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "userInterest")
public class UserInterestDto implements Serializable {

    private static final long serialVersionUID = -977083468688246472L;

    // 고유 번호
    private Integer userInterestId;
    // 지구
    private Integer urbanId;
    // 차수
    private Integer urbanOrder;
    // 사용자 아이디
    //private String userId;
    // 즐겨찾기 json
    private String contents;
    // 수정일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    // 등록일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewUpdateDate;

    public LocalDateTime getViewUpdateDate() {
        return this.updateDate;
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewInsertDate;

    public LocalDateTime getViewInsertDate() {
        return this.insertDate;
    }

}

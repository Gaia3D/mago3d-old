package gaia3d.domain.interest;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInterest extends Search {

    // 고유 번호
    private Integer userInterestId;
    // 지구
    private Integer urbanId;
    // 차수 (1차, 2차..)
    private Integer urbanOrder;
    // 사용자 아이디
    private String userId;
    // 즐겨찾기 json
    private String contents;
    // 수정일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    // 등록일
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;

}

package gaia3d.domain.simulation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "simulationTimeSeries")
public class SimulationTimeSeriesDto implements Serializable {

    private static final long serialVersionUID = -1254478075720219761L;

    // 고유번호
    private Integer simulationTimeSeriesId;
    // 시뮬레이션 시계열명
    @Size(max = 100)
    private String simulationTimeSeriesName;
    // 사용자 아이디
    //private String userId;
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime viewInsertDate;

    public SimulationTimeSeriesDto(SimulationTimeSeriesDto timeSeries) {
        this.simulationTimeSeriesId = timeSeries.getSimulationTimeSeriesId();
        this.simulationTimeSeriesName = timeSeries.getSimulationTimeSeriesName();
        this.updateDate = timeSeries.getUpdateDate();
        this.insertDate = timeSeries.getInsertDate();
        this.viewInsertDate = timeSeries.getViewInsertDate();
        this.viewUpdateDate = timeSeries.getViewUpdateDate();
    }

    public LocalDateTime getViewUpdateDate() {
        return this.updateDate;
    }
    public LocalDateTime getViewInsertDate() {
        return this.insertDate;
    }

}

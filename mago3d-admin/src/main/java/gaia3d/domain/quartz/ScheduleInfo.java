package gaia3d.domain.quartz;

import gaia3d.domain.common.Search;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleInfo extends Search implements Serializable {

    private static final long serialVersionUID = -5052845473552300136L;

    private Integer scheduleId;
    private String jobName;
    private String jobGroup;
    private String triggerName;
    private String triggerGroup;
    private String cronSchedule;
    private String data;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertDate;

}

package gaia3d.domain.quartz;

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
public class SimpleTrigger extends Search implements Serializable {

	private static final long serialVersionUID = 5867980871266513883L;

	@NotBlank
	private Integer qrtzSimpleTriggerId;
	private String qrtzSimpleTriggerName;
	private String qrtzSimpleTriggerGroup;
	private String executorName;
	private Integer repeatCount;
	private Integer repeatInterval;
	private Integer timesTriggered;
}

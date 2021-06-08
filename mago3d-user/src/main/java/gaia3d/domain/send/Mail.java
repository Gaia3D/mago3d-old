package gaia3d.domain.send;

import lombok.*;

/**
 * 메일
 * 
 * @author hansang
 *
 */
@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
	
	private String address;
	private String title;
	private String message;

}

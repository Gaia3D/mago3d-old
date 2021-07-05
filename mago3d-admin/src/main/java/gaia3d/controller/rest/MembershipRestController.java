package gaia3d.controller.rest;

import gaia3d.controller.AuthorizationController;
import gaia3d.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 멤버십
 * @author hansang
 *
 */
@Slf4j
@RestController
@RequestMapping("/memberships")
public class MembershipRestController implements AuthorizationController {

	@Autowired
	private MembershipService membershipService;

	/**
	 * 멤버십 변경 승인
	 * @param request
	 * @param userId
	 * @return
	 */
	@PostMapping(value = "/approvals/{membershipLogId}")
	public Map<String, Object> approval(HttpServletRequest request, @PathVariable Long membershipLogId) {
		log.info("@@@@@@@ approval , membershipLogId = {}", membershipLogId);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		membershipService.updateUserMembership(membershipLogId);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}
	
}

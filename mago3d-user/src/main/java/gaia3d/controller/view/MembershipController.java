package gaia3d.controller.view;

import gaia3d.domain.Key;
import gaia3d.domain.Status;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserSession;
import gaia3d.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Sign up 처리
 * 
 * @author jeongdae
 */
@Slf4j
@Controller
@RequestMapping("/membership")
public class MembershipController {

	@Autowired
	private MembershipService membershipService;

	/**
	 * 멤버십 목록
	 * @param request
	 * @return
	 */
	@GetMapping("/list")
	public String list(HttpServletRequest request) {
		Policy policy = CacheManager.getPolicy();

		return "/membership/list";
	}

	/**
	 * 멤버십 로그 등록
	 * @param membershipId
	 * @return
	 */
	@GetMapping(value = "/insert/log/{membershipId}")
	public String insertLog(HttpServletRequest request, Model model, @PathVariable String membershipId) {

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());

		MembershipLog membershipLog = membershipService.getLastLog(userSession.getUserId());

		if(membershipLog.getStatus().equals(Status.REQUEST.getValue())){
			// 승인 대기중 상태 -> 취소만 가능한 페이지
			Membership membership = membershipService.getMembershipById(membershipLog.getRequestMembershipId());
			model.addAttribute("membership", membership);
			return "/membership/wait-approval";
		}
		MembershipLog newMembershipLog = new MembershipLog();
		newMembershipLog.setCurrentMembershipId(userSession.getMembershipId());
		newMembershipLog.setRequestMembershipId(Integer.parseInt(membershipId));
		newMembershipLog.setStatus(Status.REQUEST.getValue());
		newMembershipLog.setUserId(userSession.getUserId());
		membershipService.insertLog(newMembershipLog);

		return "/membership/insert-complete";
	}

	/**
	 * 멤버십 변경 취소
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/cancel")
	public String cancel(HttpServletRequest request, Model model) {

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		MembershipLog membershipLog = membershipService.getLastLog(userSession.getUserId());
		membershipLog.setStatus(Status.CANCEL.getValue());

		membershipService.updateLogStatus(membershipLog);

		return "/membership/cancel-complete";
	}
}

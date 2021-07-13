package gaia3d.controller.view;

import gaia3d.domain.Key;
import gaia3d.domain.MembershipStatus;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.user.UserSession;
import gaia3d.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Sign up 처리
 * 
 * @author jeongdae
 */
@Slf4j
@Controller
@RequestMapping("/mypage")
public class MembershipController {

	@Autowired
	private MembershipService membershipService;



	/**
	 * 멤버십 변경 취소
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/cancel")
	public String cancel(HttpServletRequest request, Model model) {

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		MembershipLog membershipLog = membershipService.getMembershipLastLog(userSession.getUserId());
		membershipLog.setStatus(MembershipStatus.CANCEL.name());

		membershipService.updateMembershipLog(membershipLog);

		return "/membership/cancel-complete";
	}
}

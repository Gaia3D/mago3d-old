package gaia3d.controller.view;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserGroupType;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserStatus;
import gaia3d.service.DataGroupService;
import gaia3d.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
	private UserService userService;

	@Autowired
	private PropertiesConfig propertiesConfig;

	@Autowired
	private DataGroupService dataGroupService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Sign up 페이지
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	public String list(HttpServletRequest request, Model model) {
		Policy policy = CacheManager.getPolicy();

		return "/membership/list";
	}

	/**
	 * Sign up 처리
	 * @param request
	 * @param signupForm
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/update-history")
	public String updateHistory(HttpServletRequest request, @Valid @ModelAttribute("signupForm") UserInfo signupForm, BindingResult bindingResult, Model model) {
		log.info("@@ signupForm = {}", signupForm);

		// 회원 가입
		signupForm.setUserGroupId(UserGroupType.USER.getValue());
		signupForm.setStatus(UserStatus.WAITING_APPROVAL.getValue());
		log.info("signupForm  "+signupForm);

		String dataGroupPath = signupForm.getUserId() + "/basic/";

		return "redirect:/sign/signup-complete";
	}

}

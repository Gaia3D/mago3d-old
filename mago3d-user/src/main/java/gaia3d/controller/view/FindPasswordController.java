package gaia3d.controller.view;

import gaia3d.domain.SignupType;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.security.crypto.Crypt;
import gaia3d.service.UserService;
import gaia3d.support.PasswordSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 비밀번호 찾기
 * 
 * @author hansang
 */
@Slf4j
@Controller
@RequestMapping("/sign")
public class FindPasswordController {

	@Autowired
	private UserService userService;

	/**
	 * 비밀번호 찾기 페이지
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/password/find")
	public String findPassword(HttpServletRequest request, Model model) {
		Policy policy = CacheManager.getPolicy();
		log.info("@@ policy = {}", policy);

		UserInfo userInfo = new UserInfo();
		userInfo.setSignupType(SignupType.BASIC.toString());

		model.addAttribute("userInfo", userInfo);
		model.addAttribute("policy", policy);
		model.addAttribute("contentCacheVersion", policy.getContentCacheVersion());

		return "/sign/find-password";
	}

	/**
	 * 비밀번호 찾기
	 * @param request
	 * @param userInfo
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/password/process-find")
	public String processFindPassword(HttpServletRequest request, @Valid @ModelAttribute("userInfo") UserInfo userInfo, BindingResult bindingResult, Model model) {
		log.info("@@ signupForm = {}", userInfo);
		Policy policy = CacheManager.getPolicy();

		String email = userInfo.getEmail();
		String encryptEmail = Crypt.encrypt(userInfo.getEmail());
		userInfo.setEmail(encryptEmail);
		UserInfo findUserInfo = userService.getUserForFindPassword(userInfo);

		String errorcode = "";

		if(findUserInfo == null)
			errorcode = "user.info.notexist";
		else if(SignupType.SOCIAL == SignupType.valueOf(findUserInfo.getSigninType().toUpperCase()))
			errorcode = "user.signin.social";

		log.info("@@error = {}", errorcode);

		if(errorcode != null && errorcode != ""){
			userInfo.setEmail(email);
			userInfo.setErrorCode(errorcode);
			userInfo.setPassword(null);
			model.addAttribute("userInfo", userInfo);
			model.addAttribute("policy", policy);

			return "/sign/find-password";
		}

		String tempPassword = "PASSWORD";
		String encryptPassword = PasswordSupport.encodePassword(tempPassword);

		findUserInfo.setPassword(encryptPassword);
		findUserInfo.setStatus("6");

		userService.updatePassword(findUserInfo);


		return "redirect:/sign/password/find-complete";
	}

	/**
	 * 비밀번호 찾기 완료
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/password/find-complete")
	public String findPasswordComplete(HttpServletRequest request, Model model) {

		return "/sign/find-password-complete";
	}

}

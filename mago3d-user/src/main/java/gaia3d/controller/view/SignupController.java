package gaia3d.controller.view;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.SharingType;
import gaia3d.domain.SigninType;
import gaia3d.domain.SignupType;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.data.DataGroup;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserGroupType;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserStatus;
import gaia3d.service.DataGroupService;
import gaia3d.service.UserService;
import gaia3d.support.PasswordSupport;
import gaia3d.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gaia3d.utils.LocaleUtils.getUserLocale;

/**
 * Sign up 처리
 * 
 * @author jeongdae
 */
@Slf4j
@Controller
@RequestMapping("/sign")
public class SignupController {

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
	@GetMapping("/signup")
	public String signup(HttpServletRequest request, Model model) {
		Policy policy = CacheManager.getPolicy();
		log.info("@@ policy = {}", policy);

		UserInfo userInfo = new UserInfo();
		userInfo.setSignupType(SignupType.BASIC.getValue());

		model.addAttribute("signupForm", userInfo);
		model.addAttribute("policy", policy);
		model.addAttribute("contentCacheVersion", policy.getContentCacheVersion());

		return "/sign/signup";
	}

	/**
	 * Sign up 처리
	 * @param request
	 * @param signupForm
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/process-signup")
	public String processSignup(HttpServletRequest request, @Valid @ModelAttribute("signupForm") UserInfo signupForm, BindingResult bindingResult, Model model) {
		log.info("@@ signupForm = {}", signupForm);
		Policy policy = CacheManager.getPolicy();

		Boolean duplication = userService.isUserIdDuplication(signupForm);
		log.info("@@ duplication = {}", duplication);
		if(duplication) {
			signupForm.setErrorCode("user.id.duplication");
			signupForm.setPassword(null);
			model.addAttribute("signupForm", signupForm);
			model.addAttribute("policy", policy);

			return "/sign/signup";
		}

		signupForm.setPassword(signupForm.getNewPassword());
		signupForm.setNewPassword(signupForm.getNewPassword());
		signupForm.setNewPasswordConfirm(signupForm.getPasswordConfirm());
		String errorcode = userValidate(policy, signupForm);

		log.info("@@error = {}", errorcode);

		if(errorcode != null){
			signupForm.setErrorCode(errorcode);
			signupForm.setPassword(null);
			model.addAttribute("signupForm", signupForm);
			model.addAttribute("policy", policy);

			return "/sign/signup";
		}

		// 회원 가입
		signupForm.setUserGroupId(UserGroupType.USER.getValue());
		signupForm.setStatus(UserStatus.WAITING_APPROVAL.getValue());
		log.info("signupForm  "+signupForm);
		userService.insertUser(signupForm);

		makeUserDir(request, signupForm);

		return "redirect:/sign/signup-complete";
	}

	/**
	 * Sign up 완료
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/signup-complete")
	public String signupComplete(HttpServletRequest request, Model model) {

		return "/sign/signup-complete";
	}

	/**
	 * validation 체크
	 * @param userInfo
	 * @return
	 */
	private String userValidate(Policy policy, UserInfo userInfo) {
		String errorCode = null;
		if(SigninType.findBy(userInfo.getSigninType()) == SigninType.BASIC)
			errorCode = PasswordSupport.validateUserPassword(policy, userInfo);
		if(errorCode != null)
			return errorCode;
		if(!isValidEmail(userInfo.getEmail()))
			return "user.email.invalid";
		return null;
	}

	private boolean isValidEmail(String email) {
		boolean err = false;
		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		if(m.matches()) {
			err = true;
		}
		return err;
	}

	private void makeUserDir(HttpServletRequest request, UserInfo userInfo){
		// 데이터 업로딩 경로 생성
		DataGroup dataGroup = new DataGroup();
		dataGroup.setUserId(userInfo.getUserId());

		String dataGroupPath = userInfo.getUserId() + "/basic/";
		DataGroup basicDataGroup = dataGroupService.getBasicDataGroup(dataGroup);
		if(basicDataGroup == null) {
			dataGroup.setDataGroupKey("basic");
			dataGroup.setDataGroupName(messageSource.getMessage("common.basic", null, getUserLocale(request)));
			dataGroup.setDataGroupPath(propertiesConfig.getUserDataServicePath() + dataGroupPath);
			dataGroup.setSharing(SharingType.PUBLIC.name().toLowerCase());
			dataGroup.setMetainfo("{\"isPhysical\": false}");

			dataGroupService.insertBasicDataGroup(dataGroup);
		}

		FileUtils.makeDirectoryByPath(propertiesConfig.getUserDataServiceDir(), dataGroupPath);
	}

}

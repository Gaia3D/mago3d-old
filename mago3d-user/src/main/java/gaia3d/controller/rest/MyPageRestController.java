package gaia3d.controller.rest;

import gaia3d.domain.Key;
import gaia3d.domain.MembershipStatus;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserPolicy;
import gaia3d.domain.user.UserSession;
import gaia3d.domain.user.UserStatus;
import gaia3d.security.crypto.Crypt;
import gaia3d.service.MembershipService;
import gaia3d.service.PolicyService;
import gaia3d.service.UserPolicyService;
import gaia3d.service.UserService;
import gaia3d.support.PasswordSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MyPage
 * @author hansang
 *
 */
@Slf4j
@RestController
@RequestMapping("/mypages")
public class MyPageRestController {

	@Autowired
	private PolicyService policyService;
	@Autowired
	private MembershipService membershipService;
	@Autowired
	UserPolicyService userPolicyService;
	@Autowired
	private UserService userService;

	/**
	 * 이메일 수정
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@PostMapping(value = "/emails")
	public Map<String, Object> emailUpdate(HttpServletRequest request, @ModelAttribute UserInfo userInfo) {
		log.info("@@@@@ emailUpdate userInfo = {}", userInfo);

		Map<String, Object> result = new HashMap<>();
		String message = null;
		String errorcode = null;

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		UserInfo dbUserInfo = userService.getUser(userSession.getUserId());
		String newEncryptEmail = Crypt.encrypt(userInfo.getNewEmail());

		if(userInfo.getEmail() != null && !userInfo.getEmail().equals(dbUserInfo.getViewEmail())) {
			log.info("@@@@@@@@@@@@@ email different. db = {}, input = {}", dbUserInfo.getViewEmail(), userInfo.getEmail());
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "user.email.compare.invalid");
			result.put("message", message);
			return result;
		}

		if(!isValidEmail(userInfo.getNewEmail())){
			log.info("@@@@@@@@@@@@@ user.email.invalid");
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "user.email.invalid");
			result.put("message", message);
			return result;
		}

		if(userService.isEmailDuplication(newEncryptEmail)){
			log.info("@@@@@@@@@@@@@ user.email.duplication");
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "user.email.duplication");
			result.put("message", message);
			return result;
		}

		userInfo.setUserId(userSession.getUserId());
		userInfo.setEmail(newEncryptEmail);
		userService.updateEmail(userInfo);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorcode);
		result.put("message", message);

		return result;
	}

	/**
	 * 비밀번호 수정
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@PostMapping(value = "/passwords")
	public Map<String, Object> passwordUpdate(HttpServletRequest request, @ModelAttribute UserInfo userInfo) {
		log.info("@@@@@ passwordUpdate userInfo = {}", userInfo);

		Map<String, Object> result = new HashMap<>();
		String message = null;
		String errorcode = null;

		Policy policy = policyService.getPolicy();

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		UserInfo dbUserInfo = userService.getUser(userSession.getUserId());
		if(!PasswordSupport.isEquals(dbUserInfo.getPassword(), userInfo.getPassword())) {
			log.info("@@@@@@@@@@@@@ user.password.compare.invalid");
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "user.password.compare.invalid");
			result.put("message", message);
			return result;
		}

		if(!userInfo.getNewPassword().equals(userInfo.getNewPasswordConfirm())) {
			log.info("@@@@@@@@@@@@@ user.password.confirm.invalid");
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "user.password.confirm.invalid");
			result.put("message", message);
			return result;
		}

		errorcode = PasswordSupport.validateUserPassword(policy, userInfo);
		if(errorcode != null) {
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", errorcode);
			result.put("message", message);
			return result;
		}

		userInfo.setUserId(userSession.getUserId());
		userInfo.setPassword(PasswordSupport.encodePassword(userInfo.getNewPassword()));
		userInfo.setStatus(UserStatus.USE.getValue());
		userService.updatePassword(userInfo);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorcode);
		result.put("message", message);

		return result;
	}

	private boolean isValidEmail(String email) {
		boolean result = false;
		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		if(m.matches()) {
			result = true;
		}
		return result;
	}

	/**
	 * 멤버십 로그 등록
	 * @param membershipName
	 * @return
	 */
	@PostMapping(value = "/memberships/{membershipName}")
	public Map<String, Object>  membershipLog(HttpServletRequest request, @PathVariable String membershipName) {
		log.info("@@@@@ membershipName = {}", membershipName);

		Map<String, Object> result = new HashMap<>();
		String message = null;
		String errorcode = null;

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());

		MembershipLog membershipLog = membershipService.getMembershipLastLog(userSession.getUserId());

		if( MembershipStatus.REQUEST == MembershipStatus.valueOf(membershipLog.getStatus())) {
			// TODO 에러 처리 필요
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "membership.request.approval.waiting");
			result.put("message", message);
			return result;
		}

		membershipLog.setRequestMembershipName(membershipName);
		membershipService.insertMembershipLog(membershipLog);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorcode);
		result.put("message", message);

		return result;
	}

	@PostMapping("/user-policies")
	public Map<String, Object> userPolicy(HttpServletRequest request, @Valid UserPolicy userPolicy, BindingResult bindingResult) {
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		if(bindingResult.hasErrors()) {
			message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", errorCode);
			result.put("message", message);
			return result;
		}

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		userPolicy.setUserId(userSession.getUserId());
		userPolicyService.updateUserPolicy(userPolicy);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}
}

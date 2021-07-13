package gaia3d.controller.rest;

import gaia3d.domain.Key;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserSession;
import gaia3d.domain.user.UserStatus;
import gaia3d.security.crypto.Crypt;
import gaia3d.service.PolicyService;
import gaia3d.service.UserService;
import gaia3d.support.PasswordSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
	private UserService userService;

	@Autowired
	private PolicyService policyService;

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

}

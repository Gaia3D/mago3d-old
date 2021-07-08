package gaia3d.controller.rest;

import gaia3d.domain.Key;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserSession;
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
	 * 비밀번호 수정
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@PostMapping(value = "/password/modify")
	public Map<String, Object> passwordModify(HttpServletRequest request, @ModelAttribute UserInfo userInfo) {
		log.info("@@@@@ update modifyPasswordForm = {}", userInfo);
		Policy policy = policyService.getPolicy();

		Map<String, Object> result = new HashMap<>();
		String message = null;

		String errorcode = "";

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		UserInfo dbUserInfo = userService.getUser(userSession.getUserId());

		if(!PasswordSupport.isEquals(dbUserInfo.getPassword(), userInfo.getPassword())){
			errorcode = "user.password.compare.invalid";
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("errorCode", errorcode);
			result.put("policy", policy);
			return result;
		}

		if(!userInfo.getNewPassword().equals(userInfo.getNewPasswordConfirm())){
			errorcode = "user.password.confirm.invalid";
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("errorCode", errorcode);
			result.put("policy", policy);
			return result;
		}

		errorcode = PasswordSupport.validateUserPassword(policy, userInfo);

		if(errorcode != null) {
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("errorCode", errorcode);
			result.put("policy", policy);
			return result;
		}

		userInfo.setUserId(userSession.getUserId());
		String encryptPassword = PasswordSupport.encodePassword(userInfo.getNewPassword());
		userInfo.setPassword(encryptPassword);
		userInfo.setStatus("0");
		userService.updatePassword(userInfo);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorcode);
		result.put("message", message);

		return result;
	}

	/**
	 * 이메일 수정
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@PostMapping(value = "/email/modify")
	public Map<String, Object> emailModify(HttpServletRequest request, @ModelAttribute UserInfo userInfo) {
		log.info("@@@@@ update modifyPasswordForm = {}", userInfo);
		Policy policy = policyService.getPolicy();

		Map<String, Object> result = new HashMap<>();
		String message = null;

		String errorcode = "";

		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		UserInfo dbUserInfo = userService.getUser(userSession.getUserId());
		String decryptEamil = Crypt.decrypt(dbUserInfo.getEmail());
		String newEncryptEamil = Crypt.encrypt(userInfo.getNewEmail());

		if(!userInfo.getEmail().equals(decryptEamil)){
			errorcode = "user.email.compare.invalid";
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("errorCode", errorcode);
			result.put("policy", policy);
			return result;
		}

		if(!isValidEmail(userInfo.getNewEmail())){
			errorcode = "user.email.invalid";
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("errorCode", errorcode);
			result.put("policy", policy);
			return result;
		}

		if(userService.isEmailDuplication(newEncryptEamil)){
			errorcode = "user.email.duplication";
			log.info("@@@@@@@@@@@@@ errcode = {}", errorcode);
			result.put("errorCode", errorcode);
			result.put("policy", policy);
			return result;
		}

		userInfo.setUserId(userSession.getUserId());
		userInfo.setEmail(newEncryptEamil);
		userService.updateEmail(userInfo);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorcode);
		result.put("message", message);

		return result;
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

}

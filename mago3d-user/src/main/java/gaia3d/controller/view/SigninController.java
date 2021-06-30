package gaia3d.controller.view;

import gaia3d.domain.*;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.role.RoleKey;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserSession;
import gaia3d.domain.user.UserStatus;
import gaia3d.listener.Gaia3dHttpSessionBindingListener;
import gaia3d.security.crypto.Crypt;
import gaia3d.service.SigninService;
import gaia3d.service.SigninSocialService;
import gaia3d.service.UserService;
import gaia3d.service.impl.SigninFacebookServiceImpl;
import gaia3d.service.impl.SigninGoogleServiceImpl;
import gaia3d.service.impl.SigninKakaoServiceImpl;
import gaia3d.service.impl.SigninNaverServiceImpl;
import gaia3d.support.PasswordSupport;
import gaia3d.support.RoleSupport;
import gaia3d.support.SessionUserSupport;
import gaia3d.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Sign in 처리
 *
 * @author jeongdae
 */
@Slf4j
@Controller
@RequestMapping("/sign")
public class SigninController {

	@Autowired
	private UserService userService;
	@Autowired
	private SigninService signinService;
	@Autowired
	private SigninGoogleServiceImpl signinGoogleService;
	@Autowired
	private SigninFacebookServiceImpl signinFacebookService;
	@Autowired
	private SigninNaverServiceImpl signinNaverService;
	@Autowired
	private SigninKakaoServiceImpl signinKakaoService;
	@Autowired
	RestTemplate restTemplate;

	/**
	 * Sign in 페이지
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/signin")
	public String signin(HttpServletRequest request, Model model) {
		Policy policy = CacheManager.getPolicy();

		UserInfo signinForm = new UserInfo();
		model.addAttribute("signinForm", signinForm);
		model.addAttribute("policy", policy);
		model.addAttribute("contentCacheVersion", policy.getContentCacheVersion());

		return "/sign/signin";
	}

	/**
	 * Sign in 처리
	 * @param request
	 * @param signinForm
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/process-signin")
	public String processSignin(HttpServletRequest request, @Valid @ModelAttribute("signinForm") UserInfo signinForm, BindingResult bindingResult, Model model) {

		Policy policy = CacheManager.getPolicy();

		signinForm.setPasswordChangeTerm(policy.getPasswordChangeTerm());
		signinForm.setUserLastSigninLock(policy.getUserLastSigninLock());
		UserSession userSession = signinService.getUserSession(signinForm);

		String errorCode = validate(request, policy, signinForm, userSession);
		if(errorCode != null) {
			log.error("@@ 비밀번호 " + errorCode);
			if("usersession.password.invalid".equals(errorCode)) {
				userSession.setFailSigninCount(userSession.getFailSigninCount() + 1);
				// 실패 횟수가 운영 정책의 횟수와 일치할 경우 잠금(비밀번호 실패횟수 초과)
				if(userSession.getFailSigninCount() >= policy.getUserFailSigninCount()) {
					log.error("@@ 비밀번호 실패 횟수 초과에 의해 잠김 처리됨");
					userSession.setStatus(UserStatus.FAIL_SIGNIN_COUNT_OVER.getValue());
					signinForm.setStatus(UserStatus.FAIL_SIGNIN_COUNT_OVER.getValue());
				}
				signinService.updateSigninUserSession(userSession);

				bindingResult.rejectValue("userId", "usersession.password.invalid");
			} else if("usersession.lastsignin.invalid".equals(errorCode)) {
				userSession.setStatus(UserStatus.SLEEP.getValue());
				signinService.updateSigninUserSession(userSession);

				bindingResult.rejectValue("userId", "usersession.lastsignin.invalid");
			} else {
				bindingResult.rejectValue("userId", errorCode);
			}
			signinForm.setErrorCode(errorCode);
			signinForm.setUserId(null);
			signinForm.setPassword(null);
			//signinForm.setStatus(userSession.getStatus());
			model.addAttribute("signinForm", signinForm);
			model.addAttribute("policy", policy);

			return "/sign/signin";
		}

		// 사용자 정보를 갱신
		userSession.setFailSigninCount(Integer.valueOf(0));
		signinService.updateSigninUserSession(userSession);

		// TODO 고민을 하자. 사인인 시점에 토큰을 발행해서 사용하고.... 비밀번호와 SALT는 초기화 해서 세션에 저장할지
//		userSession.setPassword(null);
//		userSession.setSalt(null);

		userSession.setSigninIp(WebUtils.getClientIp(request));
		Gaia3dHttpSessionBindingListener sessionListener = new Gaia3dHttpSessionBindingListener();
		request.getSession().setAttribute(Key.USER_SESSION.name(), userSession);
		request.getSession().setAttribute(userSession.getUserId(), sessionListener);
		if(YOrN.Y == YOrN.valueOf(policy.getSecuritySessionTimeoutYn())) {
			// 세션 타임 아웃 시간을 초 단위로 변경해서 설정
			request.getSession().setMaxInactiveInterval(Integer.valueOf(policy.getSecuritySessionTimeout()).intValue() * 60);
		}

		// 패스워드 변경 기간이 오버 되었거나 , 6:임시 비밀번호(비밀번호 찾기, 관리자 설정에 의한 임시 비밀번호 발급 시)
		if(userSession.getPasswordChangeTermOver() || UserStatus.TEMP_PASSWORD == UserStatus.findBy(userSession.getStatus())){

			return "redirect:/user/modify-password";
		}

		return "redirect:/data/map";
	}


	/**
	 * Sign in 처리(소셜 로그인)
	 * @param request
	 * @param model
	 * @param authCode
	 * @return
	 */
	@GetMapping(value = "/social-process-signin/{socialType}")
	public String socialProcessSignin(HttpServletRequest request, Model model, @PathVariable(name = "socialType") String socialType, @RequestParam(value = "code") String authCode) {

		//SigninSocialService signinSocialService = signinSocialServices.get(SocialType.valueOf(socialType).getImplementation());

		SigninSocialService signinSocialService = getSocialSigninService(socialType);

		Policy policy = CacheManager.getPolicy();

		UserInfo userInfo = signinSocialService.authorize(authCode, getTimeoutRestTemplate());
		userInfo.setPasswordChangeTerm(policy.getPasswordChangeTerm());
		userInfo.setUserLastSigninLock(policy.getUserLastSigninLock());

		String encryptEmail = Crypt.encrypt(userInfo.getEmail());

		if(userService.getUserByEmail(encryptEmail) == null){
			userInfo.setSigninType(SigninType.SOCIAL.getValue());
			userInfo.setSignupType(SignupType.SOCIAL.getValue());
			userInfo.setStatus(UserStatus.WAITING_APPROVAL.getValue());
			model.addAttribute("signupForm", userInfo);
			return "/sign/signup";
		}
		UserSession userSession = signinService.getUserSessionByEmail(userInfo);

		String errorCode = validate(request, policy, userInfo, userSession);
		if(errorCode != null){
			userInfo.setErrorCode(errorCode);
			userInfo.setUserId(null);
			userInfo.setPassword(null);
			model.addAttribute("signinForm", userInfo);
			model.addAttribute("policy", policy);

			return "/sign/signin";
		}

		setSession(request, userInfo, userSession, policy);

		return "redirect:/data/map";

	}

	/**
	 * 사용자 정보 유효성을 체크하여 에러 코드를 리턴
	 * @param request
	 * @param policy
	 * @param signinForm
	 * @param userSession
	 * @return
	 */
	private String validate(HttpServletRequest request, Policy policy, UserInfo signinForm, UserSession userSession) {

		// 사용자 정보가 존재하지 않을 경우
		if(userSession == null) {
			return "user.session.empty";
		}

		// 비밀번호 불일치(기본 로그인 사용자)
		if(SigninType.findBy(userSession.getSigninType()) == SigninType.BASIC){
			if(!PasswordSupport.isEquals(userSession.getPassword(), signinForm.getPassword())) {
				return "usersession.password.invalid";
			}
		}

		if(UserStatus.USE != UserStatus.findBy(userSession.getStatus()) && UserStatus.TEMP_PASSWORD != UserStatus.findBy(userSession.getStatus())) {
			log.info("tempPassword---------" + UserStatus.findBy(userSession.getStatus()));
			// 0:사용중, 1:사용중지(관리자), 2:잠금(비밀번호 실패횟수 초과), 3:휴면(사인인 기간), 4:만료(사용기간 종료), 5:삭제(화면 비표시)
			switch(UserStatus.findBy(userSession.getStatus())){
				case WAITING_APPROVAL : return "usersession.status.wait";
			}
			signinForm.setStatus(userSession.getStatus());
			return "usersession.status.invalid";
		}

		/*// 회원 상태 체크
		if(UserStatus.USE != UserStatus.findBy(userSession.getStatus()) && UserStatus.TEMP_PASSWORD != UserStatus.findBy(userSession.getStatus()) && UserStatus.WAITING_APPROVAL != UserStatus.findBy(userSession.getStatus())) {
			// 0:사용중, 1:사용중지(관리자), 2:잠금(비밀번호 실패횟수 초과), 3:휴면(사인인 기간), 4:만료(사용기간 종료), 5:삭제(화면 비표시)
			signinForm.setStatus(userSession.getStatus());
			return "usersession.status.invalid";
		}

		if(UserStatus.WAITING_APPROVAL == UserStatus.findBy(userSession.getStatus()))
			return "usersession.status.wait";*/

		// 사인인 실패 횟수
		if(userSession.getFailSigninCount() >= policy.getUserFailSigninCount()) {
			signinForm.setFailSigninCount(userSession.getFailSigninCount());
			return "usersession.failsignincount.invalid";
		}

		// 마지막 접속일(접속 정책이 3개월 미접속인 경우 접속 금지의 경우)
		if(userSession.getUserLastSigninLockOver()) {
			signinForm.setLastSigninDate(userSession.getLastSigninDate());
			signinForm.setUserLastSigninLock(policy.getUserLastSigninLock());
			return "usersession.lastsignin.invalid";
		}

		// 초기 세팅시만 이 값을 N으로 세팅해서 사용자 Role 체크 하지 않음
		if(YOrN.N != YOrN.valueOf(userSession.getUserRoleCheckYn())) {
			// 사용자 그룹 ROLE 확인
			List<String> userGroupRoleKeyList = CacheManager.getUserGroupRoleKeyList(userSession.getUserGroupId());
			if(!RoleSupport.isUserGroupRoleValid(userGroupRoleKeyList, RoleKey.USER_SIGNIN.name())) {
				return "usersession.role.invalid";
			}
		}

//		// 사용자 IP 체크
//		if(Policy.Y.equals(policy.getSecurity_user_ip_check_yn())) {
//			UserDevice userDevice = new UserDevice();
//			userDevice.setUser_id(userSession.getUser_id());
//			userDevice.setDevice_ip(WebUtil.getClientIp(request));
//			UserDevice dbUserDevice = userDeviceService.getUserDeviceByUserIp(userDevice);
//			if(dbUserDevice == null || dbUserDevice.getUser_device_id() == null || dbUserDevice.getUser_device_id().longValue() <= 0l) {
//				return "userdevice.ip.invalid";
//			}
//		}

		// TODO 사용기간이 종료 되었는지 확인할것

		// 중복 사인인 허용 하지 않을 경우, 동일 아이디로 생성된 세션이 존재할 경우 파기
		log.info("##################################### userDuplicationSigninYn() = {}", policy.getUserDuplicationSigninYn());
		if(YOrN.N == YOrN.valueOf(policy.getUserDuplicationSigninYn())) {
			if(SessionUserSupport.isExistSession(userSession.getUserId())) {
				log.info("######################### 중복 사인인 userId = {}", userSession.getUserId());
				SessionUserSupport.invalidateSession(userSession.getUserId());
			}
		}
		return null;
	}

	/**
	 * session setting(소셜 로그인)
	 * @param request
	 * @param userInfo
	 * @param policy
	 * @return
	 */
	private void setSession(HttpServletRequest request, UserInfo userInfo, UserSession userSession, Policy policy){
		userInfo.setPasswordChangeTerm(policy.getPasswordChangeTerm());
		userInfo.setUserLastSigninLock(policy.getUserLastSigninLock());
		signinService.updateSigninUserSession(userSession);

		userSession.setSigninIp(WebUtils.getClientIp(request));
		Gaia3dHttpSessionBindingListener sessionListener = new Gaia3dHttpSessionBindingListener();
		request.getSession().setAttribute(Key.USER_SESSION.name(), userSession);
		request.getSession().setAttribute(userSession.getUserId(), sessionListener);
		if(YOrN.Y == YOrN.valueOf(policy.getSecuritySessionTimeoutYn())) {
			// 세션 타임 아웃 시간을 초 단위로 변경해서 설정
			request.getSession().setMaxInactiveInterval(Integer.valueOf(policy.getSecuritySessionTimeout()).intValue() * 60);
		}
	}

	/**
	 * SigninSocial 서비스 설정(소셜 로그인)
	 * @param socialType
	 * @return
	 */
	private SigninSocialService getSocialSigninService(String socialType){
		switch (SocialType.findBy(socialType)){
			case GOOGLE : return signinGoogleService;
			case FACEBOOK : return signinFacebookService;
			case NAVER : return signinNaverService;
			case KAKAO : return signinKakaoService;
		}
		return null;
	}

	/**
	 * RestTamplate 설정(소셜 로그인)
	 * @return
	 */
	private RestTemplate getTimeoutRestTemplate(){
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(10*1000);
		factory.setReadTimeout(10*1000);
		restTemplate.setRequestFactory(factory);
		return restTemplate;
	}

}

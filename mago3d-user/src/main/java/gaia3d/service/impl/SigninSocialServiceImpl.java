package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.Key;
import gaia3d.domain.YOrN;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserSession;
import gaia3d.domain.user.UserStatus;
import gaia3d.listener.Gaia3dHttpSessionBindingListener;
import gaia3d.service.SigninService;
import gaia3d.service.SigninSocialService;
import gaia3d.service.UserService;
import gaia3d.utils.WebUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
@Service
public class SigninSocialServiceImpl implements SigninSocialService {

	@Autowired
	private UserService userService;

	@Autowired
	private SigninService signinService;

	@Autowired
	private PropertiesConfig propertiesConfig;

	public String processSigninGoogle(HttpServletRequest request, Model model, String authCode) {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grantType", "authorization_code");
		parameters.set("clientId", propertiesConfig.getSocialGoogleClientId());
		parameters.set("redirectUri", propertiesConfig.getSocialGoogleRedirectUri());
		parameters.set("clientSecret", propertiesConfig.getSocialGoogleClientSecret());
		parameters.set("code", authCode);

		System.out.println("-----------------" + propertiesConfig.toString()+"   /   " + propertiesConfig.getSocialGoogleClientId());

		String url = propertiesConfig.getSocialGoogleAccessTokenUri();
		String accessToken = getAccessToken(parameters, url);

		Map responseBody = getUserInfo(accessToken, propertiesConfig.getSocialGoogleUserInfoUri());

		JSONObject jsonObject = new JSONObject((Map)responseBody);

		UserInfo userInfo = new UserInfo();

		String id = jsonObject.get("email").toString();
		String email = jsonObject.get("email").toString();
		String name = jsonObject.get("name").toString();

		return checkSocialSignin(userInfo, request, model, id, email, name);
	}

	public String processSigninNaver(HttpServletRequest request, Model model, String authCode) {

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grant_type", "authorization_code");
		parameters.set("client_id", propertiesConfig.getSocialNaverClientId());
		parameters.set("redirect_uri", propertiesConfig.getSocialNaverRedirectUri());
		parameters.set("client_secret", propertiesConfig.getSocialNaverClientSecret());
		parameters.set("code", authCode);
		parameters.set("session_state", "oauth_state");

		String getTokenUrl = propertiesConfig.getSocialNaverAccessTokenUri();

		String accessToken = getAccessToken(parameters, getTokenUrl);

		Map responseBody = getUserInfo(accessToken, propertiesConfig.getSocialNaverUserInfoUri());

		JSONObject jsonObject = new JSONObject((Map)responseBody.get("response"));

		UserInfo userInfo = new UserInfo();

		String id = jsonObject.get("email").toString();
		String email = jsonObject.get("email").toString();
		String name = jsonObject.get("name").toString();

		return checkSocialSignin(userInfo, request, model, id, email, name);

	}

	public String processSigninKakao(HttpServletRequest request, Model model, String authCode) {

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grant_type", "authorization_code");
		parameters.set("client_id", propertiesConfig.getSocialKakaoClientId());
		parameters.set("redirect_uri", propertiesConfig.getSocialKakaoRedirectUri());
		parameters.set("code", authCode);

		String getTokenUrl = propertiesConfig.getSocialKakaoAccessTokenUri();

		String accessToken = getAccessToken(parameters, getTokenUrl);

		Map responseBody = getUserInfo(accessToken, propertiesConfig.getSocialKakaoUserInfoUri());

		JSONObject jsonProperties = new JSONObject((Map)responseBody.get("properties"));
		JSONObject jsonAccount = new JSONObject((Map)responseBody.get("kakao_account"));

		UserInfo userInfo = new UserInfo();

		String id = jsonAccount.get("email").toString();
		String email = jsonAccount.get("email").toString();
		String name = jsonProperties.get("nickname").toString();

		return checkSocialSignin(userInfo, request, model, id, email, name);

	}

	/**
	 * Social signin 사용자 체크(소셜 로그인)
	 * @param userInfo
	 * @param request
	 * @param model
	 * @param id
	 * @param email
	 * @param name
	 * @return
	 */
	private String checkSocialSignin(UserInfo userInfo, HttpServletRequest request, Model model, String id, String email, String name){

		if(userService.getUser(id) == null){
			userInfo.setUserId(id);
			userInfo.setEmail(email);
			userInfo.setUserName(name);
			userInfo.setSigninType("2");
			userInfo.setStatus(UserStatus.WAITING_APPROVAL.getValue());
			userService.insertUser(userInfo);
		}else{
			userInfo = userService.getUser(id);
		}

		Policy policy = CacheManager.getPolicy();

		setSession(request, userInfo, policy);

		if(UserStatus.findBy(userInfo.getStatus()) != UserStatus.USE){
			userInfo.setErrorCode("usersession.status.wait");
			model.addAttribute("signinForm", userInfo);
			return "/sign/signin";
		}

		return "redirect:/data/map";
	}

	/**
	 * Access Token 받아오기(소셜 로그인)
	 * @param parameters
	 * @param url
	 * @return
	 */
	private String getAccessToken(MultiValueMap<String, Object> parameters, String url){

		//HTTP Request를 위한 RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers);
		ResponseEntity<JSONObject> apiResponse = restTemplate.postForEntity(url, restRequest, JSONObject.class);
		JSONObject responseBody = apiResponse.getBody();

		String accessToken = (String) responseBody.get("access_token");

		return accessToken;

	}

	/**
	 * 사용자 정보 받아오기(소셜 로그인)
	 * @param accessToken
	 * @param url
	 * @return
	 */
	private Map getUserInfo(String accessToken, String url){

		// 사용자 정보 가져오기
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+accessToken);

		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		ResponseEntity<Map> apiResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		Map responseBody = apiResponse.getBody();

		return responseBody;

	}

	/**
	 * session setting(소셜 로그인)
	 * @param request
	 * @param userInfo
	 * @param policy
	 * @return
	 */
	private void setSession(HttpServletRequest request, UserInfo userInfo, Policy policy){
		userInfo.setPasswordChangeTerm(policy.getPasswordChangeTerm());
		userInfo.setUserLastSigninLock(policy.getUserLastSigninLock());
		UserSession userSession = signinService.getUserSession(userInfo);
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

}

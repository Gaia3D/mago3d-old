package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.SigninSocialService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
@Service
public class SigninSocialServiceImpl implements SigninSocialService {

	@Autowired
	private PropertiesConfig propertiesConfig;

	public UserInfo authorizeGoogle(String authCode) {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grantType", "authorization_code");
		parameters.set("clientId", propertiesConfig.getSocialGoogleClientId());
		parameters.set("redirectUri", propertiesConfig.getSocialGoogleRedirectUri());
		parameters.set("clientSecret", propertiesConfig.getSocialGoogleClientSecret());
		parameters.set("code", authCode);

		String url = propertiesConfig.getSocialGoogleAccessTokenUri();
		String accessToken = getAccessToken(parameters, url);

		Map responseBody = getUserInfo(accessToken, propertiesConfig.getSocialGoogleUserInfoUri());

		JSONObject jsonObject = new JSONObject((Map)responseBody);

		UserInfo userInfo = new UserInfo();

		String id = jsonObject.get("email").toString();
		String email = jsonObject.get("email").toString();
		String name = jsonObject.get("name").toString();

		userInfo.setUserId(id);
		userInfo.setEmail(email);
		userInfo.setUserName(name);

		return userInfo;
	}

	public UserInfo authorizeNaver(String authCode) {

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

		userInfo.setUserId(id);
		userInfo.setEmail(email);
		userInfo.setUserName(name);

		return userInfo;

	}

	public UserInfo authorizeKakao(String authCode) {

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

		userInfo.setUserId(id);
		userInfo.setEmail(email);
		userInfo.setUserName(name);

		return userInfo;

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



}

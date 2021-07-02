package gaia3d.service.impl;

import gaia3d.domain.SocialType;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.SocialAuthenticationService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 소셜 사용자 정보 취득
 * TODO google, facebook, kakao, naver로 구현체를 분리 하였다가 별 내용이 없어서 하나로 함, interface public method 분리가 어중간 한 부분도 있었음
 * @author hansang
 *
 */
@Slf4j
@Service
public class SocialAuthenticationServiceImpl implements SocialAuthenticationService {

	@Autowired
	RestTemplate restTemplate;

	/**
	 * 소셜 사용자 정보
	 * @param socialType
	 * @param authCode
	 * @return
	 */
	public UserInfo authorize(String socialType, String authCode) {
		Policy policy = CacheManager.getPolicy();
		Map<String, Object> result = getAccessToken(getParameters(socialType, authCode, policy), socialType, policy);

		int statusCode = (Integer)result.get("statusCode");
		if(statusCode == 200) {
			return getUserInfo(socialType, (String)result.get("accessToken"), policy);
		} else {
			String errorCode = null;
			if(statusCode == -1) {
				errorCode = "social.authentication.access.token.server.fail";
			} else {
				errorCode = "social.authentication.access.token.fail";
			}
			return UserInfo.builder()
					.errorCode(errorCode)
					.message((String)result.get("message")).build();
		}
	}

	private MultiValueMap<String, Object> getParameters(String socialType, String authCode, Policy policy){
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grantType", "authorization_code");
		parameters.set("code", authCode);

		switch (SocialType.valueOf(socialType)) {
			case GOOGLE :
				parameters.set("clientId", policy.getSocialSigninGoogleClientId());
				parameters.set("redirectUri", policy.getSocialSigninGoogleRedirectUri());
				parameters.set("clientSecret", policy.getSocialSigninGoogleClientSecret());
				break;
			case FACEBOOK :
				parameters.set("client_id", policy.getSocialSigninNaverClientId());
				parameters.set("redirect_uri", policy.getSocialSigninNaverRedirectUri());
				parameters.set("client_secret", policy.getSocialSigninNaverClientSecret());
				parameters.set("session_state", "oauth_state");
				break;
			case NAVER :
				parameters.set("client_id", policy.getSocialSigninNaverClientId());
				parameters.set("redirect_uri", policy.getSocialSigninNaverRedirectUri());
				parameters.set("client_secret", policy.getSocialSigninNaverClientSecret());
				parameters.set("session_state", "oauth_state");
				break;
			case KAKAO :
				parameters.set("client_id", policy.getSocialSigninKakaoClientId());
				parameters.set("redirect_uri", policy.getSocialSigninKakaoRedirectUri());
				break;
		}

		return parameters;
	};

	/**
	 *  Access Token 받아오기(소셜 로그인)
	 * @param parameters
	 * @param socialType
	 * @param policy
	 * @return
	 */
	private Map<String, Object> getAccessToken(MultiValueMap<String, Object> parameters, String socialType, Policy policy){
		Map<String, Object> result = new HashMap<>();
		int statusCode = 0;
		String message = null;
		String accessToken = null;

		String accessTokenUri = null;
		// TODO 함수형 인터페이스로 중복 제거가 가능할거 같은데...
		switch (SocialType.valueOf(socialType)) {
			case GOOGLE :
				accessTokenUri = policy.getSocialSigninGoogleAccessTokenUri();
				break;
			case FACEBOOK :
				accessTokenUri = policy.getSocialSigninFacebookAccessTokenUri();
				break;
			case NAVER :
				accessTokenUri = policy.getSocialSigninNaverAccessTokenUri();
				break;
			case KAKAO :
				accessTokenUri = policy.getSocialSigninKakaoAccessTokenUri();
				break;
		}

		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parameters, headers);
			ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(accessTokenUri, httpEntity, JSONObject.class);
			HttpStatus status = responseEntity.getStatusCode();
			log.info("### status = {}", status);
			if (HttpStatus.OK == status) {
				JSONObject jSONObject = responseEntity.getBody();
				accessToken = (String) jSONObject.get("access_token");
			}

			statusCode = status.value();
		} catch(RestClientException e) {
			statusCode = -1;
			message = e.getMessage();
			LogMessageSupport.printMessage(e, "@@@ RestClientException. message = {}", e.getMessage());
		}

		result.put("accessToken", accessToken);
		result.put("statusCode", statusCode);
		result.put("message", message);

		return result;
	}

	public UserInfo getUserInfo(String socialType, String accessToken, Policy policy) {
		UserInfo userInfo = new UserInfo();
		String message = null;
		String errorCode = null;

		String userInfoUri = null;
		// TODO 함수형 인터페이스로 중복 제거가 가능할거 같은데...
		switch (SocialType.valueOf(socialType)) {
			case GOOGLE:
				userInfoUri = policy.getSocialSigninGoogleUserInfoUri();
				break;
			case FACEBOOK:
				userInfoUri = policy.getSocialSigninFacebookUserInfoUri();
				break;
			case NAVER:
				userInfoUri = policy.getSocialSigninNaverUserInfoUri();
				break;
			case KAKAO:
				userInfoUri = policy.getSocialSigninKakaoUserInfoUri();
				break;
		}

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			HttpEntity<String> httpEntity = new HttpEntity<>("body", headers);
			ResponseEntity<Map> responseEntity = restTemplate.exchange(userInfoUri, HttpMethod.GET, httpEntity, Map.class);

			HttpStatus status = responseEntity.getStatusCode();
			log.info("### status = {}", status);

			if (HttpStatus.OK == status) {
				JSONObject result = new JSONObject((Map)responseEntity.getBody());

				switch (SocialType.valueOf(socialType)) {
					case GOOGLE:
						userInfo.setUserName(result.get("name").toString());
						userInfo.setEmail(result.get("email").toString());
						break;
					case FACEBOOK:
						userInfo.setUserName(result.get("name").toString());
						userInfo.setEmail(result.get("email").toString());
						break;
					case NAVER:
						JSONObject jsonObject = new JSONObject((Map)result.get("response"));
						userInfo.setUserName(jsonObject.get("name").toString());
						userInfo.setEmail(jsonObject.get("email").toString());
						break;
					case KAKAO:
						JSONObject jsonProperties = new JSONObject((Map)result.get("properties"));
						JSONObject jsonAccount = new JSONObject((Map)result.get("kakao_account"));
						userInfo.setUserName(jsonProperties.get("nickname").toString());
						userInfo.setEmail(jsonAccount.get("email").toString());
						break;
				}
			} else {
				errorCode = "social.authentication.user.info.fail";
			}
		} catch(RestClientException e) {
			errorCode = "social.authentication.user.info.server.fail";
			message = e.getMessage();
			LogMessageSupport.printMessage(e, "@@@ RestClientException. message = {}", e.getMessage());
		}

		userInfo.setErrorCode(errorCode);
		userInfo.setMessage(message);
		return userInfo;
	}

	/**
	 * RestTamplate 설정(소셜 로그인)
	 * @return
	 */
	private RestTemplate getRestTemplateForSocialAuthentication(){
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(10*1000);
		factory.setReadTimeout(10*1000);
		restTemplate.setRequestFactory(factory);
		return restTemplate;
	}
}

package gaia3d.service.impl;

import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.SigninSocialService;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 페이스북 Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
@Service
@AllArgsConstructor
public class SigninFacebookServiceImpl implements SigninSocialService {

	public UserInfo authorize(String authCode, RestTemplate restTemplate) {

		Policy policy = CacheManager.getPolicy();

		MultiValueMap<String, Object> parameters = setParameters(authCode, policy);

		String getTokenUrl = policy.getSocialSigninNaverAccessTokenUri();

		String accessToken = getAccessToken(restTemplate, parameters, getTokenUrl);

		Map responseBody = getSocialUserInfo(restTemplate, accessToken, policy.getSocialSigninNaverUserInfoUri());

		UserInfo userInfo = setUserInfo(responseBody);

		return userInfo;

	}

	public MultiValueMap<String, Object> setParameters(String authCode, Policy policy){

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grant_type", "authorization_code");
		parameters.set("client_id", policy.getSocialSigninNaverClientId());
		parameters.set("redirect_uri", policy.getSocialSigninNaverRedirectUri());
		parameters.set("client_secret", policy.getSocialSigninNaverClientSecret());
		parameters.set("code", authCode);
		parameters.set("session_state", "oauth_state");

		return parameters;
	};

	public UserInfo setUserInfo(Map responseBody){

		JSONObject jsonObject = new JSONObject((Map)responseBody.get("response"));

		UserInfo userInfo = new UserInfo();

		String email = jsonObject.get("email").toString();
		String name = jsonObject.get("name").toString();

		userInfo.setEmail(email);
		userInfo.setUserName(name);

		return userInfo;
	}

}

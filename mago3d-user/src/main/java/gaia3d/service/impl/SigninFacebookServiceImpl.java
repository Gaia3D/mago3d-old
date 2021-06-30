package gaia3d.service.impl;

import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.SigninSocialService;
import lombok.AllArgsConstructor;
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

		String getTokenUrl = policy.getSocialSigninFacebookAccessTokenUri();

		String accessToken = getAccessToken(restTemplate, parameters, getTokenUrl);

		Map responseBody = getSocialUserInfo(restTemplate, accessToken, policy.getSocialSigninFacebookUserInfoUri());

		UserInfo userInfo = setUserInfo(responseBody);

		return userInfo;

	}

	public MultiValueMap<String, Object> setParameters(String authCode, Policy policy){

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grant_type", "authorization_code");
		parameters.set("client_id", policy.getSocialSigninFacebookClientId());
		parameters.set("redirect_uri", policy.getSocialSigninFacebookRedirectUri());
		parameters.set("client_secret", policy.getSocialSigninFacebookClientSecret());
		parameters.set("code", authCode);
		parameters.set("session_state", "oauth_state");

		return parameters;
	};

	public UserInfo setUserInfo(Map responseBody){

		UserInfo userInfo = new UserInfo();

		String email = responseBody.get("email").toString();
		String name = responseBody.get("name").toString();

		userInfo.setEmail(email);
		userInfo.setUserName(name);

		return userInfo;
	}

}

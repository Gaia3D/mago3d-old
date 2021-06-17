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
 * 카카오 Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
@Service
@AllArgsConstructor
public class SigninKakaoServiceImpl implements SigninSocialService {

	public UserInfo authorize(String authCode, RestTemplate restTemplate) {

		Policy policy = CacheManager.getPolicy();

		MultiValueMap<String, Object> parameters = setParameters(authCode, policy);

		String getTokenUrl = policy.getSocialSigninKakaoAccessTokenUri();

		String accessToken = getAccessToken(restTemplate, parameters, getTokenUrl);

		Map responseBody = getSocialUserInfo(restTemplate, accessToken, policy.getSocialSigninKakaoUserInfoUri());

		UserInfo userInfo = setUserInfo(responseBody);

		return userInfo;

	}
	public MultiValueMap<String, Object> setParameters(String authCode, Policy policy){

		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grant_type", "authorization_code");
		parameters.set("client_id", policy.getSocialSigninKakaoClientId());
		parameters.set("redirect_uri", policy.getSocialSigninKakaoRedirectUri());
		parameters.set("code", authCode);

		return parameters;
	};

	public UserInfo setUserInfo(Map responseBody){

		JSONObject jsonProperties = new JSONObject((Map)responseBody.get("properties"));
		JSONObject jsonAccount = new JSONObject((Map)responseBody.get("kakao_account"));

		UserInfo userInfo = new UserInfo();

		String email = jsonAccount.get("email").toString();
		String name = jsonProperties.get("nickname").toString();

		userInfo.setEmail(email);
		userInfo.setUserName(name);

		return userInfo;
	}

}

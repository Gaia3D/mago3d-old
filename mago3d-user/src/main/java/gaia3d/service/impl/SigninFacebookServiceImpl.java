package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.SigninSocialService;
import net.minidev.json.JSONObject;
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
public class SigninFacebookServiceImpl implements SigninSocialService {

	private RestTemplate restTemplate;
	private PropertiesConfig propertiesConfig;

	public SigninFacebookServiceImpl(PropertiesConfig propertiesConfig, RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.propertiesConfig = propertiesConfig;
	}

	public UserInfo socialAuthorize(String authCode) {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grant_type", "authorization_code");
		parameters.set("client_id", propertiesConfig.getSocialNaverClientId());
		parameters.set("redirect_uri", propertiesConfig.getSocialNaverRedirectUri());
		parameters.set("client_secret", propertiesConfig.getSocialNaverClientSecret());
		parameters.set("code", authCode);
		parameters.set("session_state", "oauth_state");

		String getTokenUrl = propertiesConfig.getSocialNaverAccessTokenUri();

		String accessToken = getAccessToken(restTemplate, parameters, getTokenUrl);

		Map responseBody = getSocialUserInfo(restTemplate, accessToken, propertiesConfig.getSocialNaverUserInfoUri());

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

}

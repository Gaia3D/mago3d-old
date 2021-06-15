package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
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
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
@Service
@AllArgsConstructor
public class SigninGoogleServiceImpl implements SigninSocialService {

	private RestTemplate restTemplate;
	private PropertiesConfig propertiesConfig;

	public UserInfo socialAuthorize(String authCode) {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.set("grantType", "authorization_code");
		parameters.set("clientId", propertiesConfig.getSocialGoogleClientId());
		parameters.set("redirectUri", propertiesConfig.getSocialGoogleRedirectUri());
		parameters.set("clientSecret", propertiesConfig.getSocialGoogleClientSecret());
		parameters.set("code", authCode);

		String url = propertiesConfig.getSocialGoogleAccessTokenUri();

		String accessToken = getAccessToken(restTemplate, parameters, url);

		// getAuthUserInfo
		Map responseBody = getSocialUserInfo(restTemplate, accessToken, propertiesConfig.getSocialGoogleUserInfoUri());

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

}

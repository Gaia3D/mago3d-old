package gaia3d.service.impl;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.SigninSocialService;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
@Service
public class SigninKakaoServiceImpl implements SigninSocialService {

	private final PropertiesConfig propertiesConfig;

	public SigninKakaoServiceImpl(PropertiesConfig propertiesConfig) {
		this.propertiesConfig = propertiesConfig;
	}

	public UserInfo socialAuthorize(String authCode) {

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

}

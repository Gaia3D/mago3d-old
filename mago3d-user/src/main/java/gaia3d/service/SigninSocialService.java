package gaia3d.service;

import gaia3d.domain.SigninType;
import gaia3d.domain.user.UserInfo;
import gaia3d.domain.user.UserStatus;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
public interface SigninSocialService {

	/**
	 * social autorization(소셜 로그인 인증)
	 * @param authCode
	 * @return
	 */
	UserInfo socialAuthorize(@RequestParam(value = "code") String authCode);

	/**
	 * Access Token 받아오기(소셜 로그인)
	 * @param parameters
	 * @param url
	 * @return
	 */
	default String getAccessToken(RestTemplate restTemplate, MultiValueMap<String, Object> parameters, String url){

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
	default Map getSocialUserInfo(RestTemplate restTemplate, String accessToken, String url){

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+accessToken);

		HttpEntity<String> entity = new HttpEntity<>("body", headers);

		ResponseEntity<Map> apiResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		Map responseBody = apiResponse.getBody();

		return responseBody;

	}

	default UserInfo checkUser(UserService userService, UserInfo userInfo){

		if(userService.getUser(userInfo.getUserId()) == null){
			userInfo.setSigninType(SigninType.SOCIAL.getValue());
			userInfo.setStatus(UserStatus.WAITING_APPROVAL.getValue());
			userService.insertUser(userInfo);
		}else{
			userInfo = userService.getUser(userInfo.getUserId());
		}

		return userInfo;
	}
}

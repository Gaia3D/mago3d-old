package gaia3d.service;

import gaia3d.domain.policy.Policy;
import gaia3d.domain.user.UserInfo;
import gaia3d.support.LogMessageSupport;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
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
	UserInfo authorize(String authCode, RestTemplate restTemplate);

	MultiValueMap<String, Object> setParameters(String authCode, Policy policy);

	UserInfo setUserInfo(Map responseBody);


	/**
	 * Access Token 받아오기(소셜 로그인)
	 * @param parameters
	 * @param url
	 * @return
	 */
	default String getAccessToken(RestTemplate restTemplate, MultiValueMap<String, Object> parameters, String url){
		String accessToken = null;
		try{
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers);

			ResponseEntity<JSONObject> apiResponse = restTemplate.postForEntity(url, restRequest, JSONObject.class);
			JSONObject responseBody = apiResponse.getBody();
			accessToken = (String) responseBody.get("access_token");

		} catch(RestClientException e) {
			LogMessageSupport.printMessage(e, "@@@ RestClientException. message = {}", e.getMessage());
		}
		return accessToken;
	}

	/**
	 * 사용자 정보 받아오기(소셜 로그인)
	 * @param accessToken
	 * @param url
	 * @return
	 */
	default Map getSocialUserInfo(RestTemplate restTemplate, String accessToken, String url){

		Map responseBody = null;

		try{
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer "+accessToken);

			HttpEntity<String> entity = new HttpEntity<>("body", headers);

			ResponseEntity<Map> apiResponse = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
			responseBody = apiResponse.getBody();
		} catch(RestClientException e) {
			LogMessageSupport.printMessage(e, "@@@ RestClientException. message = {}", e.getMessage());
		}
		return responseBody;
	}
}

package gaia3d.service;

import gaia3d.domain.user.UserInfo;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
public interface SigninSocialService {

	/**
	 * Signin google(소셜 로그인)
	 * @param authCode
	 * @return
	 */
	UserInfo authorizeGoogle(@RequestParam(value = "code") String authCode);

	/**
	 * Signin naver(소셜 로그인)
	 * @param authCode
	 * @return
	 */
	UserInfo authorizeNaver(@RequestParam(value = "code") String authCode);

	/**
	 * Signin kakao(소셜 로그인)
	 * @param authCode
	 * @return
	 */
	UserInfo authorizeKakao(@RequestParam(value = "code") String authCode);
	
}

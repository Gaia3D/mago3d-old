package gaia3d.service;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Sign in 관련 처리(소셜 로그인)
 * @author hansang
 *
 */
public interface SigninSocialService {

	/**
	 * Signin google(소셜 로그인)
	 * @param request
	 * @param model
	 * @param authCode
	 * @return
	 */
	String processSigninGoogle(HttpServletRequest request, Model model, @RequestParam(value = "code") String authCode);

	/**
	 * Signin naver(소셜 로그인)
	 * @param request
	 * @param model
	 * @param authCode
	 * @return
	 */
	String processSigninNaver(HttpServletRequest request, Model model, @RequestParam(value = "code") String authCode);

	/**
	 * Signin kakao(소셜 로그인)
	 * @param request
	 * @param model
	 * @param authCode
	 * @return
	 */
	String processSigninKakao(HttpServletRequest request, Model model, @RequestParam(value = "code") String authCode);
	
}

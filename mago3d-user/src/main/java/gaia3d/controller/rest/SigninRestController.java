package gaia3d.controller.rest;

import gaia3d.domain.SocialType;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.Policy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequestMapping("/signins")
@RestController
public class SigninRestController {

	@GetMapping(value = "/{domain}")
	public void request(HttpServletResponse response, @PathVariable String domain) {

		Policy policy = CacheManager.getPolicy();

		String googleUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
				"client_id=" + policy.getSocialSigninGoogleClientId() +
				"&redirect_uri=" + policy.getSocialSigninGoogleRedirectUri() +
				"&response_type=code" +
				"&scope=email%20profile%20openid" +
				"&access_type=offline";

		String naverUrl = "https://nid.naver.com/oauth2.0/authorize?" +
				"client_id="+policy.getSocialSigninNaverClientId() +
				"&redirect_uri=" + policy.getSocialSigninNaverRedirectUri() +
				"&response_type=code" +
				"&scope=account_email";

		String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?" +
				"client_id=" + policy.getSocialSigninKakaoClientId() +
				"&redirect_uri=" + policy.getSocialSigninKakaoRedirectUri() +
				"&response_type=code" +
				"&scope=profile,account_email";

		String redirectURL = "";

		switch (SocialType.findBy(domain)){
			case GOOGLE : redirectURL = googleUrl;
			break;
			case FACEBOOK : redirectURL = googleUrl;
			break;
			case NAVER : redirectURL = naverUrl;
			break;
			case KAKAO : redirectURL = kakaoUrl;
			break;
		}

		try {
			response.sendRedirect(redirectURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

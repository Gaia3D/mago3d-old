package gaia3d.service;

import gaia3d.domain.user.UserInfo;

/**
 * 소셜 인증
 * @author hansang
 */
public interface SocialAuthenticationService {

	/**
	 * 사용자 정보 취득
	 * @param socialType
	 * @param authCode
	 * @return
	 */
	UserInfo authorize(String socialType, String authCode);
}

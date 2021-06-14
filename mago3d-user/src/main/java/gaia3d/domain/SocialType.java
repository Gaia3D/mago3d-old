package gaia3d.domain;

public enum SocialType {

	// 구글
	GOOGLE("signinGoogleServiceImpl"),
	// 페이스북
	FACEBOOK("signinFacebookServiceImpl"),
	// 네이버
	NAVER("signinNaverServiceImpl"),
	// 네카카오
	KAKAO("signinKakaoServiceImpl");

	private String implementation;

	SocialType(String implementation) {
		this.implementation = implementation;
	}

	public String getImplementation() {
		return this.implementation;
	}


}

package gaia3d.domain;

public enum SocialType {

	// 구글
	GOOGLE("GOOGLE"),
	// 페이스북
	FACEBOOK("FACEBOOK"),
	// 네이버
	NAVER("NAVER"),
	// 네카카오
	KAKAO("KAKAO");

	private final String value;

	SocialType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static SocialType findBy(String value) {
		for(SocialType socialType : values()) {
			if(socialType.getValue().equals(value)) return socialType;
		}
		return null;
	}

}

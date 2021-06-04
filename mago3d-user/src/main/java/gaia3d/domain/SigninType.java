package gaia3d.domain;

public enum SigninType {

	// 플랫폼 가입 사용자
	BASIC("1"),
	// 소셜 가입 사용자
	SOCIAL("2");

	private final String value;

	SigninType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}

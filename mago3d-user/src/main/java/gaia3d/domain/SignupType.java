package gaia3d.domain;

public enum SignupType {

	// 플랫폼 가입 사용자
	BASIC("1"),
	// 소셜 가입 사용자
	SOCIAL("2");

	private final String value;

	SignupType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static SignupType findBy(String value) {
		for(SignupType signupType : values()) {
			if(signupType.getValue().equals(value)) return signupType;
		}
		return null;
	}

}

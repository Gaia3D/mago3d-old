package gaia3d.domain;

import gaia3d.domain.menu.MenuTarget;

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

	public static SigninType findBy(String value) {
		for(SigninType signinType : values()) {
			if(signinType.getValue().equals(value)) return signinType;
		}
		return null;
	}

}

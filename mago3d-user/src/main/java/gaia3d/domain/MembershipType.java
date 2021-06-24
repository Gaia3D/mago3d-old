package gaia3d.domain;

public enum MembershipType {

	// 일반
	BASIC(1),
	// 실버
	SILVER(2),
	// 골드
	GOLD(3),
	// 플래티넘
	PLATINUM(4);

	private Integer value;

	MembershipType(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return this.value;
	}

	public static MembershipType findBy(Integer value) {
		for(MembershipType membershipType : values()) {
			if(membershipType.getValue().equals(value)) return membershipType;
		}
		return null;
	}

}

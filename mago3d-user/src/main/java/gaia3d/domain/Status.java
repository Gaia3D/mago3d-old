package gaia3d.domain;

public enum Status {

	// 요청
	REQUEST("REQUEST"),
	// 승인
	APPROVAL("APPROVAL"),
	// 취소
	CANCEL("CANCEL"),
	// 거절
	DENY("DENY");

	private String value;

	Status(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static Status findBy(String value) {
		for(Status status : values()) {
			if(status.getValue().equals(value)) return status;
		}
		return null;
	}

}

package gaia3d.domain.membership;

public enum MembershipType {
    BASIC(1),
    SILVER(2),
    GOLD(3),
    PLATINUM(4);

    private final int value;

    MembershipType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    /**
     * TODO values for loop 로 변환
     * @param value
     * @return
     */
    public static MembershipType findBy(int value) {
        for(MembershipType membershipType : values()) {
            if(membershipType.getValue() == value) return membershipType;
        }
        return null;
    }
}

package gaia3d.service;

import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipUsage;

public interface MembershipService {

    /**
     * 멤버십 조회
     * @param membershipId
     * @return
     */
    Membership getMembershipById(Integer membershipId);

    /**
     * 멤버십 조회
     * @param userId
     * @return
     */
    Membership getMembershipByUserId(String userId);

    /**
     * 멤버십 사용량 조회
     * @param userId
     * @return
     */
    MembershipUsage getUsageByUserId(String userId);

    /**
     * 멤버십 사용량 등록
     * @param membershipUsage
     * @return
     */
    int insertUsage(MembershipUsage membershipUsage);

    /**
     * 멤버십 사용량 갱신
     * @param membershipUsage
     * @return
     */
    int updateUsageFileSize(MembershipUsage membershipUsage);


    int updateUsageFileCount(MembershipUsage membershipUsage);
}

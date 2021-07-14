package gaia3d.service;

import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipLog;
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
    MembershipUsage getMembershipUsageByUserId(String userId);

    /**
     * 멤버십 마지막 로그 상태 조회
     * @param userId
     * @return
     */
    MembershipLog getMembershipLastLog(String userId);

    /**
     * 멤버십 사용량 등록
     * @param membershipUsage
     * @return
     */
    int insertMembershipUsage(MembershipUsage membershipUsage);

    /**
     * 멤버십 로그 등록
     * @param membershipLog
     * @return
     */
    int insertMembershipLog(MembershipLog membershipLog);

    /**
     * 멤버십 변경
     * @param membershipLog
     * @return
     */
    int updateMembership(MembershipLog membershipLog);

    /**
     * 멤버십 변환 횟수 갱신
     * @param membershipUsage
     * @return
     */
    int updateMembershipUsageFileCount(MembershipUsage membershipUsage);
    
    /**
     * 멤버십 로그 상태 변경
     * @param membershipLog
     * @return
     */
    int updateMembershipLog(MembershipLog membershipLog);

    /**
     * 멤버십 사용량 변경
     * @param membershipUsage
     * @return
     */
    int updateMembershipUsage(MembershipUsage membershipUsage);
}

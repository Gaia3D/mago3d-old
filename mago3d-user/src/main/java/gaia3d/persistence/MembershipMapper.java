package gaia3d.persistence;

import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipUsage;
import org.springframework.stereotype.Repository;

/**
 * 멤버십
 * @author hansang
 *
 */
@Repository
public interface MembershipMapper {

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
	 * 멤버십 등록
	 * @param membershipUsage
	 * @return
	 */
	int insertUsage(MembershipUsage membershipUsage);

	/**
	 * 멤버십 사용량 갱신
	 * @param membershipUsage
	 * @return
	 */
	int updateUsageCapacity(MembershipUsage membershipUsage);

	/**
	 * 멤버십 변환 횟수 갱신
	 * @param membershipUsage
	 * @return
	 */
	int updateUsageCount(MembershipUsage membershipUsage);

}

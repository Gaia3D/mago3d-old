package gaia3d.persistence;

import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.membership.MembershipUsage;
import org.springframework.stereotype.Repository;

import java.util.List;

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
	Membership getMembership(Integer membershipId);

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
	 * 멤버십 사용량 총 건수
	 * @param membershipUsage
	 * @return
	 */
	Long getMembershipUsageTotalCount(MembershipUsage membershipUsage);

	/**
	 * 멤버십 로그 총 건수
	 * @param membershipLog
	 * @return
	 */
	Long getMembershipLogTotalCount(MembershipLog membershipLog);

	/**
	 * 멤버십 사용량 목록
	 * @param membershipUsage
	 * @return
	 */
	List<MembershipUsage> getListMembershipUsage(MembershipUsage membershipUsage);

	/**
	 * 멤버십 로그 목록
	 * @param membershipLog
	 * @return
	 */
	List<MembershipLog> getListMembershipLog(MembershipLog membershipLog);

	/**
	 * 멤버십 로그 정보
	 * @param membershipLogId
	 * @return
	 */
	MembershipLog getMembershipLog(Long membershipLogId);

	/**
	 * 멤버십 등록
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
	 * 사용량
	 * @param membershipUsage
	 * @return
	 */
    int updateMembershipUsage(MembershipUsage membershipUsage);
}

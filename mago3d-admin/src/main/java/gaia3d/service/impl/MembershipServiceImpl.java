package gaia3d.service.impl;

import gaia3d.domain.MembershipStatus;
import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.membership.MembershipUsage;
import gaia3d.domain.user.UserInfo;
import gaia3d.persistence.MembershipMapper;
import gaia3d.persistence.UserMapper;
import gaia3d.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 멤버십
 * @author hansang
 *
 */
@Service
public class MembershipServiceImpl implements MembershipService {

	@Autowired
	private MembershipMapper membershipMapper;

	@Autowired
	private UserMapper userMapper;

	/**
	 * 멤버십 조회
	 * @param membershipId
	 * @return
	 */
	@Transactional
	public Membership getMembership(Integer membershipId) {
		return membershipMapper.getMembership(membershipId);
	}

	/**
	 * 멤버십 조회
	 * @param userId
	 * @return
	 */
	@Transactional
	public Membership getMembershipByUserId(String userId) {
		return membershipMapper.getMembershipByUserId(userId);
	}

	/**
	 * 멤버십 사용량 조회
	 * @param userId
	 * @return
	 */
	@Transactional
	public MembershipUsage getMembershipUsageByUserId(String userId) {
		return membershipMapper.getMembershipUsageByUserId(userId);
	}

	/**
	 * 멤버십 사용량 총 건수
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public Long getMembershipUsageTotalCount(MembershipUsage membershipUsage) {
		return membershipMapper.getMembershipUsageTotalCount(membershipUsage);
	}

	/**
	 * 멤버십 로그 총 건수
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public Long getMembershipLogTotalCount(MembershipLog membershipLog) {
		return membershipMapper.getMembershipLogTotalCount(membershipLog);
	}

	/**
	 * 멤버십 사용량 목록
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public List<MembershipUsage> getListMembershipUsage(MembershipUsage membershipUsage) {
		return membershipMapper.getListMembershipUsage(membershipUsage);
	}

	/**
	 * 멤버십 로그 목록
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public List<MembershipLog> getListMembershipLog(MembershipLog membershipLog) {
		return membershipMapper.getListMembershipLog(membershipLog);
	}

	/**
	 * 멤버십 등록
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public int insertMembershipUsage(MembershipUsage membershipUsage) {
		return membershipMapper.insertMembershipUsage(membershipUsage);
	}
	
	/**
	 * 멤버십 로그 등록
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public int insertMembershipLog(MembershipLog membershipLog) {
		return membershipMapper.insertMembershipLog(membershipLog);
	}

	/**
	 * 멤버십 변환 횟수 갱신
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public int updateMembershipUsageFileCount(MembershipUsage membershipUsage) {
		return membershipMapper.updateMembershipUsageFileCount(membershipUsage);
	}

	/**
	 * 멤버십 로그 상태 변경
	 * @param membershipLogId
	 * @return
	 */
	@Transactional
	public int updateUserMembership(Long membershipLogId) {
		MembershipLog dbMembershipLog = membershipMapper.getMembershipLog(membershipLogId);

		UserInfo userInfo = UserInfo.builder()
				.userId(dbMembershipLog.getUserId())
				.membershipId(dbMembershipLog.getRequestMembershipId())
				.build();
		userMapper.updateUser(userInfo);

		MembershipUsage membershipUsage = MembershipUsage.builder()
				.userId(dbMembershipLog.getUserId())
				.membershipId(dbMembershipLog.getRequestMembershipId())
				.build();
		membershipMapper.updateMembershipUsage(membershipUsage);

		MembershipLog membershipLog = MembershipLog.builder().
				membershipLogId(dbMembershipLog.getMembershipLogId())
				.status(MembershipStatus.APPROVAL.name())
				.build();
		return membershipMapper.updateMembershipLog(membershipLog);
	}
}

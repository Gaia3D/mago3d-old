package gaia3d.service.impl;

import gaia3d.domain.Status;
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
	public Membership getMembershipById(Integer membershipId) {
		return membershipMapper.getMembershipById(membershipId);
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
	public MembershipUsage getUsageByUserId(String userId) {
		return membershipMapper.getUsageByUserId(userId);
	}

	/**
	 * 멤버십 마지막 로그 상태 조회
	 * @param userId
	 * @return
	 */
	@Transactional
	public MembershipLog getLastLog(String userId) {
		return membershipMapper.getLastLog(userId);
	}

	/**
	 * 멤버십 사용량 총 건수
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public Long getUsageTotalCount(MembershipUsage membershipUsage) {
		return membershipMapper.getUsageTotalCount(membershipUsage);
	}

	/**
	 * 멤버십 로그 총 건수
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public Long getLogTotalCount(MembershipLog membershipLog) {
		return membershipMapper.getLogTotalCount(membershipLog);
	}

	/**
	 * 멤버십 사용량 목록
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public List<MembershipUsage> getListUsage(MembershipUsage membershipUsage) {
		return membershipMapper.getListUsage(membershipUsage);
	}

	/**
	 * 멤버십 로그 목록
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public List<MembershipLog> getListLog(MembershipLog membershipLog) {
		return membershipMapper.getListLog(membershipLog);
	}

	/**
	 * 멤버십 등록
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public int insertUsage(MembershipUsage membershipUsage) {
		return membershipMapper.insertUsage(membershipUsage);
	}
	
	/**
	 * 멤버십 로그 등록
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public int insertLog(MembershipLog membershipLog) {
		return membershipMapper.insertLog(membershipLog);
	}

	/**
	 * 멤버십 변환 횟수 갱신
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public int updateUsageFileCount(MembershipUsage membershipUsage) {
		return membershipMapper.updateUsageFileCount(membershipUsage);
	}

	/**
	 * 멤버십 로그 상태 변경
	 * @param membershipLog
	 * @return
	 */
	@Transactional
	public int updateLogStatus(MembershipLog membershipLog) {
		System.out.println(membershipLog.getStatus());
		if(membershipLog.getStatus().equals(Status.APPROVAL.getValue())){
			System.out.println("membershipLog.getStatus()");
			UserInfo userInfo = new UserInfo();
			userInfo.setUserId(membershipLog.getUserId());
			userInfo.setMembershipId(membershipLog.getRequestMembershipId());
			userMapper.updateUser(userInfo);

			MembershipUsage membershipUsage = new MembershipUsage();
			membershipUsage.setUserId(membershipLog.getUserId());
			membershipUsage.setMembershipId(membershipLog.getRequestMembershipId());
			membershipMapper.updateUsage(membershipUsage);
		}
		return membershipMapper.updateLogStatus(membershipLog);
	}
}

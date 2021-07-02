package gaia3d.service.impl;

import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.membership.MembershipUsage;
import gaia3d.persistence.MembershipMapper;
import gaia3d.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 멤버십
 * @author hansang
 *
 */
@Service
public class MembershipServiceImpl implements MembershipService {

	@Autowired
	private MembershipMapper membershipMapper;

	/**
	 * 멤버십 조회
	 * @param membershipId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Membership getMembershipById(Integer membershipId) {
		return membershipMapper.getMembershipById(membershipId);
	}

	/**
	 * 멤버십 조회
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly=true)
	public Membership getMembershipByUserId(String userId) {
		return membershipMapper.getMembershipByUserId(userId);
	}

	/**
	 * 멤버십 사용량 조회
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly=true)
	public MembershipUsage getUsageByUserId(String userId) {
		return membershipMapper.getUsageByUserId(userId);
	}

	/**
	 * 멤버십 마지막 로그 상태 조회
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly=true)
	public MembershipLog getLastLog(String userId) {
		return membershipMapper.getLastLog(userId);
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
		return membershipMapper.updateLogStatus(membershipLog);
	}

	/**
	 * 멤버십 사용량 갱신
	 * @param membershipUsage
	 * @return
	 */
	@Transactional
	public int updateUsage(MembershipUsage membershipUsage) {
		return membershipMapper.updateUsage(membershipUsage);
	}

}

package gaia3d.service.impl;

import gaia3d.domain.SignupType;
import gaia3d.domain.Status;
import gaia3d.domain.membership.Membership;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.membership.MembershipType;
import gaia3d.domain.membership.MembershipUsage;
import gaia3d.domain.user.UserGroupType;
import gaia3d.domain.user.UserInfo;
import gaia3d.persistence.UserMapper;
import gaia3d.security.crypto.Crypt;
import gaia3d.service.MembershipService;
import gaia3d.service.UserService;
import gaia3d.support.PasswordSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자
 * @author jeongdae
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private MembershipService membershipService;

	/**
	 * 사용자 ID 중복 체크
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly = true)
	public Boolean isUserIdDuplication(UserInfo userInfo) {
		return userMapper.isUserIdDuplication(userInfo);
	}

	/**
	 * 사용자 Email 중복 체크
	 * @param email
	 * @return
	 */
	@Transactional(readOnly = true)
	public Boolean isEmailDuplication(String email) {
		String encEmail = Crypt.encrypt(email);
		return userMapper.isEmailDuplication(encEmail);
	}

	/**
	 * 사용자 정보 취득
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly=true)
	public UserInfo getUser(String userId) {
		return userMapper.getUser(userId);
	}

	/**
	 * 사용자 정보 취득(email)
	 * @param email
	 * @return
	 */
	@Transactional(readOnly=true)
	public UserInfo getUserByEmail(String email) {
		return userMapper.getUserByEmail(email);
	}

	/**
	 * 사용자 정보 취득(비밀번호 찾기)
	 * @param userInfo
	 * @return
	 */
	@Transactional(readOnly=true)
	public UserInfo getUserForFindPassword(UserInfo userInfo) {
		return userMapper.getUserForFindPassword(userInfo);
	}

	/**
	 * 사용자 등록
	 * @param userInfo
	 * @return
	 */
	@Transactional
	public int insertUser(UserInfo userInfo) {

		userInfo.setUserGroupId(UserGroupType.USER.getValue());
		if(SignupType.BASIC == SignupType.valueOf(userInfo.getSignupType().toUpperCase())) {
			userInfo.setPassword(PasswordSupport.encodePassword(userInfo.getPassword()));
		} else {
			userInfo.setPassword(PasswordSupport.encodePassword("PASSWORD"));
		}
		userInfo.setEmail(Crypt.encrypt(userInfo.getEmail()));
		
		// 멤버십 사용량
		Membership membership = membershipService.getMembershipById(MembershipType.BASIC.getValue());
		MembershipUsage membershipUsage = new MembershipUsage();
		membershipUsage.setMembershipId(membership.getMembershipId());
		membershipUsage.setMembershipName(membership.getMembershipName());
		membershipUsage.setUserId(userInfo.getUserId());
		membershipService.insertUsage(membershipUsage);

		// 멤버십 로그
		MembershipLog membershipLog = new MembershipLog();
		membershipLog.setUserId(userInfo.getUserId());
		membershipLog.setCurrentMembershipId(membership.getMembershipId());
		membershipLog.setRequestMembershipId(membership.getMembershipId());
		membershipLog.setStatus(Status.APPROVAL.getValue());
		membershipService.insertLog(membershipLog);

		return userMapper.insertUser(userInfo);
	}

	/**
	 * 사용자 비밀번호 수정
	 * @param userInfo
	 * @return
	 */
	@Transactional
	public int updatePassword(UserInfo userInfo) {
		return userMapper.updatePassword(userInfo);
	}

	/**
	 * 사용자 삭제
	 * @param userId
	 * @return
	 */
	@Transactional
	public int deleteUser(String userId) {
		return userMapper.deleteUser(userId);
	}
}

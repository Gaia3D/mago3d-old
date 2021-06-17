package gaia3d.service.impl;

import gaia3d.domain.user.UserInfo;
import gaia3d.persistence.UserMapper;
import gaia3d.security.Crypt;
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
	 * 사용자 등록
	 * @param userInfo
	 * @return
	 */
	@Transactional
	public int insertUser(UserInfo userInfo) {
		if(userInfo.getSignupType().equals("1")){
			userInfo.setPassword(PasswordSupport.encodePassword(userInfo.getPassword()));
		}
		else{
			userInfo.setPassword(PasswordSupport.encodePassword("PASSWORD"));
			userInfo.setUserGroupId(2);
		}
		userInfo.setEmail(Crypt.encrypt(userInfo.getEmail()));
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
}

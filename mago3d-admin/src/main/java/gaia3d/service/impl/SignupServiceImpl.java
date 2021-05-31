package gaia3d.service.impl;

import gaia3d.domain.user.UserInfo;
import gaia3d.persistence.SignupMapper;
import gaia3d.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupServiceImpl implements SignupService {

    @Autowired
    private SignupMapper signupMapper;

    /**
     * 회원 가입
     * @param userInfo
     * @return
     */
    @Transactional
    public int insert(UserInfo userInfo) {
        return signupMapper.insert(userInfo);
    }
}

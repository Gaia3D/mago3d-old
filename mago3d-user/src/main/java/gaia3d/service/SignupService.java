package gaia3d.service;

import gaia3d.domain.user.UserInfo;

public interface SignupService {

    /**
     * 회원 가입
     * @param userInfo
     * @return
     */
    int insert(UserInfo userInfo);
}

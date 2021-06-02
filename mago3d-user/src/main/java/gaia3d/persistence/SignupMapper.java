package gaia3d.persistence;

import gaia3d.domain.user.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupMapper {

    /**
     * 회원 가입
     * @param userInfo
     * @return
     */
    int insert(UserInfo userInfo);
}

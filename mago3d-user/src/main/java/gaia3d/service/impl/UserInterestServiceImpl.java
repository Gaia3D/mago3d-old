package gaia3d.service.impl;

import gaia3d.domain.interest.UserInterest;
import gaia3d.persistence.UserInterestMapper;
import gaia3d.service.UserInterestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserInterestServiceImpl implements UserInterestService {

    @Autowired
    private UserInterestMapper userInterestMapper;

    /**
     * 사용자 즐겨찾기 목록 갯수
     *
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기 목록 갯수
     */
    @Transactional(readOnly=true)
    public int getListUserInterestTotalCount(UserInterest userInterest) {
        return userInterestMapper.getListUserInterestTotalCount(userInterest);
    }

    /**
     * 사용자 즐겨찾기 목록 조회
     *
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기 목록
     */
    @Transactional(readOnly=true)
    public List<UserInterest> getListUserInterest(UserInterest userInterest) {
        return userInterestMapper.getListUserInterest(userInterest);
    }

    /**
     * 사용자 즐겨찾기 조회
     *
     * @param id 고유번호
     * @return 사용자 즐겨찾기
     */
    @Transactional(readOnly=true)
    public UserInterest getUserInterestById(Integer id) {
        UserInterest userInterest = UserInterest.builder()
                .userInterestId(id)
                .build();
        return userInterestMapper.getUserInterest(userInterest);
    }

    /**
     * 사용자 즐겨찾기 조회
     *
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기
     */
    @Transactional(readOnly=true)
    public UserInterest getUserInterest(UserInterest userInterest) {
        return userInterestMapper.getUserInterest(userInterest);
    }

//    /**
//     * 사용자 즐겨찾기 등록
//     *
//     * @param userInterest 사용자 즐겨찾기
//     * @return 사용자 즐겨찾기 등록 결과
//     */
//    @Override
//    public int insertUserInterest(UserInterest userInterest) {
//        userInterestMapper.insertUserInterest(userInterest);
//        return userInterest.getUserInterestId();
//    }

    /**
     * 사용자 즐겨찾기 갱신
     *
     * @param userInterest 수정될 사용자 즐겨찾기
     * @return 사용자 즐겨찾기 갱신 결과
     */
    @Transactional
    public int update(UserInterest userInterest) {
        UserInterest dbUserInterest = userInterestMapper.getUserInterest(userInterest);
        log.info("@@ dbUserInterest = {}", dbUserInterest);
        if(dbUserInterest == null) {
            return userInterestMapper.insertUserInterest(userInterest);
        } else {
            int updatedId = dbUserInterest.getUserInterestId();
            userInterest.setUserInterestId(updatedId);
            userInterestMapper.updateUserInterest(userInterest);
            return updatedId;
        }
    }

//    /**
//     * 사용자 즐겨찾기 삭제
//     *
//     * @param userInterest 삭제될 사용자 즐겨찾기
//     * @return 사용자 즐겨찾기 삭제 결과
//     */
//    @Override
//    public int deleteUserInterest(UserInterest userInterest) {
//        return userInterestMapper.deleteUserInterest(userInterest);
//    }
}

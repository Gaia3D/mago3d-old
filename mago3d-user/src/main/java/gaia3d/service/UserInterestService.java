package gaia3d.service;

import gaia3d.domain.interest.UserInterest;

import java.util.List;

/**
 * 사용자 즐겨찾기
 *
 * @author yhjeong
 */
public interface UserInterestService {

    /**
     * 사용자 즐겨찾기 목록 갯수
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기 목록 갯수
     */
    int getListUserInterestTotalCount(UserInterest userInterest);

    /**
     * 사용자 즐겨찾기 목록 조회
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기 목록
     */
    List<UserInterest> getListUserInterest(UserInterest userInterest);

    /**
     * 사용자 즐겨찾기 조회
     * @param id 고유번호
     * @return 사용자 즐겨찾기
     */
    UserInterest getUserInterestById(Integer id);

    /**
     * 사용자 즐겨찾기 조회
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기
     */
    UserInterest getUserInterest(UserInterest userInterest);

//    /**
//     * 사용자 즐겨찾기 등록
//     * @param userInterest 사용자 즐겨찾기
//     * @return 사용자 즐겨찾기 등록 결과
//     */
//    int insertUserInterest(UserInterest userInterest);

    /**
     * 사용자 즐겨찾기 갱신
     * @param userInterest 수정될 사용자 즐겨찾기
     * @return 사용자 즐겨찾기 갱신 결과
     */
    int update(UserInterest userInterest);

//    /**
//     * 사용자 즐겨찾기 삭제
//     * @param userInterest 삭제될 사용자 즐겨찾기
//     * @return 사용자 즐겨찾기 삭제 결과
//     */
//    int deleteUserInterest(UserInterest userInterest);

}

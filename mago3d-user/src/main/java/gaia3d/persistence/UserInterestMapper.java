package gaia3d.persistence;

import gaia3d.domain.interest.UserInterest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInterestMapper {

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
     * @param userInterest 사용자 즐겨찾기 검색용 파라미터
     * @return 사용자 즐겨찾기
     */
    UserInterest getUserInterest(UserInterest userInterest);

    /**
     * 사용자 즐겨찾기 등록
     * @param userInterest 사용자 즐겨찾기
     * @return 사용자 즐겨찾기 등록 결과
     */
    int insertUserInterest(UserInterest userInterest);

    /**
     * 사용자 즐겨찾기 갱신
     * @param userInterest 수정될 사용자 즐겨찾기
     * @return 사용자 즐겨찾기 갱신 결과
     */
    int updateUserInterest(UserInterest userInterest);

    /**
     * 사용자 즐겨찾기 삭제
     * @param userInterest 삭제될 사용자 즐겨찾기
     * @return 사용자 즐겨찾기 삭제 결과
     */
    int deleteUserInterest(UserInterest userInterest);

}

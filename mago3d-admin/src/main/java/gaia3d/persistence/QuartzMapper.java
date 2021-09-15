package gaia3d.persistence;

import gaia3d.domain.quartz.ScheduleInfo;
import gaia3d.domain.quartz.SimpleTrigger;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 쿼츠
 * @author hansang
 *
 */
@Repository
public interface QuartzMapper {

	/**
	 * SimpleTrigger 목록
	 * @return
	 */
	List<SimpleTrigger> getListSimpleTrigger();

	/**
	 * ScheduleInfo 목록
	 * @return
	 */
    List<ScheduleInfo> getListScheduleInfo();

	/**
	 * ScheduleInfo 조회
	 * @return
	 */
	ScheduleInfo getScheduleInfo(Integer scheduleInfoId);

	/**
	 * ScheduleInfo 등록
	 * @param scheduleInfo
	 * @return
	 */
    int insertScheduleInfo(ScheduleInfo scheduleInfo);

	/**
	 * ScheduleInfo 정보 수정
	 * @param scheduleInfo
	 * @return
	 */
	int updateScheduleInfo(ScheduleInfo scheduleInfo);
}

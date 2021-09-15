//package gaia3d.service.impl;
//
//import gaia3d.persistence.QuartzMapper;
//import gaia3d.service.QuartzService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * 쿼츠
// * @author hansang
// *
// */
//@Service
//public class QuartzServiceImpl implements QuartzService {
//
//	@Autowired
//	private QuartzMapper quartzMapper;
//
//	/**
//	 * SimpleTrigger 목록
//	 * @return
//	 */
//	public List<SimpleTrigger> getListSimpleTrigger(){;
//		return quartzMapper.getListSimpleTrigger();
//	}
//
//}

package gaia3d.service.impl;

import gaia3d.domain.quartz.ScheduleInfo;
import gaia3d.persistence.QuartzMapper;
import gaia3d.service.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Quartz
 * @author hansang
 *
 */
@Service
public class QuartzServiceImpl implements QuartzService {

    @Autowired
    private QuartzMapper quartzMapper;


    /**
     * ScheduleInfo 목록
     * @return
     */
    @Transactional(readOnly=true)
    public List<ScheduleInfo> getListScheduleInfo() {
        return quartzMapper.getListScheduleInfo();
    }

    /**
     * ScheduleInfo 조회
     * @param scheduleInfoId
     * @return
     */
    @Transactional(readOnly = true)
    public ScheduleInfo getScheduleInfo(Integer scheduleInfoId){
        return quartzMapper.getScheduleInfo(scheduleInfoId);
    }

    /**
     * ScheduleInfo 등록
     * @param scheduleInfo
     * @return
     */
    @Transactional
    public int insertScheduleInfo(ScheduleInfo scheduleInfo) {
        return quartzMapper.insertScheduleInfo(scheduleInfo);
    }

    /**
     * ScheduleInfo 정보 수정
     * @param scheduleInfo
     * @return
     */
    @Transactional
    public int updateScheduleInfo(ScheduleInfo scheduleInfo) {
        return quartzMapper.updateScheduleInfo(scheduleInfo);
    }

}


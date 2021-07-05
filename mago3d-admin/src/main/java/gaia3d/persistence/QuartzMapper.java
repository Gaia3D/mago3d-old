package gaia3d.persistence;

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

}

package gaia3d.service.impl;

import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceLog;
import gaia3d.persistence.MicroServiceMapper;
import gaia3d.service.MicroServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Role
 * @author jeongdae
 *
 */
@Service
public class MicroServiceServiceImpl implements MicroServiceService {
	
	@Autowired
	private MicroServiceMapper microServiceMapper;

	/**
	 * 마이크로 서비스 목록
	 * @param microService
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<MicroService> getListMicroService(MicroService microService) {
		return microServiceMapper.getListMicroService(microService);
	}

	/**
	 * 마이크로 서비스 로그 등록
	 * @param microServiceLog
	 * @return
	 */
	@Transactional
	public int insertMicroServiceLog(MicroServiceLog microServiceLog) {
		return microServiceMapper.insertMicroServiceLog(microServiceLog);
	}
}

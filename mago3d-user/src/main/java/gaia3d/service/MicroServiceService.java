package gaia3d.service;

import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceLog;

import java.util.List;

public interface MicroServiceService {

    /**
     * 마이크로 서비스 목록
     * @param microService
     * @return
     */
    List<MicroService> getListMicroService(MicroService microService);

    /**
     * 마이크로 서비스 로그 등록
     * @param microServiceLog
     * @return
     */
    int insertMicroServiceLog(MicroServiceLog microServiceLog);
}

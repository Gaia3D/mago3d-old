package gaia3d.api;

import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceLog;
import gaia3d.domain.microservice.MicroServiceStatus;
import gaia3d.domain.microservice.MicroServiceType;
import gaia3d.service.MicroServiceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 솔루션 사용해서 구현해야 함.
 * TODO 그냥 흉내를 내 볼까 하다가, 시간, 실력이 부족 다음에~ 구현
 */
@Slf4j
@AllArgsConstructor
public abstract class APIGateway {

    private final MicroServiceService microServiceService;
    protected static final String URL_PATH_COLON = ":";
    protected static final String URL_PATH_QUERYSTRING = "?";
    protected static final String URL_PATH_DELIMITER = "/";


    /**
     * 요청 전 처리 필터
     * 인증 처리, 헤더에서 토큰을 읽어서 유효성 체크를 한다.
     * 요청 로그, 서비스 제한 등 처리
     */
    protected void preFiler() {

    }

    /**
     * 추가 필터 처리
     */
    protected void customFiler() {

    }

    /**
     * 라우팅 필터
     * 조건에 따라 특정 서버로 전달하고자 할 때 이곳에 로직 구현.
     */
    public ResponseEntity<?> routingFiler(MicroServiceType type, String path, String param) {

        String message = null;
        HttpStatus httpStatus;
        ResponseEntity<?> responseEntity = ResponseEntity.noContent().build();

        // 1. 캐시에서 상태 확인
        MicroService microService = CacheManager.getMicroService(type.name());

//        // 상태. up : 실행, down : 정지, unknown : 알수 없음
//        private String status;
//        // true : 사용, false : 사용안함
//        private Boolean available;

        // 2. 서비스 호출
        if (microService.getAvailable()) {
            String status = microService.getStatus().toUpperCase();
            if (MicroServiceStatus.UP == MicroServiceStatus.valueOf(status)) {
                // 여기서 호출
                responseEntity = getResponseEntity(microService, path, param);
                httpStatus = responseEntity.getStatusCode();
            } else {
                // 503
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
        } else {
            // 사용 불가. 423
            httpStatus = HttpStatus.LOCKED;
        }

        message = httpStatus.getReasonPhrase();

        // 여기에... 항목들 줄줄이 저장
        MicroServiceLog microServiceLog = MicroServiceLog.builder()
                .microServiceId(microService.getMicroServiceId())
                .microServiceName(microService.getMicroServiceName())
                .microServiceType(type.name())
                .urlPath(path)
                .status(httpStatus.toString())
                .errorMessage(message)
                .build();

        // 3. 결과 저장
        postFiler(microServiceLog);

        return responseEntity;
    }

    // TODO 서비스 별로 구현해야 할 메소드
    abstract ResponseEntity<?> getResponseEntity(MicroService microService, String path, String param);

    protected String createServiceUrl(MicroService service) {
        String serviceUrl = service.getUrlScheme() + URL_PATH_COLON + URL_PATH_DELIMITER + URL_PATH_DELIMITER + service.getServerIp();
        if (service.getUrlPort() != 80) {
            serviceUrl += URL_PATH_COLON + service.getUrlPort();
        }
        serviceUrl += URL_PATH_DELIMITER + service.getUrlPath();
        return serviceUrl;
    }

    /**
     * 후 처리 필터
     * 결과 로그, 서비스 모니터링 처리
     */
    protected void postFiler(MicroServiceLog microServiceLog) {
        microServiceService.insertMicroServiceLog(microServiceLog);
    }

    /**
     * 에러 처리 필터
     * 에러에 따른 공통 처리
     */
    protected void errorFiler() {

    }

}

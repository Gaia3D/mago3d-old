package gaia3d.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@PropertySource("classpath:mago3d.properties")
@ConfigurationProperties(prefix = "mago3d")
public class PropertiesConfig {
	
	/**
	 * 로컬 : local, 개발 : develop, 운영 : product
	 */
	private String profile;
	
    /**
     * 로컬 환경은 WINDOW, 운영 환경은 LINUX 
     */
    private String osType;
    
    /**
     * 로컬 환경에서 mock 사용여부
     */
    private boolean mockEnable;
    private boolean callRemoteEnable;
    private String serverIp;
    
    // http, https
    private String restTemplateMode;
    private String restAuthKey;
    private String gisRestServer;
    // 운영 서버용. 로그 표시 유무 설정
    private boolean logDisplay;

    // layer 파일 업로딩 디렉토리
    private String layerUploadDir;
    // layer export 용 임시 디렉토리
    private String layerExportDir;
    
    private String rabbitmqServerHost;
	private String rabbitmqServerPort;
	private String rabbitmqUser;
	private String rabbitmqPassword;
    private String rabbitmqConverterQueue;
    private String rabbitmqTilerQueue;
    private String rabbitmqConverterExchange;
    private String rabbitmqConverterRoutingKey;
    private String rabbitmqTilerRoutingKey;
    
    // F4D 파일이 변환되는 Root 경로 이자, mago3DJS 에서 요청되는 파일의 Root 경로. ServletConfig 에서 매핑
    private String dataServiceDir;
    // F4D 변환 결과 로그 저장 경로
    private String dataConverterLogDir;
    // 관리자용
    private String adminDataServiceDir;
    private String adminDataLibraryServiceDir;
    private String adminDataServicePath;
    private String adminDataLibraryServicePath;
    // 사용자용
    private String userDataServiceDir;
    private String userDataLibraryServiceDir;
    private String userDataServicePath;
    private String userDataLibraryServicePath;

    private String dataUploadDir;
    private String dataBulkUploadDir;
    // smart tiling 데이터 업로드 디렉토리
    private String dataSmartTilingDir;
    private String dataAttributeDir;
    private String dataAttributeUploadDir;
    private String dataObjectAttributeUploadDir;
    
    private String guideDataServiceDir;

    private String adminWindServiceDir;
    private String adminWindServicePath;
    private String adminPm25ServiceDir;
    private String adminPm25ServicePath;
    private String adminPm25WithEmisServiceDir;
    private String adminPm25WithEmisServicePath;
    private String adminStinkServiceDir;
    private String adminStinkServicePath;

    private String userWindServiceDir;
    private String userWindServicePath;
    private String userPm25ServiceDir;
    private String userPm25ServicePath;
    private String userPm25WithEmisServiceDir;
    private String userPm25WithEmisServicePath;
    private String userStinkServiceDir;
    private String userStinkServicePath;


    // 소셜 로그인 api
    private String socialGoogleClientId;
    private String socialGoogleClientSecret;
    private String socialGoogleRedirectUri;
    private String socialGoogleAccessTokenUri;
    private String socialGoogleUserInfoUri;

    private String socialFacebookClientId;
    private String socialFacebookClientSecret;
    private String socialFacebookRedirectUri;
    private String socialFacebookAccessTokenUri;
    private String socialFacebookUserInfoUri;

    private String socialNaverClientId;
    private String socialNaverClientSecret;
    private String socialNaverRedirectUri;
    private String socialNaverAccessTokenUri;
    private String socialNaverUserInfoUri;

    private String socialKakaoClientId;
    private String socialKakaoRedirectUri;
    private String socialKakaoAccessTokenUri;
    private String socialKakaoUserInfoUri;

}

package gaia3d.api;

import gaia3d.domain.microservice.MicroService;
import gaia3d.service.MicroServiceService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Component
public class APIGatewaySensorThings extends APIGateway {

    private final RestTemplate restTemplate;

    public APIGatewaySensorThings(MicroServiceService microServiceService, RestTemplate restTemplate) {
        super(microServiceService);
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<?> getResponseEntity(MicroService microService, String path, String param) {
        String serviceUrl = createServiceUrl(microService);
        log.info("@@@@ service-url: {}", serviceUrl);
        //String url = "http://iot.openindoormap.io/v1.0/";
        //String url = "http://localhost:8888/FROST-Server/v1.0/";
        URI uri = getSendURI(serviceUrl, path, param);
        return restTemplate.getForEntity(uri, String.class);
    }

    private URI getSendURI(String url, String path, String param) {
        URI uri = null;
        try {
            String lastStr = url.substring(url.length() - 1);
            if (!lastStr.equals(URL_PATH_DELIMITER)) {
                url += URL_PATH_DELIMITER;
            }
            String sendUri = url + path;
            if (param != null && !param.isEmpty()) {
                sendUri += URL_PATH_QUERYSTRING + param;
            }
            log.info("@@@@ frost-url: {}", sendUri);
            uri = new URI(sendUri);
        } catch (URISyntaxException e) {
            LogMessageSupport.printMessage(e);
        }
        return uri;
    }

}

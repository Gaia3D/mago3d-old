package gaia3d.service.router;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.SocialType;
import gaia3d.service.SigninSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SigninSocialServiceRouter {

    //의존성 List로 주입(..)
    private final Map<String, SigninSocialService> sampleInterfaces;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PropertiesConfig propertiesConfig;

    public SigninSocialService getImplemetationByType(SocialType socialType) {

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10*1000);
        factory.setReadTimeout(10*1000);

        restTemplate.setRequestFactory(factory);

        SigninSocialService signinSocialService = sampleInterfaces.get(socialType.getImplementation());
        return signinSocialService;
    }

}

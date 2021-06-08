package gaia3d.service.router;

import gaia3d.config.PropertiesConfig;
import gaia3d.domain.SocialType;
import gaia3d.service.SigninSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SigninSocialServiceRouter {

    private final Map<String, SigninSocialService> sampleInterfaces; //의존성 List로 주입

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PropertiesConfig propertiesConfig;

    public SigninSocialService getImplemetationByType(SocialType sampleType) {
        System.out.println(sampleInterfaces.keySet());
        SigninSocialService signinSocialService = sampleInterfaces.get(sampleType.getImplementation());
        return signinSocialService;
    }

}

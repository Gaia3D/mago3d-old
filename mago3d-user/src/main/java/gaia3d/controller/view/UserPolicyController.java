package gaia3d.controller.view;

import gaia3d.domain.Key;
import gaia3d.domain.user.UserPolicy;
import gaia3d.domain.user.UserSession;
import gaia3d.service.UserPolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/mypage")
public class UserPolicyController {

    @Autowired
    private UserPolicyService userPolicyService;

    @GetMapping("/user-policy")
    public String geoPolicy(HttpServletRequest request, Model model) {
        UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
        String userId = userSession.getUserId();

        UserPolicy userPolicy = userPolicyService.getUserPolicy(userId);

        model.addAttribute("userPolicy", userPolicy);

        return "/mypage/user-policy";
    }
}

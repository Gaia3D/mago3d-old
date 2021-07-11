package gaia3d.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.config.PropertiesConfig;
import gaia3d.domain.Key;
import gaia3d.domain.user.UserSession;
import gaia3d.domain.user.UserStatus;
import gaia3d.support.URLSupport;
import gaia3d.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 보안 관련 체크 인터셉터
 * @author jeongdae
 *
 */
@Slf4j
@Component
public class SecurityInterceptor implements HandlerInterceptor {

	@Autowired
	private PropertiesConfig propertiesConfig;
	@Autowired
	private ObjectMapper objectMapper;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


    	String uri = request.getRequestURI();
    	String requestIp = WebUtils.getClientIp(request);
    	log.info("## Requst URI = {}, Method = {}, Request Ip = {}, referer={}", uri, request.getMethod(), requestIp, request.getHeader("referer"));
    	
    	if(uri.indexOf("/error") >= 0) {
    		log.info("error pass!!!");
    		printHead(request);
    		return true;
    	}

    	// 인증이 필요한 url 인지 확인
    	boolean isAuthenticationRequired = false;
    	int authenticationRequiredCount = URLSupport.AUTHENTICATION_REQUIRED_URL.length;
    	for(int i=0 ; i<authenticationRequiredCount; i++) {
    		if(uri.indexOf(URLSupport.AUTHENTICATION_REQUIRED_URL[i]) >= 0) {
				isAuthenticationRequired = true;
    			break;
    		}
    	}
    	
    	// 인증이 필요한 url 이 아니면
    	if(!isAuthenticationRequired) {
    		return true;
    	}

    	String loginUrl = URLSupport.SIGNIN_URL;
    	if(uri.indexOf(URLSupport.POPUP_URL) >=0) {
    		loginUrl = URLSupport.POPUP_SIGNIN_URL;
    	}
    	
		// 세션이 존재하지 않는 경우
		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		if(userSession == null || userSession.getUserId() == null || "".equals(userSession.getUserId())) {
			log.info("Session is Null. userSession = {}", userSession);
			if (isAjax(request)) {
				log.info("## ajax call session null. uri = {}", uri);
				Map<String, Object> unauthorizedResult = new HashMap<>();
				if (!isAuthenticationRequired) {
					unauthorizedResult.put("statusCode", HttpStatus.UNAUTHORIZED.value());
					unauthorizedResult.put("errorCode", "session.required");
					unauthorizedResult.put("message", null);
				} else {
					unauthorizedResult.put("statusCode", HttpStatus.UNAUTHORIZED.value());
					unauthorizedResult.put("errorCode", "session.required");
					unauthorizedResult.put("message", loginUrl);
				}
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
//				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.getWriter().write(objectMapper.writeValueAsString(unauthorizedResult));
				return false;
			} else {
				response.sendRedirect(loginUrl);
				return false;
			}
		}
		
		// 임시 비밀번호 사용자는 로그인, 패스워드 변경 페이지외에 갈수 없음
		if(UserStatus.TEMP_PASSWORD == UserStatus.findBy(userSession.getStatus())) {
			log.info(" =========== Temporary Password User Access Error =========== ");
			response.sendRedirect(loginUrl);
	    	return false;
		}

        return true;
    }
    
    private boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}
    
    private void printHead(HttpServletRequest request) {
    	Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
        	String headerName = headerNames.nextElement();
        	log.info("headerName = {}", headerName);
        	Enumeration<String> headers = request.getHeaders(headerName);
        	while (headers.hasMoreElements()) {
        		String headerValue = headers.nextElement();
        		log.info(" ---> headerValue = {}", headerValue);
        	}
        }
    }
}

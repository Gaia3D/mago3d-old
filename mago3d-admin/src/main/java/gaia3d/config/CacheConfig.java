package gaia3d.config;

import gaia3d.domain.ProfileType;
import gaia3d.domain.YOrN;
import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.cache.CacheName;
import gaia3d.domain.cache.CacheParams;
import gaia3d.domain.cache.CacheType;
import gaia3d.domain.menu.Menu;
import gaia3d.domain.menu.MenuTarget;
import gaia3d.domain.microservice.MicroService;
import gaia3d.domain.microservice.MicroServiceType;
import gaia3d.domain.policy.Policy;
import gaia3d.domain.role.RoleTarget;
import gaia3d.domain.user.UserGroup;
import gaia3d.domain.user.UserGroupMenu;
import gaia3d.domain.user.UserGroupRole;
import gaia3d.service.MenuService;
import gaia3d.service.MicroServiceService;
import gaia3d.service.PolicyService;
import gaia3d.service.UserGroupService;
import gaia3d.support.LogMessageSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CacheConfig {

	@Autowired
	private MenuService menuService;
	@Autowired
	private MicroServiceService microServiceService;
	@Autowired
	private PropertiesConfig propertiesConfig;
	@Autowired
	private PolicyService policyService;
	@Autowired
	private RestTemplate restTemplate;
    @Autowired
    private UserGroupService userGroupService;

    @PostConstruct
    public void init() {
		CacheManager.setProfile(propertiesConfig.getProfile().toUpperCase());
		if(ProfileType.LOCAL == ProfileType.valueOf(CacheManager.getProfile())) {
        	LogMessageSupport.stackTraceEnable = true;
        }
		LogMessageSupport.logDisplay = propertiesConfig.isLogDisplay();
		log.info("*** User Profile = {}, stackTraceEnable = {}, logDisplay = {}", propertiesConfig.getProfile(), LogMessageSupport.stackTraceEnable, LogMessageSupport.logDisplay);
        
    	log.info("*************************************************");
        log.info("************ Admin Cache Init Start *************");
        log.info("*************************************************");

        CacheParams cacheParams = new CacheParams();
		cacheParams.setCacheType(CacheType.SELF);

		// 운영 정책 캐시 갱신
		policy(cacheParams);
        // 사용자 그룹
		userGroup(cacheParams);
        // 사용자 그룹별 메뉴, Menu
        menu(cacheParams);
        // 사용자 그룹별 메뉴, Role
        role(cacheParams);
        // terrain
		terrain(cacheParams);
        
        log.info("*************************************************");
        log.info("************* Admin Cache Init End **************");
        log.info("*************************************************");
    }
    
    public void loadCache(CacheParams cacheParams) {
		CacheName cacheName = cacheParams.getCacheName();
		
		if(cacheName == CacheName.POLICY) policy(cacheParams);
		else if(cacheName == CacheName.GEO_POLICY) geoPolicy(cacheParams);
		else if(cacheName == CacheName.MENU) menu(cacheParams);
		else if(cacheName == CacheName.MICRO_SERVICE) microService(cacheParams);
		else if(cacheName == CacheName.ROLE) role(cacheParams);
		else if(cacheName == CacheName.USER_GROUP) userGroup(cacheParams);
		else if(cacheName == CacheName.TERRAIN) terrain(cacheParams);
	}

	/**
	 * TODO 임시
	 * micro service
	 */
	private void microService(CacheParams cacheParams) {
		log.info("************ Cache Reload Micro Service ************");
		CacheType cacheType = cacheParams.getCacheType();
		if(cacheType == CacheType.BROADCAST) {
			callRemoteCache(cacheParams);
		}
	}
    
    /**
     * policy
     * @param cacheParams
     */
    private void policy(CacheParams cacheParams) {
    	log.info("************ Cache Reload Policy ************");

		Policy policy = policyService.getPolicy();
		CacheManager.setPolicy(policy);

    	CacheType cacheType = cacheParams.getCacheType();
    	if(cacheType == CacheType.BROADCAST) {
    		callRemoteCache(cacheParams);
    	}
    }
    
    /**
     * 2D, 3D 운영 정책
     * @param cacheParams
     */
    private void geoPolicy(CacheParams cacheParams) {
    	log.info("************ Cache Reload Policy ************");
    	CacheType cacheType = cacheParams.getCacheType();
    	if(cacheType == CacheType.BROADCAST) {
    		callRemoteCache(cacheParams);
    	}
    }
    
    /**
     * 사용자 그룹
     */
    private void userGroup(CacheParams cacheParams) {
    	log.info("************ Cache Reload userGroup ************");
    	CacheParams selfParams = new CacheParams();
    	selfParams.setCacheType(CacheType.SELF);
    	menu(selfParams);
    	role(selfParams);
    	
    	CacheType cacheType = cacheParams.getCacheType();
    	if(cacheType == CacheType.BROADCAST) {
    		callRemoteCache(cacheParams);
    	}
    }

    /**
     * menu
     * @param cacheParams
     */
    private void menu(CacheParams cacheParams) {
    	log.info("************ Cache Reload menu ************");
    	Map<Integer, Menu> menuMap = new HashMap<>();
		Map<String, Integer> menuUrlMap = new HashMap<>();
		Menu adminMenu = new Menu();
		adminMenu.setDefaultYn(null);
		adminMenu.setMenuTarget(MenuTarget.ADMIN.getValue());

		List<Menu> menuList = menuService.getListMenu(adminMenu);
		for(Menu menu : menuList) {
			menuMap.put(menu.getMenuId(), menu);
			menuUrlMap.put(menu.getUrl(), menu.getMenuId());
		}

    	UserGroup inputUserGroup = new UserGroup();
    	inputUserGroup.setAvailable(true);
    	List<UserGroup> userGroupList = userGroupService.getListUserGroup();

    	Map<Integer, List<UserGroupMenu>> userGroupMenuMap = new HashMap<>();

    	UserGroupMenu userGroupMenu = new UserGroupMenu();
    	userGroupMenu.setMenuTarget(MenuTarget.ADMIN.getValue());

    	for(UserGroup userGroup : userGroupList) {
    		Integer userGroupId = userGroup.getUserGroupId();

    		userGroupMenu.setUserGroupId(userGroupId);
    		userGroupMenu.setUseYn(YOrN.Y.toString());
    		List<UserGroupMenu> userGroupMenuList = userGroupService.getListUserGroupMenu(userGroupMenu);
    		userGroupMenuMap.put(userGroupId, userGroupMenuList);
    	}

    	CacheManager.setMenuMap(menuMap);
		CacheManager.setMenuUrlMap(menuUrlMap);
    	CacheManager.setUserGroupMenuMap(userGroupMenuMap);

    	CacheType cacheType = cacheParams.getCacheType();
		if(cacheType == CacheType.BROADCAST) {
			callRemoteCache(cacheParams);
		}
    }
    
    /**
     * 사용자 그룹, 메뉴, Role
     */
    private void role(CacheParams cacheParams) {
    	log.info("************ Cache Reload role ************");
    	UserGroup inputUserGroup = new UserGroup();
    	inputUserGroup.setAvailable(true);
    	List<UserGroup> userGroupList = userGroupService.getListUserGroup();

    	Map<Integer, List<String>> userGroupRoleMap = new HashMap<>();

    	UserGroupRole userGroupRole = new UserGroupRole();
    	userGroupRole.setRoleTarget(RoleTarget.ADMIN.getValue());
    	for(UserGroup userGroup : userGroupList) {
    		Integer userGroupId = userGroup.getUserGroupId();

    		userGroupRole.setUserGroupId(userGroupId);
    		List<String> userGroupRoleKeyList = userGroupService.getListUserGroupRoleKey(userGroupRole);
    		userGroupRoleMap.put(userGroupId, userGroupRoleKeyList);
    	}

    	CacheManager.setUserGroupRoleMap(userGroupRoleMap);

    	CacheType cacheType = cacheParams.getCacheType();
		if(cacheType == CacheType.BROADCAST) {
			callRemoteCache(cacheParams);
		}
    }

	/**
	 * Terrain
	 */
	private void terrain(CacheParams cacheParams) {
		log.info("************ Cache Reload terrain ************");
		CacheType cacheType = cacheParams.getCacheType();
		if(cacheType == CacheType.BROADCAST) {
			callRemoteCache(cacheParams);
		}
	}

    /**
	 * Remote Cache 갱신 요청
	 * @param cacheParams
	 */
	private void callRemoteCache(CacheParams cacheParams) {

		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@ callRemoteCache start! ");
		if(!propertiesConfig.isCallRemoteEnable()) {
			log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@ isCallRemoteEnable = {}. skip callRemoteCache ", propertiesConfig.isCallRemoteEnable());
			return;
		}

		CacheName cacheName = cacheParams.getCacheName();
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@ cacheName ={}", cacheName.toString());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("cache_name=" + cacheName.toString());
		
		String authData = stringBuilder.toString();

		MicroService cacheMicroService = new MicroService();
		cacheMicroService.setMicroServiceType(MicroServiceType.CACHE.toString().toLowerCase());
		List<MicroService> microServiceList = microServiceService.getListMicroService(cacheMicroService);
		for (MicroService microService : microServiceList) {
			String cacheUrl = microService.getUrlScheme() + "://" + microService.getUrlHost() + ":" + microService.getUrlPort() + microService.getUrlPath();
			log.info("----- cacheUrl = {}", cacheUrl);
			try {
				@SuppressWarnings("unchecked")
				ResponseEntity<String> result = restTemplate.postForEntity(cacheUrl, authData, String.class);
				log.info("----- statusCode = {}", result.getStatusCode());
				log.info("----- body = {}", result.getBody());
			} catch (Exception e) {
				LogMessageSupport.printMessage(e, "cache reload 원격 api 호출 실패 = {}", e.getMessage());
			}
		}
	}
}

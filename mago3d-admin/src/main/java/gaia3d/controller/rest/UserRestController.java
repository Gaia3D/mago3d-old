package gaia3d.controller.rest;

import gaia3d.config.PropertiesConfig;
import gaia3d.controller.AuthorizationController;
import gaia3d.domain.ServerTarget;
import gaia3d.domain.SharingType;
import gaia3d.domain.data.DataGroup;
import gaia3d.domain.user.UserGroupType;
import gaia3d.domain.user.UserInfo;
import gaia3d.service.DataGroupService;
import gaia3d.service.UserService;
import gaia3d.utils.FileUtils;
import gaia3d.utils.LocaleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static gaia3d.domain.user.UserGroupType.SUPER_ADMIN;

/**
 * TODO 관리자를 등록하는 경우, 디렉토리도 생성해 줘야 함
 * 사용자
 * @author kimhj
 *
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserRestController implements AuthorizationController {

	@Autowired
	private DataGroupService dataGroupService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private PropertiesConfig propertiesConfig;
	@Autowired
	private UserService userService;

	/**
	 * 사용자 ID 중복 체크
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@GetMapping(value = "/duplication")
	public Map<String, Object> userIdDuplicationCheck(HttpServletRequest request, UserInfo userInfo) {
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		// TODO @Valid 로 구현해야 함
		if(ObjectUtils.isEmpty(userInfo.getUserId())) {
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "user.id.empty");
			result.put("message", message);
			return result;
		}

		Boolean duplication = userService.isUserIdDuplication(userInfo);
		log.info("@@ duplication = {}", duplication);
		int statusCode = HttpStatus.OK.value();

		result.put("duplication", duplication);
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * 사용자 등록
	 * @param request
	 * @param userInfo
	 * @param bindingResult
	 * @return
	 */
	@PostMapping(value = "/insert")
	public Map<String, Object> insert(HttpServletRequest request, @Valid @ModelAttribute UserInfo userInfo, BindingResult bindingResult) {
		log.info("@@@@@ insert userInfo = {}", userInfo);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;
		int statusCode;

		if(bindingResult.hasErrors()) {
			message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			log.info("@@@@@ message = {}", message);
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", errorCode);
			result.put("message", message);
            return result;
		}

		userService.insertUser(userInfo);
		statusCode = HttpStatus.OK.value();

		String dataServiceDir;
		String dataServicePath;
		String dataGroupPath;
		String dataGroupTarget;
		if(SUPER_ADMIN != UserGroupType.findBy(userInfo.getUserGroupId())) {
			dataGroupTarget = ServerTarget.USER.name().toLowerCase();
			dataServiceDir = propertiesConfig.getUserDataServiceDir();
			dataServicePath = propertiesConfig.getUserDataServicePath();
			dataGroupPath = userInfo.getUserId() + "/basic/";

			try {
				createUserDataGroupDirectory(LocaleUtils.getUserLocale(request), userInfo, dataGroupTarget, dataServiceDir, dataServicePath, dataGroupPath);
			} catch (Exception e) {
				log.info("user.data.group.directory.make.fail path = {}", dataServiceDir + dataGroupPath);

				FileUtils.deleteFileRecursive(dataServiceDir + dataGroupPath);
				userService.deleteUser(userInfo.getUserId());
				statusCode = HttpStatus.BAD_REQUEST.value();
				errorCode = "user.data.group.directory.make.fail";
			}
		}

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * 사용자 그룹 디렉토리 생성
	 */
	private void createUserDataGroupDirectory(Locale locale, UserInfo userInfo, String dataGroupTarget, String dataServiceDir, String dataServicePath, String dataGroupPath) throws Exception {
		// 데이터 업로딩 경로 생성
		DataGroup dataGroup = new DataGroup();
		dataGroup.setUserId(userInfo.getUserId());
		dataGroup.setDataGroupKey("basic");
		dataGroup.setDataGroupTarget(dataGroupTarget);
		dataGroup.setDataGroupName(messageSource.getMessage("common.basic", null, locale));
		dataGroup.setDataGroupPath(dataServicePath + dataGroupPath);
		dataGroup.setSharing(SharingType.PUBLIC.name().toLowerCase());
		dataGroup.setMetainfo("{\"isPhysical\": false}");
		dataGroupService.insertBasicDataGroup(dataGroup);

		if(!FileUtils.makeDirectoryByPath(dataServiceDir, dataGroupPath)) {
			dataGroupService.deleteDataGroup(dataGroup);
			throw new Exception("user.data.group.directory.make.fail");
		}
	}

	/**
	 * 사용자 수정
	 * @param request
	 * @param userInfo
	 * @return
	 */
	@PutMapping(value = "/{userId}")
	public Map<String, Object> update(HttpServletRequest request, @PathVariable String userId, UserInfo userInfo) {
		log.info("@@ userInfo = {}", userInfo);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

//		if(bindingResult.hasErrors()) {
//			message = bindingResult.getAllErrors().get(0).getDefaultMessage();
//			log.info("@@@@@ message = {}", message);
//			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
//			result.put("errorCode", errorCode);
//			result.put("message", message);
//            return result;
//		}

		userInfo.setUserId(userId);
		userService.updateUser(userInfo);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * 사용 대기자 사용 승인
	 * @param request
	 * @param userId
	 * @return
	 */
	@PostMapping(value = "/{userId}/approvals")
	public Map<String, Object> approval(HttpServletRequest request, @PathVariable String userId) {
		log.info("@@@@@@@ approval , userId = {}", userId);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		userService.updateUserStatusUse(userId);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}

	/**
	 * 사용자 상태 변경
	 * @param request
	 * @param checkIds
	 * @param statusValue
	 * @return
	 */
	@PostMapping(value = "/status")
	public Map<String, Object> status(HttpServletRequest request,
										@RequestParam("checkIds") String checkIds,
										@RequestParam("statusValue") String statusValue) {

		log.info("@@@@@@@ checkIds = {}, statusValue = {}", checkIds, statusValue);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		if(checkIds.length() <= 0) {
			result.put("statusCode", HttpStatus.BAD_REQUEST.value());
			result.put("errorCode", "check.value.required");
			result.put("message", message);
            return result;
		}

		userService.updateUserStatus(statusValue, checkIds);
		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		return result;
	}
}

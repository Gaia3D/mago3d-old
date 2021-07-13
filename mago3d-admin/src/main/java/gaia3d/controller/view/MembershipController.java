package gaia3d.controller.view;

import gaia3d.controller.AuthorizationController;
import gaia3d.domain.Key;
import gaia3d.domain.PageType;
import gaia3d.domain.common.Pagination;
import gaia3d.domain.membership.MembershipLog;
import gaia3d.domain.membership.MembershipUsage;
import gaia3d.domain.role.RoleKey;
import gaia3d.domain.user.UserSession;
import gaia3d.service.MembershipService;
import gaia3d.service.PolicyService;
import gaia3d.support.SQLInjectSupport;
import gaia3d.utils.DateUtils;
import gaia3d.utils.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 멤버십
 * @author hansang
 *
 */
@Slf4j
@Controller
@RequestMapping("/membership")
public class MembershipController implements AuthorizationController {

	@Autowired
	private MembershipService membershipService;

	@Autowired
	private PolicyService policyService;

	/**
	 * 사용량 목록
	 * @param request
	 * @param membershipUsage
	 * @param pageNo
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/usage/list")
	public String usageList(HttpServletRequest request, @RequestParam(defaultValue="1") String pageNo, MembershipUsage membershipUsage, Model model) {
		log.info("@@ membershipUsage = {}", membershipUsage);

		membershipUsage.setSearchWord(SQLInjectSupport.replaceSqlInection(membershipUsage.getSearchWord()));
		membershipUsage.setOrderWord(SQLInjectSupport.replaceSqlInection(membershipUsage.getOrderWord()));

		String roleCheckResult = roleValidate(request);
		if(roleValidate(request) != null) return roleCheckResult;

		String today = DateUtils.getToday(FormatUtils.YEAR_MONTH_DAY);
		if(!ObjectUtils.isEmpty(membershipUsage.getStartDate())) {
			membershipUsage.setStartDate(membershipUsage.getStartDate().substring(0, 8) + DateUtils.START_TIME);
		}
		if(!ObjectUtils.isEmpty(membershipUsage.getEndDate())) {
			membershipUsage.setEndDate(membershipUsage.getEndDate().substring(0, 8) + DateUtils.END_TIME);
		}

		long totalCount = membershipService.getMembershipUsageTotalCount(membershipUsage);
		Pagination pagination = new Pagination(request.getRequestURI(),
										getSearchParameters(PageType.LIST, membershipUsage),
										totalCount,
										Long.parseLong(pageNo),
										membershipUsage.getListCounter());

		membershipUsage.setOffset(pagination.getOffset());
		membershipUsage.setLimit(pagination.getPageRows());

		List<MembershipUsage> membershipUsageList = new ArrayList<>();
		if(totalCount > 0L) {
			membershipUsageList = membershipService.getListMembershipUsage(membershipUsage);
		}

		model.addAttribute(pagination);
		model.addAttribute("membershipUsageList", membershipUsageList);

		return "/membership/usage-list";
	}

	/**
	 * 사용자 목록
	 * @param request
	 * @param membershipLog
	 * @param pageNo
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/log/list")
	public String logList(HttpServletRequest request, @RequestParam(defaultValue="1") String pageNo, MembershipLog membershipLog, Model model) {
		membershipLog.setSearchWord(SQLInjectSupport.replaceSqlInection(membershipLog.getSearchWord()));
		membershipLog.setOrderWord(SQLInjectSupport.replaceSqlInection(membershipLog.getOrderWord()));

		String roleCheckResult = roleValidate(request);
		if(roleValidate(request) != null) return roleCheckResult;

		String today = DateUtils.getToday(FormatUtils.YEAR_MONTH_DAY);
		if(!ObjectUtils.isEmpty(membershipLog.getStartDate())) {
			membershipLog.setStartDate(membershipLog.getStartDate().substring(0, 8) + DateUtils.START_TIME);
		}
		if(!ObjectUtils.isEmpty(membershipLog.getEndDate())) {
			membershipLog.setEndDate(membershipLog.getEndDate().substring(0, 8) + DateUtils.END_TIME);
		}

		long totalCount = membershipService.getMembershipLogTotalCount(membershipLog);
		Pagination pagination = new Pagination(request.getRequestURI(),
				getSearchParameters(PageType.LIST, membershipLog),
				totalCount,
				Long.parseLong(pageNo),
				membershipLog.getListCounter());

		membershipLog.setOffset(pagination.getOffset());
		membershipLog.setLimit(pagination.getPageRows());

		List<MembershipLog> membershipLogList = new ArrayList<>();
		if(totalCount > 0L) {
			membershipLogList = membershipService.getListMembershipLog(membershipLog);
		}

		model.addAttribute(pagination);
		model.addAttribute("membershipLogList", membershipLogList);

		return "/membership/log-list";
	}

	/**
	 * 검색 조건(멤버십 사용량)
	 * @param membershipUsage
	 * @return
	 */
	private String getSearchParameters(PageType pageType, MembershipUsage membershipUsage) {
		StringBuffer buffer = new StringBuffer(membershipUsage.getParameters());
		boolean isListPage = true;
		if(pageType == PageType.MODIFY || pageType == PageType.DETAIL) {
			isListPage = false;
		}

		return buffer.toString();
	}

	/**
	 * 검색 조건(멤버십 로그)
	 * @param membershipLog
	 * @return
	 */
	private String getSearchParameters(PageType pageType, MembershipLog membershipLog) {
		StringBuffer buffer = new StringBuffer(membershipLog.getParameters());
		boolean isListPage = true;
		if(pageType == PageType.MODIFY || pageType == PageType.DETAIL) {
			isListPage = false;
		}

		return buffer.toString();
	}

	private String roleValidate(HttpServletRequest request) {
		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		int httpStatusCode = getRoleStatusCode(userSession.getUserGroupId(), RoleKey.ADMIN_USER_MANAGE.name());
		if(httpStatusCode > 200) {
			log.info("@@ httpStatusCode = {}", httpStatusCode);
			request.setAttribute("httpStatusCode", httpStatusCode);
			return "/error/error";
		}

		return null;
	}

}

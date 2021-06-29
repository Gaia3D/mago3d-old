package gaia3d.controller.view;

import gaia3d.domain.common.Pagination;
import gaia3d.domain.microservice.MicroServiceLog;
import gaia3d.service.MicroServiceService;
import gaia3d.support.SQLInjectSupport;
import gaia3d.utils.DateUtils;
import gaia3d.utils.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/micro-service-log")
@Controller
public class MicroServiceLogController {

	@Autowired
	private MicroServiceService microServiceService;

	/**
	 * MicroService Log 목록
	 */
	@GetMapping(value = "/list")
	public String list(HttpServletRequest request, @RequestParam(defaultValue="1") String pageNo, @ModelAttribute MicroServiceLog microServiceLog, Model model) {

		microServiceLog.setSearchWord(SQLInjectSupport.replaceSqlInection(microServiceLog.getSearchWord()));
		microServiceLog.setOrderWord(SQLInjectSupport.replaceSqlInection(microServiceLog.getOrderWord()));

		String today = DateUtils.getToday(FormatUtils.YEAR_MONTH_DAY);
		if(StringUtils.isEmpty(microServiceLog.getStartDate())) {
			microServiceLog.setStartDate(today.substring(0,4) + DateUtils.START_DAY_TIME);
		} else {
			microServiceLog.setStartDate(microServiceLog.getStartDate().substring(0, 8) + DateUtils.START_TIME);
		}
		if(StringUtils.isEmpty(microServiceLog.getEndDate())) {
			microServiceLog.setEndDate(today + DateUtils.END_TIME);
		} else {
			microServiceLog.setEndDate(microServiceLog.getEndDate().substring(0, 8) + DateUtils.END_TIME);
		}

		Long totalCount = microServiceService.getMicroServiceLogTotalCount(microServiceLog);
		Pagination pagination = new Pagination(request.getRequestURI(), microServiceLog.getSearchParameters(), totalCount, Long.parseLong(pageNo), microServiceLog.getListCounter());
		log.info("@@ pagination = {}", pagination);

		microServiceLog.setOffset(pagination.getOffset());
		microServiceLog.setLimit(pagination.getPageRows());
		List<MicroServiceLog> microServiceLogList = new ArrayList<>();
		if(totalCount > 0l) {
			microServiceLogList = microServiceService.getListMicroServiceLog(microServiceLog);
		}
		log.info("@@ microServiceLog = {}", microServiceLog);
		model.addAttribute(pagination);
		model.addAttribute("microServiceLog", microServiceLog);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("microServiceLogList", microServiceLogList);

		return "/micro-service-log/list";
	}
}

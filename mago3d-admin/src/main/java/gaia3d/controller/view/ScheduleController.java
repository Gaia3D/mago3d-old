package gaia3d.controller.view;

import gaia3d.domain.PageType;
import gaia3d.domain.quartz.ScheduleInfo;
import gaia3d.service.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 스케쥴 관리
 */
@Slf4j
@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	@Autowired
	private QuartzService quartzService;

	/**
	 * 스케쥴 목록
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/list")
	public String list(Model model) {
		List<ScheduleInfo> scheduleInfoList = quartzService.getListScheduleInfo();
		model.addAttribute("scheduleInfoList", scheduleInfoList);

		return "/schedule/list";
	}

	/**
	 * 스케쥴 등록 화면
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/input")
	public String input(Model model) {
		ScheduleInfo scheduleInfo = new ScheduleInfo();
		model.addAttribute("scheduleInfo", scheduleInfo);
		return "/schedule/input";
	}

	/**
	 * 수정 페이지로 이동
	 * @param scheduleInfoId
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/modify")
	public String modify(@RequestParam Integer scheduleInfoId, Model model) {
		ScheduleInfo scheduleInfo = quartzService.getScheduleInfo(scheduleInfoId);

		model.addAttribute(scheduleInfo);

		return "/schedule/modify";
	}

	/**
	 * 검색 조건
	 * @param pageType
	 * @param scheduleInfo
	 * @return
	 */
	private String getSearchParameters(PageType pageType, ScheduleInfo scheduleInfo) {
		return scheduleInfo.getParameters();
	}
}
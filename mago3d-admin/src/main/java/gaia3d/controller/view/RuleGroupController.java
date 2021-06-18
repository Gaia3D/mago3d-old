package gaia3d.controller.view;

import gaia3d.domain.PageType;
import gaia3d.domain.rule.RuleGroup;
import gaia3d.service.DataLibraryGroupService;
import gaia3d.service.RuleGroupService;
import gaia3d.support.SQLInjectSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/rule-group")
public class RuleGroupController {

	@Autowired
	private DataLibraryGroupService dataLibraryGroupService;
	@Autowired
	private RuleGroupService ruleGroupService;

	/**
	 * 룰 그룹 목록
	 * @param request
	 * @param ruleGroup
	 * @param pageNo
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/list")
	public String list(HttpServletRequest request, @RequestParam(defaultValue="1") String pageNo, RuleGroup ruleGroup, Model model) {
		ruleGroup.setSearchWord(SQLInjectSupport.replaceSqlInection(ruleGroup.getSearchWord()));
		ruleGroup.setOrderWord(SQLInjectSupport.replaceSqlInection(ruleGroup.getOrderWord()));

		List<RuleGroup> ruleGroupList = ruleGroupService.getListRuleGroup(ruleGroup);

		model.addAttribute("errorCode", request.getParameter("errorCode"));
		model.addAttribute("ruleGroupList", ruleGroupList);
		return "/rule-group/list";
	}

	/**
	 * 룰 그룹 등록 페이지 이동
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/input")
	public String input(HttpServletRequest request, Model model) {
		List<RuleGroup> ruleGroupList = ruleGroupService.getListRuleGroup(new RuleGroup());

		model.addAttribute("ruleGroup", new RuleGroup());
		model.addAttribute("ruleGroupList", ruleGroupList);

		return "/rule-group/input";
	}

	/**
	 * 룰 그룹 수정 페이지 이동
	 * @param request
	 * @param ruleGroupId
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/modify")
	public String modify(HttpServletRequest request, @RequestParam("ruleGroupId") Integer ruleGroupId, Model model) {
		RuleGroup ruleGroup = ruleGroupService.getRuleGroup(RuleGroup.builder().ruleGroupId(ruleGroupId).build());

		List<RuleGroup> ruleGroupList = ruleGroupService.getListRuleGroup(ruleGroup);

		model.addAttribute("ruleGroup", ruleGroup);
		model.addAttribute("ruleGroupList", ruleGroupList);

		return "/rule-group/modify";
	}

	/**
	 * 룰 그룹 삭제
	 * @param ruleGroupId
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/delete")
	public String delete(@RequestParam("ruleGroupId") Integer ruleGroupId, Model model) {
		// TODO validation 체크 해야 함
		if(ruleGroupId == null) {
			log.info("@@@ validation error ruleGroupId = {}", ruleGroupId);
			return "redirect:/rule-group/list";
		}

		RuleGroup ruleGroup = new RuleGroup();
		ruleGroup.setRuleGroupId(ruleGroupId);
		ruleGroupService.deleteRuleGroup(ruleGroup);

		return "redirect:/rule-group/list";
	}

	/**
	 * 검색 조건
	 * @param ruleGroup
	 * @return
	 */
	private String getSearchParameters(PageType pageType, RuleGroup ruleGroup) {
		StringBuffer buffer = new StringBuffer(ruleGroup.getParameters());
		boolean isListPage = true;
		if(pageType == PageType.MODIFY || pageType == PageType.DETAIL) {
			isListPage = false;
		}

//		if(!isListPage) {
//			buffer.append("pageNo=" + request.getParameter("pageNo"));
//			buffer.append("&");
//			buffer.append("list_count=" + uploadData.getList_counter());
//		}

		return buffer.toString();
	}
}

package gaia3d.controller.view;

import gaia3d.domain.terrain.Terrain;
import gaia3d.service.TerrainService;
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
@RequestMapping("/terrain")
public class TerrainController {

	@Autowired
	private TerrainService terrainService;

	/**
	 * Terrain 목록
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/list")
	public String list(HttpServletRequest request, Model model) {
		List<Terrain> terrainList = terrainService.getListTerrain();

		model.addAttribute("terrainList", terrainList);

		return "/terrain/list";
	}

	/**
	 * Terrain 등록 화면
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/input")
	public String input(Model model) {
		model.addAttribute("terrain", new Terrain());
		return "/terrain/input";
	}

	/**
	 * Terrain 수정 화면
	 * @param reuqet
	 * @param terrainId
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/modify")
	public String modify(HttpServletRequest reuqet, @RequestParam Integer terrainId,  Model model) {
		Terrain terrain = terrainService.getTerrain(terrainId);

		model.addAttribute(terrain);

		return "/terrain/modify";
	}

	/**
	 * Terrain 삭제
	 * @param terrainId
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/delete")
	public String delete(@RequestParam("terrainId") Integer terrainId, Model model) {
		terrainService.deleteTerrain(terrainId);

		return "redirect:/terrain/list";
	}
}

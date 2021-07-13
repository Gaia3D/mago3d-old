package gaia3d.controller.rest;

import gaia3d.domain.Key;
import gaia3d.domain.layer.Layer;
import gaia3d.domain.layer.LayerGroup;
import gaia3d.domain.layer.LayerInsertType;
import gaia3d.domain.user.UserSession;
import gaia3d.service.LayerGroupService;
import gaia3d.service.LayerService;
import gaia3d.service.UserPolicyService;
import gaia3d.support.LayerDisplaySupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/layers")
@Slf4j
public class LayerRestController {

	private final LayerService layerService;
	private final LayerGroupService layerGroupService;
	private final UserPolicyService userPolicyService;
	
	public LayerRestController(LayerService layerService, LayerGroupService layerGroupService, UserPolicyService userPolicyService) {
		this.layerService = layerService;
		this.layerGroupService = layerGroupService;
		this.userPolicyService = userPolicyService;
	}

	/**
	 * 레이어 그룹 목록
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping
	public Map<String, Object> list(HttpServletRequest request, Model model) {
		UserSession userSession = (UserSession)request.getSession().getAttribute(Key.USER_SESSION.name());
		String userId = userSession != null ? userId = userSession.getUserId() : null;
		String baseLayers = userPolicyService.getUserPolicy(userId).getBaseLayers();
		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;
		
		List<LayerGroup> layerGroupList = layerGroupService.getListLayerGroupAndLayer(new LayerGroup());
		int statusCode = HttpStatus.OK.value();
		
		result.put("layerGroupList", LayerDisplaySupport.getListDisplayLayer(layerGroupList, baseLayers));
		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);
		
		return result;
	}

	/**
	 * 레이어의 BoundingBox를 구함
	 */
	@GetMapping(value = "/{id}/envelope", produces = "application/json; charset=UTF-8")
	public ResponseEntity<?> envelope(@PathVariable("id") Integer id) {

		Layer layer = layerService.getLayer(id);
		if (layer == null) {
			log.info("@@ envelope. layer not found with id = {}", id);
			return ResponseEntity.notFound().build();
		}

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		LayerInsertType layerInsertType = LayerInsertType.valueOf(layer.getLayerInsertType().toUpperCase());
		log.info("insert type : {}", layerInsertType);

		String bboxWkt = "";
		if (LayerInsertType.UPLOAD == layerInsertType) {
			bboxWkt = layerService.getEnvelope(layer.getLayerKey());
		} else {
			bboxWkt = "POLYGON((126.70695082879526 37.3552906189018," +
					"124.609708885853 38.6151323380178," +
					"126.73972933011044 37.381863217601364," +
					"131.872766214216 33.1137120723124," +
					"124.609708885853 33.1137120723124))";
		}

		bboxWkt = bboxWkt.replace("POLYGON((", "");
		bboxWkt = bboxWkt.replace("))", "");

		String[] points = bboxWkt.split(",");
		String[] minPoint = new String[2], maxPoint = new String[2];

		minPoint[0] = points[0].split(" ")[0];
		minPoint[1] = points[0].split(" ")[1];

		maxPoint[0] = points[2].split(" ")[0];
		maxPoint[1] = points[2].split(" ")[1];

		result.put("minPoint", minPoint);
		result.put("maxPoint", maxPoint);

		int statusCode = HttpStatus.OK.value();

		result.put("statusCode", statusCode);
		result.put("errorCode", errorCode);
		result.put("message", message);

		return ResponseEntity.ok(result);
	}

}

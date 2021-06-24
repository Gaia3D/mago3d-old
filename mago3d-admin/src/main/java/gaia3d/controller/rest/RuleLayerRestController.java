package gaia3d.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaia3d.domain.Key;
import gaia3d.domain.rule.RuleGroup;
import gaia3d.domain.user.UserSession;
import gaia3d.rule.layer.LayerAttribute;
import gaia3d.rule.layer.LayerAttributeType;
import gaia3d.rule.layer.LayerUsage;
import gaia3d.service.RuleGroupService;
import gaia3d.service.RuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 레이어 룰 관리
 */
@Slf4j
@RestController
@RequestMapping(value = "/rule/layers")
public class RuleLayerRestController extends RuleBaseRestController {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private RuleGroupService ruleGroupService;

	/**
	 * 룰 속성 등록
	 * @param request
	 * @param layerAttribute
	 * @return
	 */
	@PostMapping(value = "/attributes")
	public Map<String, Object> attributesInsert(HttpServletRequest request, LayerAttribute layerAttribute) {
		log.info("@@@@@ before layerAttribute = {}", layerAttribute);

		// 레이어 용도가 배경일때, 속성 초기화
		if(LayerUsage.BACKGROUND == LayerUsage.valueOf(layerAttribute.getUsage().toUpperCase())) {
			layerAttribute.setApplyEvent(null);
			layerAttribute.setAttributeType(null);
			layerAttribute.setCsvMappingColumnForShape(null);
			layerAttribute.setAdditionalCsvColumns(null);
		}
		// 레이어 속성 파일 유형이 Shape 일때 초기화
		if(LayerAttributeType.SHAPE == LayerAttributeType.valueOf(layerAttribute.getAttributeType().toUpperCase())) {
			layerAttribute.setCsvMappingColumnForShape(null);
			layerAttribute.setAdditionalCsvColumns(null);
		}

		log.info("@@@@@ after layerAttribute = {}", layerAttribute);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		String userId = ((UserSession)request.getSession().getAttribute(Key.USER_SESSION.name())).getUserId();

		RuleGroup ruleGroup = getRuleGroup(ruleGroupService, layerAttribute.getRuleGroupId());
		layerAttribute.setRuleGroupId(ruleGroup.getRuleGroupId());
		layerAttribute.setRuleGroupKey(ruleGroup.getRuleGroupKey());
		layerAttribute.setRuleGroupName(ruleGroup.getRuleGroupName());

		String attributes = getAttributes(objectMapper, layerAttribute);
		insert(ruleService, userId, layerAttribute, attributes);

		result.put("statusCode", HttpStatus.OK.value());
		result.put("errorCode", null);
		result.put("message", null);

		return result;
	}

	/**
	 * 롤 속성 수정
	 * @param request
	 * @param layerAttribute
	 * @return
	 */
	@PutMapping(value = "/attributes/{ruleId}")
	public Map<String, Object> attributesUpdate(HttpServletRequest request, @PathVariable Integer ruleId, LayerAttribute layerAttribute) {
		log.info("@@@@@ before layerAttribute = {}", layerAttribute);

		// 레이어 용도가 배경일때, 속성 초기화
		if(LayerUsage.BACKGROUND == LayerUsage.valueOf(layerAttribute.getUsage().toUpperCase())) {
			layerAttribute.setApplyEvent(null);
			layerAttribute.setAttributeType(null);
			layerAttribute.setCsvMappingColumnForShape(null);
			layerAttribute.setAdditionalCsvColumns(null);
		}
		// 레이어 속성 파일 유형이 Shape 일때 초기화
		if(layerAttribute.getAttributeType() == null || LayerAttributeType.SHAPE == LayerAttributeType.valueOf(layerAttribute.getAttributeType().toUpperCase())) {
			layerAttribute.setCsvMappingColumnForShape(null);
			layerAttribute.setAdditionalCsvColumns(null);
		}

		log.info("@@@@@ after layerAttribute = {}", layerAttribute);

		Map<String, Object> result = new HashMap<>();
		String errorCode = null;
		String message = null;

		String userId = ((UserSession)request.getSession().getAttribute(Key.USER_SESSION.name())).getUserId();
		RuleGroup ruleGroup = getRuleGroup(ruleGroupService, layerAttribute.getRuleGroupId());
		layerAttribute.setRuleGroupId(ruleGroup.getRuleGroupId());
		layerAttribute.setRuleGroupKey(ruleGroup.getRuleGroupKey());
		layerAttribute.setRuleGroupName(ruleGroup.getRuleGroupName());

		String attributes = getAttributes(objectMapper, layerAttribute);
		update(ruleService, ruleId, layerAttribute.getRuleName(), layerAttribute.getAvailable(), attributes, layerAttribute.getDescription());

		result.put("statusCode", HttpStatus.OK.value());
		result.put("errorCode", null);
		result.put("message", null);

		return result;
	}
}

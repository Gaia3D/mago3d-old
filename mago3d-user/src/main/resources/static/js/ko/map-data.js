var pageNo = 1;

$(document).ready(function() {
	
	//$(".ui-slider-handle").slider({});
	
	//데이터 목록 초기화
	//mapDataInfoList(1, null);
	
	// 데이터 검색 버튼 클릭
	$("#mapDataSearch").click(function() {
		mapDataInfoList(1, $("#searchDataName").val(), $("#searchDataGroup").val(), $("#searchDataType").val());
	});
	
	// 데이터 그룹 검색 엔터키
	$("#mapDataSearch").keyup(function(e) {
		if(e.keyCode == 13) mapDataInfoList(1, $("#searchDataName").val(), $("#searchDataGroup").val(), $("#searchDataType").val());
	});
	
	// 데이터 목록 tab 클릭
	$('#dataListTab').click(function() {
		var pageNo = $('input[name="pageNo"]').val();
		mapDataInfoList(pageNo, $("#searchDataName").val(), $("#searchDataGroup").val(), $("#searchDataType").val());
	});
	
	//데이터 3D Instance show/hide
	$('#dataListDHTML').on('click', '.showHideButton', function() {
		var dataGroupId = $(this).data('group-id');
		var dataKey = $(this).data('key');
		var dataTiling = $(this).data('tiling');
		
		if(dataGroupId === null || dataGroupId === '' || dataKey === null || dataKey === '') {
			alert(JS_MESSAGE["data.info.incorrect"]);
			return;
		}
		
		var option = true;
		if ($(this).hasClass("show")) {
			option = false;
		}
		
		if (dataTiling) {
			dataKey = "F4D_" + dataKey;
		}
		//setNodeAttributeAPI(MAGO3D_INSTANCE, dataGroupId, dataKey, optionObject);
		
		dataGroupId = parseInt(dataGroupId);
		//var nodeMap = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.getNodesMap(dataGroupId);
		
		//isExistDataAPI(MAGO3D_INSTANCE, dataGroupId, dataKey);
		//isDataReadyToRender(MAGO3D_INSTANCE, dataGroupId, dataKey);
		
		if (!isExistDataAPI(MAGO3D_INSTANCE, dataGroupId, dataKey)) {
			alert(JS_MESSAGE["data.not.loaded"]);
			return;
		}

		var optionObject = { isVisible : option };
		setNodeAttributeAPI(MAGO3D_INSTANCE, dataGroupId, dataKey, optionObject);
		
		if ($(this).hasClass("show")) {
			$(this).removeClass("show");
			$(this).addClass("hide");
		} else {
			$(this).removeClass("hide");
			$(this).addClass("show");
		}
		
	});
	
});

//데이터 검색 페이징에서 호출됨
function pagingDataInfoList(pageNo, searchParameters) {
	// searchParameters-----> &searchWord=data_name&searchOption=1&searchValue=&startDate=&endDate=&orderWord=&orderValue=&listCounter=10&dataType=
	var dataName = null;
	var status = null;
	var dataType = null;
	var dataGroupId = null;
	var parameters = searchParameters.split("&");
	for(var i=0; i<parameters.length; i++) {
		var tempParams = parameters[i];
		if(tempParams != null && tempParams.indexOf("=") >= 0) {
			var tempValue = parameters[i].split("=");
			if(tempValue[0] === "searchValue") {
				dataName = tempValue[1];
			} else if(tempValue[0] === "dataType") {
				dataType = tempValue[1];
			} else if(tempValue[0] === "dataGroupId") {
				dataGroupId = tempValue[1];
			}
		}
	}
	mapDataInfoList(pageNo, dataName, dataGroupId, dataType);
}

//데이터 검색
var dataSearchFlag = true;
function mapDataInfoList(pageNo, searchDataName, searchDataGroupId, searchDataType) {
	// searchOption : 1 like

	//searchDataName
	if(dataSearchFlag) {
		dataSearchFlag = false;
		//var formData =$("#searchDataForm").serialize();

		$.ajax({
			url: "/datas",
			type: "GET",
			data: { pageNo : pageNo, searchWord : "data_name", searchValue : searchDataName, searchOption : "1", dataGroupId : searchDataGroupId, dataType : searchDataType},
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(msg){
				if(msg.statusCode <= 200) {
					
					var dataList = msg.dataList;
					var projectsMap = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.projectsMap;
					
					if (dataList.length > 0) {
						for (i in dataList) {
							var data = dataList[i];
							var dataId = parseInt(data.dataGroupId);
							var isVisible = true;
							if (!$.isEmptyObject(projectsMap)) {
								var projects = projectsMap[dataId];
								if ($.isEmptyObject(projects)) {
									data.groupVisible = isVisible;
									continue;
								}
								if(!projects.attributes) {
									projects.attributes = {};
									projects.attributes.objectType = "basicF4d";
									if(data.tiling) {
										projects.attributes.fromSmartTile = true;
									}
								}
								 
								var groupVisible = projects.attributes.isVisible;
								if (groupVisible !== undefined) {
									isVisible = groupVisible;
								}
							}
							data.groupVisible = isVisible;
						}
					}
					pageNo = msg.pagination.pageNo;
					
	                //핸들바 템플릿 컴파일
	                var template = Handlebars.compile($("#dataListSource").html());
	               	//핸들바 템플릿에 데이터를 바인딩해서 HTML 생성
	                $("#dataListDHTML").html("").append(template(msg));
				} else {
					alert(JS_MESSAGE[msg.errorCode]);
				}
				dataSearchFlag = true;
			},
			error:function(request,status,error){
				alert(JS_MESSAGE["ajax.error.message"]);
				dataSearchFlag = true;
			}
		});
	} else {
		alert(JS_MESSAGE["button.double.click"]);
	}
}


// smart tiling data flyTo
function gotoFly(longitude, latitude, altitude) {
	if(longitude === null || longitude === '' || latitude === null || latitude === '' || altitude === null || altitude === '') {
		alert(JS_MESSAGE["location.invalid"]);
		return;
	}

	gotoFlyAPI(MAGO3D_INSTANCE, longitude, latitude, 500, 3);
	hereIamMarker(longitude, latitude, altitude);
}

function flyTo(dataGroupId, dataKey) {
	if(dataGroupId === null || dataGroupId === '' || dataKey === null || dataKey === '') {
		alert(JS_MESSAGE["location.invalid"]);
		return;
	}

	//  searchDataAPI
	searchDataAPI(MAGO3D_INSTANCE, dataGroupId, dataKey);

	var node = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.getNodeByDataKey(dataGroupId, dataKey);
	var geographic = node.data.bbox.geographicCoord;
	hereIamMarker(geographic.longitude, geographic.latitude, geographic.altitude);
}

var hereIamTimeOut;
function hereIamMarker(longitude, latitude, altitude) {
	var magoManager = MAGO3D_INSTANCE.getMagoManager();
	if(!magoManager.speechBubble) {
		magoManager.speechBubble = new Mago3D.SpeechBubble();
	}
	var sb = magoManager.speechBubble;
	removeHearIam();
	if(hereIamTimeOut) {
		clearTimeout(hereIamTimeOut);
	}
	var commentTextOption = {
		pixel:12,
		color:'black',
		borderColor:'white',
		text: JS_MESSAGE["here.it.is"]
	}

	var img = sb.getPng([80,32],'#94D8F6', commentTextOption);

	var options = {
		positionWC    : Mago3D.ManagerUtils.geographicCoordToWorldPoint(longitude, latitude, parseFloat(altitude)+5),
		imageFilePath : img
	};

	var omId = new Date().getTime();
	var om = magoManager.objMarkerManager.newObjectMarker(options, magoManager);
	om.id = omId;
	om.hereIam = true;

	var effectOption = {
		effectType : "zMovement",
		durationSeconds : 9.9,
		zVelocity : 100,
		zMax : 30,
		zMin : 0
	};
	var effect = new Mago3D.Effect(effectOption);
	magoManager.effectsManager.addEffect(omId, effect);

	hereIamTimeOut = setTimeout(function() {
		removeHearIam();
	},10000);

	function removeHearIam() {
		magoManager.objMarkerManager.setMarkerByCondition(function(om){
			return !om.hereIam;
		});
	}
}

// 데이터 정보 다이얼 로그
var dataInfoDialog = $( "#dataInfoDialogDHTML" ).dialog({
	autoOpen: false,
	width: 500,
	height: 720,
	modal: true,
	overflow : "auto",
	resizable: false
});

//데이터 상세 정보 조회
function detailDataInfo(url) {
	dataInfoDialog.dialog( "open" );
	$.ajax({
		url: url,
		type: "GET",
		headers: {"X-Requested-With": "XMLHttpRequest"},
		dataType: "json",
		success: function(msg){
			if(msg.statusCode <= 200) {
				dataInfoDialog.dialog( "option", "title", msg.dataInfo.dataName + JS_MESSAGE["more.information"]);

				var template = Handlebars.compile($("#dataInfoSource").html());
				var dataInfoHtml = template(msg.dataInfo);

				$("#dataInfoDialogDHTML").html("").append(dataInfoHtml);
			} else {
				alert(JS_MESSAGE[msg.errorCode]);
			}
		},
		error:function(request,status,error){
			alert(JS_MESSAGE["ajax.error.message"]);
		}
	});
}

// 데이터 속성 다이얼 로그
var dataAttributeDialog = $( "#dataAttributeDialog" ).dialog({
	autoOpen: false,
	width: 600,
	height: 350,
	modal: true,
	resizable: false
});
// 부산 데이터 속성용 다이얼 로그
var sampleDataAttributeDialog = $( "#sampleDataAttributeDialogDHTML" ).dialog({
	autoOpen: false,
	width: 500,
	height: 600,
	modal: true,
	resizable: false
});

// 데이터 속성
function detailDataAttribute(dataId, dataGroupKey, dataKey, dataName) {
	if(	dataGroupKey !== "busan-mj" ) {
		dataAttributeDialog.dialog( "open" );
		$("#dataNameForAttribute").html(dataName);
		$.ajax({
			url: "/datas/attributes/" + dataId,
			type: "GET",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			dataType: "json",
			success: function(msg){
				if(msg.statusCode <= 200) {
					if(msg.dataAttribute !== null) {
						//$("#dataAttributeForOrigin").html(msg.dataAttribute.attributes);
						$("#dataAttributeViewer").html("");
						var jsonViewer = new JSONViewer();
						document.querySelector("#dataAttributeViewer").appendChild(jsonViewer.getContainer());
						jsonViewer.showJSON(JSON.parse(msg.dataAttribute.attributes), -1, -1);
					}
				} else {
					alert(JS_MESSAGE[msg.errorCode]);
				}
			},
			error:function(request,status,error){
				alert(JS_MESSAGE["ajax.error.message"]);
			}
		});
	} else {
		sampleDataAttributeDialog.dialog( "open" );
		$.ajax({
			url: "/attribute-repository/" + dataKey,
			type: "GET",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			dataType: "json",
			success: function(msg){
				if(msg.statusCode <= 200) {
					if(msg.attributeRepository !== null) {
						var template = Handlebars.compile($("#sampleDataAttributeSource").html());
						var html = template(msg.attributeRepository);
						$("#sampleDataAttributeDialogDHTML").html("").append(html);
					}
				} else {
					alert(JS_MESSAGE[msg.errorCode]);
				}
			},
			error:function(request,status,error){
				alert(JS_MESSAGE["ajax.error.message"]);
			}
		});
	}
}

// 데이터 Object 속성 다이얼 로그
var dataObjectAttributeDialog = $( "#dataObjectAttributeDialog" ).dialog({
	autoOpen: false,
	width: 800,
	height: 550,
	modal: true,
	resizable: false
});

// 데이터 Object 속성
function detailDataObjectAttribute(dataId, dataName) {
	dataObjectAttributeDialog.dialog( "open" );
	$("#dataNameForObjectAttribute").html(dataName);

	$.ajax({
		url: "/datas/object/attributes/" + dataId,
		type: "GET",
		headers: {"X-Requested-With": "XMLHttpRequest"},
		dataType: "json",
		success: function(msg){
			if(msg.statusCode <= 200) {
				if(msg.dataObjectAttribute !== null) {
					//$("#dataObjectAttributeForOrigin").html(msg.dataObjectAttribute.attributes);
					$("#dataObjectAttributeViewer").html("");
					var jsonViewer = new JSONViewer();
					document.querySelector("#dataObjectAttributeViewer").appendChild(jsonViewer.getContainer());
					jsonViewer.showJSON(JSON.parse(msg.dataObjectAttribute.attributes), -1, -1);
				}
			} else {
				alert(JS_MESSAGE[msg.errorCode]);
			}
		},
		error:function(request,status,error){
			alert(JS_MESSAGE["ajax.error.message"]);
		}
	});
}

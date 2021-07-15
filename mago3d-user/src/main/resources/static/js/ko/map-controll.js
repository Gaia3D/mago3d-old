var lengthInMeters = 0;
var areaInMeters = 0;

var formatDistance = function (_length) {
	var unitFactor = 1;//parseFloat($('#distanceFactor option:selected').val());
	var unitName = 'm (미터)';//$('#distanceFactor option:selected').text();
	var output= Math.round(_length / unitFactor * 100) / 100 + " " + unitName.substring(0, unitName.indexOf('('));
	return output;
};

var formatArea = function (_area) {
	var unitFactor = 1;//parseFloat($('#areaFactor option:selected').val());
	var unitName = 'm² (제곱미터)' //$('#areaFactor option:selected').text();
		var output= Math.round(_area / unitFactor * 100) / 100 + " " + unitName.substring(0, unitName.indexOf('('));
	return output;
};

$("#mapPolicy").click(function(){
	$("#mapPolicy").toggleClass("on");
	$("#mago3DSettingLabelLayer").toggle();
});
$(".layerClose").click(function(){
	$("#mapPolicy").removeClass("on");
	$("#mago3DSettingLabelLayer").hide();
});

// 가이드 팝업 띄우기
function goMagoAPIGuide(url) {
	var width = 1200;
	var height = 800;

	// 만들 팝업창 좌우 크기의 1/2 만큼 보정값으로 빼주었음
	var popupX = (window.screen.width / 2) - (width / 2);
	var popupY = (window.screen.height / 2) - (height / 2);
	
	var popWin = window.open(url, "", "toolbar=no, width=" + width + " ,height=" + height + ", top=" + popupY + ", left=" + popupX + 
			", directories=no,status=yes,scrollbars=no,menubar=no,location=no");
	return false;
}

var MapControll = {
	divided : false,
	divideMap : function(loadEnd) {
		$('.mapWrap2').show();
		
		var display = $('#contentsWrap').is(':visible');
		var width;
		var divideWidth;
		if(!display) {
			width = 'calc(50% - 38.5px)';
			divideWidth = 'calc(50% - 41.5px)';
		} else {
			width = 'calc(50% - 215px)';
			divideWidth = 'calc(50% - 218px)';
		}
		
		$('.mapWrap').css({
			width : width
		});
		$('.mapWrap2').css({
			width : divideWidth
		});
		
		$('#magoContainer .totalSearch').hide();
		
		var clonePolicy = cloneObject(MAGO.policy);//common.js
		
		var option = {};
		option.defaultControl = {};
		option.defaultControl.zoom = false;
		option.defaultControl.initCamera = false;
		option.defaultControl.fullScreen = false;
		option.defaultControl.measure = false;
		option.defaultControl.tools = false;
		option.defaultControl.attribution = false;
		option.defaultControl.overviewMap = false;
		
		
		var curresntPosition = MAGO3D_INSTANCE.getMagoManager().sceneState.camera.position;
		var currentGeoCoord = Mago3D.ManagerUtils.pointToGeographicCoord(curresntPosition);
		
		clonePolicy.initLatitude = currentGeoCoord.latitude;
		clonePolicy.initLongitude = currentGeoCoord.longitude;
		clonePolicy.initDuration = 0;
		clonePolicy.initAltitude = currentGeoCoord.altitude;
		
		if(!MAGO3D_DIVIDE_INSTANCE) {
			MAGO3D_DIVIDE_INSTANCE = new Mago3D.Mago3d('magoDivideContainer', clonePolicy, {loadend : divideLoadEnd}, option);
		}

		this.divided = true;
		
		function divideLoadEnd(e) {
			var originViewer = MAGO3D_INSTANCE.getViewer();
			var viewer = e.getViewer();
			viewer.scene.camera = originViewer.scene.camera;
			
			if(loadEnd && typeof loadEnd === 'function') loadEnd(e);
		}
	},
	undivideMap : function() {
		if(!MAGO3D_DIVIDE_INSTANCE) return;
		
		var mm = MAGO3D_DIVIDE_INSTANCE.getMagoManager();
		var vv = MAGO3D_DIVIDE_INSTANCE.getViewer();
		
		mm.deleteAll();
		vv.destroy();
		MAGO3D_DIVIDE_INSTANCE = null;
		
		$('.mapWrap2').hide();
		$('#magoContainer .totalSearch').show();
		
		var width;
		var display = $('#contentsWrap').is(':visible');
		if(!display) {
			width = 'calc(100% - 77px)';
		} else {
			width = 'calc(100% - 430px)';
		}
		
		$('.mapWrap').css({
			width : width
		});

		this.divided = false;
	}
}

var mapControllEventHandler = function(magoInstance) {
	var magoManager = magoInstance.getMagoManager();
	var configInformation = magoManager.configInformation;
	//var Shape = _Shape(magoInstance);
	
	//MAGO.measureTools.lineShape = new Shape(2, false);
	//MAGO.measureTools.polygonShape = new Shape(3, false);
	
	var shapeInfo = document.getElementById("measureInfo");
	
	$('#geoLod0').val(configInformation.lod0);
	$('#geoLod1').val(configInformation.lod1);
	$('#geoLod2').val(configInformation.lod2);
	$('#geoLod3').val(configInformation.lod3);
	$('#geoLod4').val(configInformation.lod4);
	$('#geoLod5').val(configInformation.lod5);
	
	var translateInteraction = magoManager.defaultTranslateInteraction;
	var selectInteraction = magoManager.defaultSelectInteraction;
	
	selectInteraction.on(Mago3D.PointSelectInteraction.EVENT_TYPE.DEACTIVE, function(){
		$('div.buttonModeWrap button').removeClass('on');
		translateInteraction.setActive(false);
		//MAGO.selectedDataController.toggleWrap(false);
	});
	
	
	// 처음
	$('#mapCtrlReset').click(function() {
		var magoManager = magoInstance.getMagoManager();
		if (magoManager.isCesiumGlobe()) {
			var config = magoManager.configInformation;
			if (config.initCameraEnable) {
				var lon = parseFloat(config.initLongitude);
				var lat = parseFloat(config.initLatitude);
				var height = parseFloat(config.initAltitude);
				var duration = parseInt(config.initDuration);
				if (isNaN(lon) || isNaN(lat) || isNaN(height)) {
					throw new Error('Longitude, Latitude, Height must number type.');
				}
				if (isNaN(duration)) {
					duration = 3;
				}
				magoManager.flyTo(lon, lat, height, duration);
			}
		}
	});

	// 전체화면
	
	let fullScreen = false;
	const fullScreenTarget = document.getElementById(magoInstance.getMagoManager().config.getContainerId());
	
	fullScreenTarget.addEventListener('fullscreenchange', (event) => {
		if (document.fullscreenElement) {
			fullScreen = true;
	  	} else {
			fullScreen = false;
	  	}
	});
	
	$('#mapCtrlAll').click(function() {
		if (fullScreen) {
			if (isFullScreen()) {
				exitFullScreen();
			}
		} else {
			if (isFullScreenSupported()) {
				requestFullScreen(fullScreenTarget);
			}
		}

		function isFullScreenSupported() {
			var body = document.body;
			return !!(
				body.webkitRequestFullscreen ||
				(body.msRequestFullscreen && document.msFullscreenEnabled) ||
				(body.requestFullscreen && document.fullscreenEnabled)
			);
		}

		function isFullScreen() {
			return !!(
				document.webkitIsFullScreen ||
				document.msFullscreenElement ||
				document.fullscreenElement
			);
		}

		function requestFullScreen(element) {
			if (element.requestFullscreen) {
				element.requestFullscreen();
			} else if (element.msRequestFullscreen) {
				element.msRequestFullscreen();
			} else if (element.webkitRequestFullscreen) {
				element.webkitRequestFullscreen();
			}
		}

		function exitFullScreen() {
			if (document.exitFullscreen) {
				document.exitFullscreen();
			} else if (document.msExitFullscreen) {
				document.msExitFullscreen();
			} else if (document.webkitExitFullscreen) {
				document.webkitExitFullscreen();
			}
		}

	});
	
	// 레이어
	$('#map-ctrl-layer').click(function() {
		/*$(this).toggleClass('on');
		$('#mapCtrlMeasure').removeClass('on');
		$('#controlLayerWrap').toggle();
		$('#controlMeasureWrap').hide();*/
		alert('준비중입니다.');
	});

	// 측정
	var measure = new Measure(magoInstance);
	$('#map-ctrl-measure').click(function() {
		$(this).toggleClass('on');
		$('.toolbox-measure').toggle();
		
		$('.toolbox-measure-btn').removeClass('on');
	});

	// 높이 측정
	var heigtEntities = [];
	$('#mapCtrlMeasureHeight').click(function() {
		$(this).siblings().removeClass('on');
		$(this).toggleClass('on');
		
		if(MAGO.measureTools.polygonShape.entityId.length !=0){
			MAGO.measureTools.polygonShape.init();
		}
		
		if(MAGO.measureTools.lineShape.entityId.length !=0){
			MAGO.measureTools.lineShape.init();
		}
		shapeInfo.innerHTML = '';
		shapeInfo.style.display = 'none';

		deleteHeigtEntities();
		measureHeight();
	});
	
	function deleteHeigtEntities() {
		var viewer = magoInstance.getViewer();
		
		for (var index = 0; index < heigtEntities.length; index++) {
			viewer.entities.remove(heigtEntities[index]);
		}
		heigtEntities.length = 0;
	}
	
	//카메라 고정, 임시
	$('#mapCameraFix').click(function() {
		$(this).toggleClass('on');
		var viewer = magoInstance.getViewer();
		if($(this).hasClass('on')) {
			var scene = viewer.scene;
			var globe = scene.globe;
			var camera = scene.camera;
			
			var canvas = scene.canvas;
			var ray = camera.getPickRay(new Cesium.Cartesian2(canvas.offsetHeight/2, canvas.offsetWidth/2));
			var intersection = globe.pick(ray, scene);
			
			var range = Cesium.Cartesian3.distance(camera.positionWC, intersection);
			
			viewer.camera.lookAt(camera.position, new Cesium.HeadingPitchRange(camera.heading, camera.pitch, range));
		} else {
			viewer.camera.lookAtTransform(Cesium.Matrix4.IDENTITY);
		}
	});
	
	// 화면분할
	$('#mapCtrlDivide').click(function() {
		$(this).toggleClass('on');
		
		if($(this).hasClass('on')) {
			MapControll.divideMap();
		} else {
			MapControll.undivideMap();
		}
	});
	
	var handler = new Cesium.ScreenSpaceEventHandler(magoInstance.getViewer().scene.canvas);
	$('#mapRenderPosition').click(function() {
		$(this).toggleClass('on');
		
		if($(this).hasClass('on')) {
			handler.setInputAction(getPosition, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK)
		} else {
			$('#positionBox').hide();
			handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);
		}
	});
	
	$('#positionBox button').click(function(){
		$('#positionBox').hide();
	});
	
	var getPosition = function(e) {
		$('#positionBox').show();
		
		var viewer = magoInstance.getViewer();
		var cesiumScene = viewer.scene; 
		var cesiumGlobe = cesiumScene.globe;
		var cesiumCamera = cesiumScene.camera;
		var windowCoordinates = new Cesium.Cartesian2(e.position.x, e.position.y);
		var ray = cesiumCamera.getPickRay(windowCoordinates);
		var intersection = cesiumGlobe.pick(ray, cesiumScene);
		
		var promise = Cesium.sampleTerrain(viewer.terrainProvider, 17, [Cesium.Cartographic.fromCartesian(intersection)]);
    	promise.then(function(t){
    		var p = t[0];
    		
    		$('#positionBoxLongitude').text(Cesium.Math.toDegrees(p.longitude).toFixed(4) + ' 도');
    		$('#positionBoxLatitude').text(Cesium.Math.toDegrees(p.latitude).toFixed(4) + ' 도');
    		$('#positionBoxHeight').text(p.height.toFixed(4) + ' m');
    	});
	}
	
	var getCameraPosition = function(magoManager) {
		var scene = magoManager.scene;
		var camera = scene.camera;
		return Cesium.Cartographic.fromCartesian(camera.position);
	}

	// 확대
	$('#mapCtrlZoomIn').click(function() {
		var magoManager = magoInstance.getMagoManager();
		if (magoManager.isCesiumGlobe()) {
			var cartographicPosition = getCameraPosition(magoManager);
			scene.camera.zoomIn(cartographicPosition.height * 0.1);

		}
	});

	// 축소
	$('#mapCtrlZoomOut').click(function() {
		var magoManager = magoInstance.getMagoManager();
		if (magoManager.isCesiumGlobe()) {
			var cartographicPosition = getCameraPosition(magoManager);
			scene.camera.zoomOut(cartographicPosition.height * 0.1);
		}
	});

	// 데이터
	$('#map-control-setting').click(function() {
		$('#toolbarWrap div.detaildata.poplayer:not(#map-control-popup)').hide().removeClass('on');
		if($('#map-ctrl-measure').hasClass('on')) $('#map-ctrl-measure').trigger('click');
		
		$('#map-control-popup').slideToggle("slow");
	})
	
	$('#map-control-advance-toggle-btn').click(function() {
		var collapse = JSON.parse($(this).data('collapse'));
		if(collapse) {
			$('#map-control-popup div.setup.advance').show({
				easing : 'swing'
			});
			$(this).removeClass('map-control-advance-collapse-on').addClass('map-control-advance-collapse-off');
		} else {
			$('#map-control-popup div.setup.advance').hide({
				easing : 'swing'
			});
			$(this).addClass('map-control-advance-collapse-on').removeClass('map-control-advance-collapse-off');
		}
		$(this).data('collapse', !collapse);
	});
	
	
	var archInfoController = new ArchInfoController(ArchInfoController.createMockData(magoManager), magoManager);
	// 건축통합
	$('#master-plan-setting').click(function() {
		$('#toolbarWrap div.detaildata.poplayer:not(#master-plan-popup)').hide().removeClass('on');
		if($('#map-ctrl-measure').hasClass('on')) $('#map-ctrl-measure').trigger('click');
		$('#master-plan-popup').slideToggle("slow");
	});
	
	/*archinfo.js로 이전계획 */
	//건축통합 온오프
	$('#master-plan-popup input[type="checkbox"]').change(function() {
		var checked = $(this).prop('checked');
		var id = $(this).parents('tr').data('id');
		var arch = archInfoController.getArchInfoById(id);
		
		arch.show = checked;
	});
	
	//건축통합 칼라피커 이벤트 바인드
	$('#master-plan-popup button.master-plan-color').click(function() {
		var $color = $(this).siblings('input[type="color"]'); 
		$color.trigger('click');
	});
	
	//건축통합 색상변경
	$('#master-plan-popup input[type="color"]').change(function() {
		var id = $(this).parents('tr').data('id');
		var arch = archInfoController.getArchInfoById(id);
		
		arch.color = Mago3D.Color.fromHexCode($(this).val());
	});
	

	// 라이브러리
	$('#data-library-setting').click(function() {
		$('#toolbarWrap div.detaildata.poplayer:not(#data-library-popup)').hide();
		if($('#map-ctrl-measure').hasClass('on')) $('#map-ctrl-measure').trigger('click');
		if(MAGO.dataLibraryController || !MAGO.dataLibraryController.ready) {
			var load = MAGO.dataLibraryController.load();
			if(load) {
				load.pipe(function() {
					$('#data-library-popup').slideToggle("slow").toggleClass('on');					
				})
				return;
			}
		}
		$('#data-library-popup').slideToggle("slow").toggleClass('on');
	});
	
	//3d tool close
	var popLayer3dSelectQuery = 'div.detaildata.poplayer'; 
	$(`${popLayer3dSelectQuery} button.pop-close`).click(function(){$(this).parents(popLayer3dSelectQuery).hide().removeClass('on')});

	// BBOX
	$('#mapSettingBboxToggle').click(function() {
		$(this).toggleClass('on');
		
		var magoManager = magoInstance.getMagoManager();
		magoManager.magoPolicy.setShowBoundingBox($(this).hasClass('on'));
	});

	// LABEL
	$('#mapSettingLabelToggle').click(function() {
		$(this).toggleClass('on');
		var magoManager = magoInstance.getMagoManager();
		magoManager.magoPolicy.setShowLabelInfo($(this).hasClass('on'));
	});

	// ORIGIN
	$('#mapSettingOriginToggle').click(function() {
		$(this).toggleClass('on');
		var magoManager = magoInstance.getMagoManager();
		magoManager.magoPolicy.setShowOrigin($(this).hasClass('on'));
	});

	// SHADOW
	$('#mapSettingShadowToggle').click(function() {
		$(this).toggleClass('on');
		var magoManager = magoInstance.getMagoManager();
		magoManager.sceneState.setApplySunShadows($(this).hasClass('on'));
	});
	
	
	var defaultSelectInteraction = magoInstance.getMagoManager().defaultSelectInteraction;
	var defaultTranslateInteraction = magoInstance.getMagoManager().defaultTranslateInteraction;

	// SELECT, MOVE MODE
	$('#selectModeF4d').click(function() {
		setClassSiblingsInteractionBtn($(this));
		setSelectInteraction($(this).hasClass('on'), 'f4d');
	});
	$('#moveModeF4d').click(function() {
		setClassSiblingsInteractionBtn($(this));
		setSelectInteraction($(this).hasClass('on'), 'f4d');
		setTranslateInteraction($(this).hasClass('on'), 'f4d');
	});

	$('#selectModeObject').click(function() {
		setClassSiblingsInteractionBtn($(this));
		setSelectInteraction($(this).hasClass('on'), 'object');
	});
	$('#moveModeObject').click(function() {
		setClassSiblingsInteractionBtn($(this));
		setSelectInteraction($(this).hasClass('on'), 'object');
		setTranslateInteraction($(this).hasClass('on'), 'object');
	});

	$('#selectModeNative').click(function() {
		setClassSiblingsInteractionBtn($(this));
		setSelectInteraction($(this).hasClass('on'), 'native');
	});
	$('#moveModeNative').click(function() {
		setClassSiblingsInteractionBtn($(this));
		setSelectInteraction($(this).hasClass('on'), 'native');
		setTranslateInteraction($(this).hasClass('on'), 'native');
	});
	
	function setClassSiblingsInteractionBtn($obj) {
		var buttonModeWrap = $obj.parents('div.buttonModeWrap');
		var wrapSiblings = buttonModeWrap.siblings();
		wrapSiblings.each(function(idx, e){
			var buttons = $(this).find('div.rightButtonWrap').children();
			buttons.each(function(){
				$(this).removeClass('on');
			});
		});
		
		$obj.siblings('button').removeClass('on');
		$obj.toggleClass('on');
	}
	
	function setSelectInteraction(active, type) {
		defaultSelectInteraction.setActive(active);
		defaultSelectInteraction.setTargetType(type);
	}
	function setTranslateInteraction(active, type) {
		defaultTranslateInteraction.setActive(active);
		defaultTranslateInteraction.setTargetType(type);
	}
	
	magoInstance.getViewer().clock.onTick.addEventListener(function(clock) {
		if(heigtEntities.length > 0 && heigtEntities.length === 3) {
			var point = heigtEntities[heigtEntities.length-1];
			var position = point.position.getValue();
			
			var position2d = Cesium.SceneTransforms.wgs84ToWindowCoordinates(
							magoInstance.getViewer().scene, position);
				
			if(position2d.x > 0 && position2d.y > 0) {
				shapeInfo.style.display = 'block';

				var left = position2d.x;
				if($('#navWrap').is(':visible')) {
					left += $('#navWrap').width();
				}
				if($('#contentsWrap').is(':visible')) {
					left += $('#contentsWrap').width();
				}
				var top = position2d.y-50;

				shapeInfo.style.left = left - 60 + 'px';
				shapeInfo.style.top = top + 'px';
			} else {
				shapeInfo.style.display = 'none';
			}
		}
	});
	
	var measureHeight = function () {
		var magoManager = MAGO3D_INSTANCE.getMagoManager();
		var handler = new Cesium.ScreenSpaceEventHandler(MAGO3D_INSTANCE.getViewer().scene.canvas);
		var points = [];
		
		var lineEndPoint;
		var line;
		var depthDetected = false;
		//클릭
		handler.setInputAction(function(event){
			depthDetected = Mago3D.ManagerUtils.detectedDepth(event.position.x, event.position.y, magoManager);
			if(points.length === 0) {
				var point3d = Mago3D.ManagerUtils.screenCoordToWorldCoordUseDepthCheck(event.position.x, event.position.y, magoManager); 
				var geographic = Mago3D.ManagerUtils.pointToGeographicCoord(point3d);
				
				let worldPosition = Cesium.Cartesian3.fromDegrees(geographic.longitude, geographic.latitude, geographic.altitude);
				var entity = MAGO3D_INSTANCE.getViewer().entities.add({
			        position: worldPosition,
			        point: {
			        	pixelSize: 10,
						color: Cesium.Color.WHITE,
			            disableDepthTestDistance: Number.POSITIVE_INFINITY
			        }
			    });
				points.push(entity);
				heigtEntities.push(entity);
			} else {
				var height;
				var orgPoint3d = lineEndPoint;
				var orggeographic = Mago3D.ManagerUtils.pointToGeographicCoord(orgPoint3d);
				var entity = MAGO3D_INSTANCE.getViewer().entities.add({
			        position: lineEndPoint,
			        point: {
			            pixelSize: 10,
						color: Cesium.Color.WHITE,
			            disableDepthTestDistance: Number.POSITIVE_INFINITY
			        }
			    });
				points.push(entity);
				heigtEntities.push(entity);
				handler = handler.destroy();
		    	
				$('#mapCtrlMeasureHeight').removeClass('on');
			}
		}, Cesium.ScreenSpaceEventType.LEFT_CLICK);
		
		handler.setInputAction(function(event){
			if(points.length > 0) {
				var orgPoint3d = points[0].position.getValue(new Date());
				var height;
				if(depthDetected) {
					var point3d = Mago3D.ManagerUtils.screenCoordToWorldCoordUseDepthCheck(event.endPosition.x, event.endPosition.y, magoManager);
					var geographic = Mago3D.ManagerUtils.pointToGeographicCoord(point3d);
					
					height = geographic.altitude;
				}
				
				if(!depthDetected || Math.abs(height) < 0.8) {
					var viewer = MAGO3D_INSTANCE.getViewer();
					var scene = viewer.scene;
					
					var orgCartesian = new Cesium.Cartesian3(orgPoint3d.x, orgPoint3d.y, orgPoint3d.z);
					
                    var surfaceNormal = scene.globe.ellipsoid.geodeticSurfaceNormal(orgCartesian);
                    var planeNormal = Cesium.Cartesian3.subtract(scene.camera.position, orgCartesian, new Cesium.Cartesian3());
                    planeNormal = Cesium.Cartesian3.normalize(planeNormal, planeNormal);
                    var ray =  viewer.scene.camera.getPickRay(event.endPosition);
                    var plane = Cesium.Plane.fromPointNormal(orgCartesian, planeNormal);
                    var newCartesian =  Cesium.IntersectionTests.rayPlane(ray, plane);
                    
                    var newCartographic = viewer.scene.globe.ellipsoid.cartesianToCartographic(newCartesian);
                    height = newCartographic.height;
                    if(height < 0) height *= -1;
				}
				
				var orggeographic = Mago3D.ManagerUtils.pointToGeographicCoord(orgPoint3d);
				lineEndPoint = Cesium.Cartesian3.fromDegrees(orggeographic.longitude, orggeographic.latitude, height);

				if(!line) {
					line = MAGO3D_INSTANCE.getViewer().entities.add({
						polyline: {
							// This callback updates positions each frame.
			                positions: new Cesium.CallbackProperty(function() {
								return [points[0].position.getValue(new Date()), lineEndPoint];                    
			                }, false),
			                width: 10,
			                depthFailMaterial : Cesium.Color.RED,
			                material: new Cesium.PolylineGlowMaterialProperty({
								color: Cesium.Color.DEEPSKYBLUE,
								glowPower: 0.25
							})
			            },
					});
					heigtEntities.push(line);
				}
				
				var shapeInfo = document.getElementById("measureInfo");
				if(shapeInfo.style.display === 'none') {
					shapeInfo.style.display = "block";
					shapeInfo.style.background = 'white';
					shapeInfo.style.border = '1px solid #888';
					shapeInfo.style.paddingLeft = '5px';
					shapeInfo.style.position = 'absolute';
					shapeInfo.style.zIndex = '100';
					shapeInfo.style.color = "#00BFFF";
				}
				
				var position2d = Mago3D.ManagerUtils.calculateWorldPositionToScreenCoord(undefined, lineEndPoint.x, lineEndPoint.y, lineEndPoint.z,undefined, MAGO3D_INSTANCE.getMagoManager());
				
				if(position2d.x > 0 && position2d.y > 0) {
					var left = position2d.x;
					if($('#navWrap').is(':visible')) {
						left += $('#navWrap').width();
					}
					if($('#contentsWrap').is(':visible')) {
						left += $('#contentsWrap').width();
					}
					var top = position2d.y-50;

					shapeInfo.style.left = left - 60 + 'px';
					shapeInfo.style.top = top + 'px';
					$('#measureInfo').text("height : " + getHeight(lineEndPoint));
					
					var exitbtn = document.createElement("button");
					exitbtn.id = "exit";
					exitbtn.innerText = "x";
					exitbtn.style.border = "0px";
					exitbtn.style.padding = "0 5px";
					exitbtn.style.backgroundColor = "transparent";
					exitbtn.addEventListener("click", function() {
						shapeInfo.innerHTML = "";
						deleteHeigtEntities();
						handler = handler.destroy();
					});
					shapeInfo.appendChild(exitbtn);
				}
			}
		}, Cesium.ScreenSpaceEventType.MOUSE_MOVE);
		
		function getHeight(last) {
			
			var startgeographic = Mago3D.ManagerUtils.pointToGeographicCoord(points[0].position.getValue(new Date()));
			var endgeographic = Mago3D.ManagerUtils.pointToGeographicCoord(last ? last : points[1].position.getValue(new Date()));
			var height = endgeographic.altitude - startgeographic.altitude;
			return height.toFixed(3) + 'm';
		}
	}
}
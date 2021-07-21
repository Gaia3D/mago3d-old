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
		
		var promise = Cesium.sampleTerrain(viewer.terrainProvider, Mago3D.MagoManager.getMaximumLevelOfTerrainProvider(viewer.terrainProvider), [Cesium.Cartographic.fromCartesian(intersection)]);
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
			//$(this).removeClass('map-control-advance-collapse-on').addClass('map-control-advance-collapse-off');
			$(this).text('접기');
		} else {
			$('#map-control-popup div.setup.advance').hide({
				easing : 'swing'
			});
			//$(this).addClass('map-control-advance-collapse-on').removeClass('map-control-advance-collapse-off');
			$(this).text('더보기');
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
	
	//경관
	$('#map-ctrl-scene').click(function() {
		alert('준비중입니다.');		
	});
	
	var defaultSelectInteraction = magoInstance.getMagoManager().defaultSelectInteraction;
	var defaultTranslateInteraction = magoInstance.getMagoManager().defaultTranslateInteraction;

	// SELECT, MOVE MODE
	var selectedDataController = new SelectedDataController(magoInstance);
}
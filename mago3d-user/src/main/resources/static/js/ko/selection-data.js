var SelectedDataController = function(magoInstance) {
	this.magoInstance = magoInstance;
	this.selected;
	
	this.setEventHandler();
}

//정규화 범위
var minMarginLeft = 0;
var maxMarginLeft = 90;
//실제값 범위
var minRotation = -360;
var maxRotation = 360;

SelectedDataController.prototype.isActive = function() {
	return this.selected ? true : false;
}

SelectedDataController.prototype.setEventHandler = function() {
	var magoManager = this.magoInstance.getMagoManager();
	var translateInteraction = magoManager.defaultTranslateInteraction;
	var selectInteraction = magoManager.defaultSelectInteraction;
	var that = this;
	
	var SELECT_MODE = {
		NONE : 2,
		ALL : 0,
		OBJECT : 1
	}
	$('#map-control-popup input[name="objectMoveMode"]').change(function() {
		var mode = parseInt($(this).val());
		switch(mode) {
			case SELECT_MODE.NONE : {
				setSelectInteraction(false, Mago3D.DataType.ALL);
				setTranslateInteraction(false, Mago3D.DataType.ALL);
				break;
			}
			case SELECT_MODE.ALL : {
				setSelectInteraction(true, Mago3D.DataType.ALL);
				setTranslateInteraction(true, Mago3D.DataType.ALL);
				break;
			}
			case SELECT_MODE.OBJECT : {
				setSelectInteraction(true, Mago3D.DataType.OBJECT);
				setTranslateInteraction(true, Mago3D.DataType.OBJECT);
				break;
			}
		}
	});
	
	function setSelectInteraction(active, type) {
		selectInteraction.setActive(active);
		selectInteraction.setTargetType(type);
	}
	function setTranslateInteraction(active, type) {
		translateInteraction.setActive(active);
		translateInteraction.setTargetType(type);
	}
	
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.SELECTEDF4D, function(result) {
		var f4d = result.selected;
		if(f4d && f4d instanceof Mago3D.Node) {
			that.selectData(f4d);
		}
	});
	
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.SELECTEDF4DOBJECT, function(result) {
		var f4d = result.f4d;
		var f4dObject = result.selected;
		
		if(f4d && f4d instanceof Mago3D.Node 
		&& f4dObject && f4dObject instanceof Mago3D.NeoReference 
		) {
			that.selectData(f4dObject, f4d);
		}
	});
	
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.SELECTEDGENERALOBJECT, function(result) {
		var native = result.selected;
		if(native && native instanceof Mago3D.MagoRenderable) {
			that.selectData(native);
		}
	});
	
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.DESELECTEDF4D, function(result) {
		var f4d = result.deselected
		if(f4d && f4d instanceof Mago3D.Node) {
			that.deselectData();
		}
	});
	
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.DESELECTEDF4DOBJECT, function(result) {
		var f4dObject = result.deselected
		if(f4dObject && f4dObject instanceof Mago3D.NeoReference) {
			that.deselectData();
		}
	});
	
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.DESELECTEDGENERALOBJECT, function(result) {
		var native = result.deselected
		if(native && native instanceof Mago3D.MagoRenderable) {
			that.deselectData();
		}
	});
	
	//높이 보정결과 UI 적용 
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.VALIDHEIGHTEND, function(result) {
		var arr = result.validDataArray;
		var len = arr.length;
		for(var i=0;i<len;i++) {
			if(arr[i] === that.selected) {
    			var heightReference = that.getHeightReferenceFromData();
    			that.changeHeightInfo(heightReference);
				break;
			}
		}
	});
	
	selectInteraction.on(Mago3D.PointSelectInteraction.EVENT_TYPE.DEACTIVE, function(){
        that.toggleWrap(false);
    });
	
	//선택된 데이터 이동 시 결과 리턴
	translateInteraction.on(Mago3D.TranslateInteraction.EVENT_TYPE.MOVING_F4D, function(moved) {
		moving(moved.result);
	});
	translateInteraction.on(Mago3D.TranslateInteraction.EVENT_TYPE.MOVING_NATIVE, function(moved) {
		moving(moved.result);
	});
	
	function moving(target) {
    	var movedGeolocationData = target.getCurrentGeoLocationData();
    	var positionInfo = that.getPositionInfoFromGeolocationData(movedGeolocationData);
    	//var heightReference = that.getHeightReferenceFromData();
		that.changePositionInfo(positionInfo);
		//that.changeHeightInfo(heightReference);
	}
	
	//높이 참조가 '기준없음(Mago3D.HeightReference.NONE)' 이 아닐 경우 높이 보정 
	/*translateInteraction.on(Mago3D.TranslateInteraction.EVENT_TYPE.MOVE_END_F4D, function(moved) {
		moveEnd();
	});
	translateInteraction.on(Mago3D.TranslateInteraction.EVENT_TYPE.MOVE_END_NATIVE, function(moved) {
		moveEnd();
	});
	
	function moveEnd() {
    	if($('#dcHeightReference').val() !== Mago3D.HeightReference.NONE) {
    		if(that.selected instanceof Mago3D.MagoRenderable) {
    			console.info(that.selected.smartTileOwner.X + ' : ' + that.selected.smartTileOwner.Y)
    		}
    		that.addNeedValidHeightData();
    	}
	}*/
	
	//색상변경 적용
	$('#data-control-color-apply-btn').click(function() {
		that.changeColor();
	});
	//색상변경 취소
	$('#data-control-color-restore-btn').click(function() {
		that.restoreColor();
	});
	
	$('#data-control-wrap div.object-setup-color span.color-value,#data-control-wrap div.object-setup-color input[type="text"]').click(function() {
		$('#data-control-wrap div.object-setup-color input[type="color"]').trigger('click');
	}).css('cursor','pointer');
	
	//칼라피커 색상 인풋에 저용
	$('#data-control-wrap div.object-setup-color input[type="color"]').change(function(){
		var color = $(this).val();
		$('#data-control-wrap div.object-setup-color span.color-value').css('background', color);
		$('#data-control-wrap div.object-setup-color input[type="text"]').val(color);
	});
	
	//회전 변경 range 조절
	$('#data-control-input-pitch,#data-control-input-roll,#data-control-input-heading').on('input change',function(){
		var val = $(this).val();
		var type = $(this).data('type');
		$('#dc' + type).val(val);

		that.changeRotation();
	});

	//회전 변경 버튼 조절
	var rotBtnHoldInterval;
	$('.dcRangeBtn').on('click', function(e) {
		if (rotBtnHoldInterval) clearInterval(rotBtnHoldInterval);
		changeRotation($(this));
	});
	$('.dcRangeBtn').on('mousedown', function(e) {
		if (rotBtnHoldInterval) clearInterval(rotBtnHoldInterval);
		var $this = $(this);
		rotBtnHoldInterval = setInterval(function(){
			changeRotation($this);
		}, 150);
	});
	$('.dcRangeBtn').on('mouseup mouseleave',function() {
		clearInterval(rotBtnHoldInterval);
	});

	function changeRotation($btn) {
		var type = $btn.data('type');
		var range = $btn.parent().siblings('input[type="range"]');
		var offset = (type ==='prev') ? -1 : 1;
		var curVal = parseFloat(range.val());
		range.val(curVal + offset).change();
	}
	
	//데이터 높이 이벤트
	var locAltholdInterval;
	$('#data-control-alt-up-btn,#data-control-alt-down-btn').on('click', function(e) {
		if (locAltholdInterval) clearInterval(locAltholdInterval);
		changeAltitude($(this));
	});
	$('#data-control-alt-up-btn,#data-control-alt-down-btn').on('mousedown', function(e) {
		if (locAltholdInterval) clearInterval(locAltholdInterval);
		var $this = $(this);
		locAltholdInterval = setInterval(function(){
			changeAltitude($this);
		}, 150);
	});
	
	function changeAltitude($btn) {
		var type = $btn.data('type');
		var offset = parseFloat($('#data-control-input-altitude-offset').val());
		offset = (type==='up') ? offset : -offset;

		var alt = parseFloat($('#data-control-input-altitude').val());
		$('#data-control-input-altitude').val(alt + offset);

		that.changePosition();
	}
	
	$('#data-control-alt-up-btn,#data-control-alt-down-btn').on('mouseup mouseleave',function() {
		clearInterval(locAltholdInterval);
	});
	
	//속성조회
	$('#data-control-attr-btn').click(function(){
		detailDataInfo("/datas/" + that.selected.data.dataId);
	});
	
	//위치회전정보 저장
	$('#data-control-save-btn').click(function() {
		if(confirm(JS_MESSAGE["data.update.check"])) {
			if(!that.selected) {
				alert(JS_MESSAGE["data.not.select"]);
				return false;
			}
			startLoading();
			var formData = $('#data-control-form').serialize();
			var dataId = that.selected.data.dataId;
			$.ajax({
				url: "/datas/" + dataId,
				type: "POST",
				headers: {"X-Requested-With": "XMLHttpRequest"},
				data: formData,
				success: function(msg){
					if(msg.statusCode <= 200) {
						alert(JS_MESSAGE["update"]);
					} else if(msg.statusCode === 403) {
						//data.smart.tiling
						alert(JS_MESSAGE["data.smart.tiling.grant.required"]);
					} else if (msg.statusCode === 428) {
						if(confirm(JS_MESSAGE[msg.errorCode])) {
							$('input[name="dataId"]').val(dataId);
							var formData = $('#data-control-form').serialize();
							$.ajax({
								url: "/data-adjust-logs",
								type: "POST",
								headers: {"X-Requested-With": "XMLHttpRequest"},
								data: formData,
								success: function(msg){
									if(msg.statusCode <= 200) {
										alert(JS_MESSAGE["requested"]);
									} else {
										alert(JS_MESSAGE[msg.errorCode]);
										console.log("---- " + msg.message);
									}
									insertDataAdjustLogFlag = true;
								},
								error: function(request, status, error){
							        alert(JS_MESSAGE["ajax.error.message"]);
							        insertDataAdjustLogFlag = true;
								},
								always: function(msg) {
									$('input[name="dataId"]').val("");
								}
							});
						}
					} else {
						alert(JS_MESSAGE[msg.errorCode]);
						console.log("---- " + msg.message);
					}
					updateDataInfoFlag = true;
				},
				error:function(request, status, error){
			        alert(JS_MESSAGE["ajax.error.message"]);
			        updateDataInfoFlag = true;
				}
			}).always(stopLoading);
		} else {
			//alert('no');
		}
	});
	
	var rotSliderOn = false;
	var x =0;
	
	var rotationSetupDivElement = document.querySelector('#data-control-wrap .object-setup-rotation');
	
	rotationSetupDivElement.addEventListener('mousedown', function(e) {
		if(e.target && e.target.className === 'scroll') {
			rotSliderOn = true;
			x = e.clientX;
		}
	});
	document.addEventListener('mouseup', function(e) {
		if(rotSliderOn) {
			rotSliderOn = false;
			x = 0;
		}
		
	});
	rotationSetupDivElement.addEventListener('mouseleave', function(e) {
		if(e.target && e.target.className === 'scroll' && rotSliderOn) {
			rotSliderOn = false;
			x = 0;
		}
		
	});
	rotationSetupDivElement.addEventListener('mousemove', function(e) {
		
		if(e.target && (e.target.className === 'scroll' || e.target.className === 'wscroll-bg') && rotSliderOn) {
			var scroll = e.target.className === 'scroll' ? e.target : e.target.children.item(0);
			var style = window.getComputedStyle(scroll);
			var marginLeft = parseInt(style['margin-left']);
			
			var curX = e.clientX;
			var diff = curX- x;
			var calX = Math.abs(diff);
			
			//false is left
			var direction = diff < 0 ? false : true;
			x = curX;
			marginLeft = marginLeft + (direction ? calX : calX*-1);
			
			if((marginLeft < minMarginLeft && !direction) || (marginLeft > maxMarginLeft && direction)) return;
			
			scroll.style['margin-left'] = `${marginLeft }px`;
		}
	});
	var rotChange = function(e) {
		that.changeRotation();
	}
	
	var PREFIX_ROT_INPUT_ID = 'data-control-input-';
	const scrollObserver = new MutationObserver(function(mutations) {
		mutations.forEach(function(mutation) {
			if(mutation.target.className !== 'scroll') return;
			var style = window.getComputedStyle(mutation.target);
			var curMarginLeft = parseInt(style['margin-left']);
			
			var rot = API.MATH.minMaxDenormalize(minRotation, maxRotation, curMarginLeft, (maxMarginLeft - minMarginLeft));
			var type = mutation.target.dataset.type;
			var id = `${PREFIX_ROT_INPUT_ID + type}`;
			
			var input = document.getElementById(id); 
			input.value = rot;
			rotChange(input);
		});
	});
	scrollObserver.observe(rotationSetupDivElement, { attributes: true, attributeFilter:['style'],subtree: true,childList:true, attributeOldValue:true});
	
	$('#data-control-wrap').on('change', 'input.data-control-rotation-input', rotChange);
}

SelectedDataController.prototype.selectData = function(selected, parent) {
	var that = this;
	that.toggleWrap(true);
	that.selected = selected;
	that.parent = parent;
	init(selected, parent);
	
	function init(selectedData, parent) {
		setTitle(selectedData, parent);
		setHex(selectedData, parent);
		setPositionInfo(selectedData, parent);
		setBtn(selectedData);
	}
	
	function setTitle(selectData, parent) {
		if(selectData instanceof Mago3D.Node || selectData instanceof Mago3D.NeoReference) {
			
			
			var target = (selectData instanceof Mago3D.Node) ? selectData : parent;
			var data = target.data;
			var projectId = data.projectId;

			var dataGroupName = MAGO.dataGroup.get(projectId);

			var tempDataName = data.data_name || data.nodeId;
			if(tempDataName.indexOf("F4D_") >= 0) {
				tempDataName = tempDataName.replace("F4D_", "");
			}
			var title = dataGroupName + ' / ' + tempDataName + ((parent) ? ' / ' + selectData.objectId : '');
			
			$('#data-control-wrap-header').text(title);
		} else if(selectData instanceof Mago3D.MagoRenderable) {
			$('#data-control-wrap-header').text(selectData._guid);
		}
	}
	
	function setHex(selectData) {
		var hex = '#000000';
		if(selectData instanceof Mago3D.Node) {
			var data = selectData.data; 
			if(data.aditionalColor && data.isColorChanged) {
				hex = data.aditionalColor.getHexCode();
			}
		} else if(selectData instanceof Mago3D.MagoRenderable) {
			if(selectData.color4) {
				hex = selectData.color4.getHexCode();
			}
		}
		$('#data-control-wrap div.object-setup-color span.color-value').css('background', hex);
		$('#data-control-wrap div.object-setup-color input[type="text"]').val(hex);
		$('#data-control-wrap div.object-setup-color input[type="color"]').val(hex).change;
		//$('#dcColorPicker').val(hex).change();
	}
	
	function setPositionInfo(selectData, parent) {
		$('#data-control-form').hide();
		if(selectData instanceof Mago3D.NeoReference) {
			return false;
		} else {
			$('#data-control-form').show();
			var currentGeoLocData = selectData.getCurrentGeoLocationData();
			
			var positionInfo = that.getPositionInfoFromGeolocationData(currentGeoLocData);
			that.changePositionInfo(positionInfo);
		}
	}
	
	function setBtn(selectData) {
		var $saveBtn = $('#data-control-save-btn');
		var $attrBtn = $('#data-control-attr-btn');
		
		$('#data-control-issue-btn').hide();
		$saveBtn.show();
		$attrBtn.show();
		//$('#dcOpacityBox').show();
		if(selectData instanceof Mago3D.Node) {
			if(selectData.data.attributes.fromSmartTile || selectData.data.attributes.isReference || !MAGO.dataGroup.get(selectData.data.projectId)) {
				$saveBtn.hide();
				/*$('#dcShowAttr').hide();*/
			}
		} else {/* if(selectData instanceof Mago3D.MagoRenderable || selectData instanceof Mago3D.NeoReference) { */
			$saveBtn.hide();
			$attrBtn.hide();
			/*if(selectData instanceof Mago3D.NeoReference) {
				$('#dcOpacityBox').hide();
			}*/
		}
	}
}
SelectedDataController.prototype.deselectData = function() {
	this.toggleWrap(false);
	this.selected = undefined;
}
SelectedDataController.prototype.changePositionInfo = function(obj) {
	$('#data-control-input-longitude').val(obj.longitude);
	$('#data-control-input-latitude').val(obj.latitude);
	$('#data-control-input-altitude').val(obj.altitude);

	//set span margin-left, change input value
	$('#data-control-wrap .object-setup-rotation span[data-type="pitch"]').css('marginLeft', `${API.MATH.minMaxNormalize(minRotation, maxRotation, obj.pitch, 90)}px`);
	$('#data-control-wrap .object-setup-rotation span[data-type="heading"]').css('marginLeft', `${API.MATH.minMaxNormalize(minRotation, maxRotation, obj.heading, 90)}px`);
	$('#data-control-wrap .object-setup-rotation span[data-type="roll"]').css('marginLeft', `${API.MATH.minMaxNormalize(minRotation, maxRotation, obj.roll, 90)}px`);
}
SelectedDataController.prototype.changeHeightInfo = function(heightReference) {
	switch(heightReference) {
		case Mago3D.HeightReference.CLAMP_TO_GROUND : {
			$('#data-control-input-altitude').val(0);
			break;
		}
		case Mago3D.HeightReference.RELATIVE_TO_GROUND :
		case Mago3D.HeightReference.NONE : {
			var currentGeoLocData = this.selected.getCurrentGeoLocationData();
			var positionInfo = this.getPositionInfoFromGeolocationData(currentGeoLocData);
			
			var surfaceHeight = (this.selected instanceof Mago3D.Node) ? this.selected.data.surfaceHeight : this.selected.terrainHeight;
			var height = (heightReference === Mago3D.HeightReference.NONE) ? positionInfo.altitude : positionInfo.altitude - surfaceHeight; 
			$('#data-control-input-altitude').val(height);
			break;
		}
	}
}

SelectedDataController.prototype.getHeightReferenceFromData = function() {
	var store = (this.selected instanceof Mago3D.Node) ? this.selected.data.attributes : this.selected.options;
	return store.heightReference || Mago3D.HeightReference.NONE;
}
SelectedDataController.prototype.setHeightReferenceToData = function(heightReference) {
	var store = (this.selected instanceof Mago3D.Node) ? this.selected.data.attributes : this.selected.options;
	store.heightReference = heightReference;
}

SelectedDataController.prototype.addNeedValidHeightData = function() {
	var targetArrayName = (this.selected instanceof Mago3D.Node) ? '_needValidHeightNodeArray' : '_needValidHeightNativeArray';
	
	var targetArray = this.magoInstance.getMagoManager()[targetArrayName];
	if(targetArray.indexOf(this.selected) < 0) {
		targetArray.push(this.selected);
	}
}

SelectedDataController.prototype.changeColor = function() {
	if(this.selected instanceof Mago3D.Node) {
		this.changeF4dColor();
	} else if (this.selected instanceof Mago3D.MagoRenderable) {
		this.changeNativeColor();
	} else { /* this.selected instanceof Mago3D.NeoReference*/
		this.changeReferenceColor();
	}
}
SelectedDataController.prototype.restoreColor = function() {
	if(this.selected instanceof Mago3D.Node) {
		this.restoreF4dColor();
	} else if (this.selected instanceof Mago3D.MagoRenderable) {
		this.restoreNativeColor();
	} else { /* this.selected instanceof Mago3D.NeoReference*/
		this.restoreReferenceColor();
	}
}

SelectedDataController.prototype.changeReferenceColor = function() {
	if(!this.selected || !(this.selected instanceof Mago3D.NeoReference)) {
		alert(JS_MESSAGE["data.select"]);
		return;
	}
	var data = this.parent.data;

	var rgbArray = hex2rgbArray($('#data-control-wrap div.object-setup-color input[type="text"]').val());
	changeColorAPI(this.magoInstance, data.projectId, data.nodeId, [this.selected.objectId], 'isPhysical=true', rgbArray.join(','));
}

SelectedDataController.prototype.changeF4dColor = function() {
	if(!this.selected || !(this.selected instanceof Mago3D.Node)) {
		alert(JS_MESSAGE["data.select"]);
		return;
	}
	var data = this.selected.data;

	var rgbArray = hex2rgbArray($('#data-control-wrap div.object-setup-color input[type="text"]').val());
	changeColorAPI(this.magoInstance, data.projectId, data.nodeId, null, 'isPhysical=true', rgbArray.join(','));
}

SelectedDataController.prototype.changeNativeColor = function() {
	if(!this.selected || !(this.selected instanceof Mago3D.MagoRenderable)) {
		alert(JS_MESSAGE["data.select"]);
		return;
	}
	var rgbArray = hex2rgbArray($('#data-control-wrap div.object-setup-color input[type="text"]').val());
	this.selected.setOneColor(rgbArray[0]/255,rgbArray[1]/255,rgbArray[2]/255,1);
}

SelectedDataController.prototype.restoreF4dColor = function() {
	this.selected.deleteChangeColor(this.magoInstance.getMagoManager());
}

SelectedDataController.prototype.restoreReferenceColor = function() {
	this.parent.deleteChangeColor(this.magoInstance.getMagoManager());
}

SelectedDataController.prototype.restoreNativeColor = function() {
	this.selected.restoreColor();
}

SelectedDataController.prototype.changePosition = function() {
	if(this.selected instanceof Mago3D.Node) {
		this.changeF4dPosition();
	} else if(this.selected instanceof Mago3D.MagoRenderable) {
		this.changeNativePosition();
	}
}

SelectedDataController.prototype.changeF4dPosition = function() {
	var positionInfo = this.getPositionInfoFromElem();

	var height = 0; 
	if($('#dcHeightReference').val() !== Mago3D.HeightReference.RELATIVE_TO_GROUND) {
		height = positionInfo.altitude
	} else {
		this.selected.data.relativeHeight = positionInfo.altitude;
		height = this.selected.data.relativeHeight + this.selected.data.surfaceHeight;
	}
	
	this.selected.changeLocationAndRotation(positionInfo.latitude, positionInfo.longitude, height, positionInfo.heading, positionInfo.pitch, positionInfo.roll, this.magoInstance.getMagoManager());
}

SelectedDataController.prototype.changeNativePosition = function() {
	var positionInfo = this.getPositionInfoFromElem();

	var height = 0; 
	if($('#dcHeightReference').val() === Mago3D.HeightReference.RELATIVE_TO_GROUND) {
		this.selected.relativeHeight = positionInfo.altitude;
		height = this.selected.relativeHeight + this.selected.terrainHeight;
	} else if($('#dcHeightReference').val() === Mago3D.HeightReference.CLAMP_TO_GROUND){
		height = this.selected.getCurrentGeoLocationData().geographicCoord.altitude;
	} else {
		height = positionInfo.altitude;
	}
	
	this.selected.changeLocationAndRotation(positionInfo.latitude, positionInfo.longitude, height, positionInfo.heading, positionInfo.pitch, positionInfo.roll);
}

SelectedDataController.prototype.changeRotation = function() {
	var positionInfo = this.getPositionInfoFromElem();
	var currentGeographicCoord = this.selected.getCurrentGeoLocationData().geographicCoord;
	this.selected.changeLocationAndRotation(currentGeographicCoord.latitude, currentGeographicCoord.longitude, currentGeographicCoord.altitude, positionInfo.heading, positionInfo.pitch, positionInfo.roll, this.magoInstance.getMagoManager());
}

SelectedDataController.prototype.getPositionInfoFromElem = function() {
	return {
		longitude : parseFloat($('#data-control-input-longitude').val()),
		latitude : parseFloat($('#data-control-input-latitude').val()),
		altitude : parseFloat($('#data-control-input-altitude').val()),
		heading : parseInt($('#data-control-input-heading').val()),
		pitch : parseInt($('#data-control-input-pitch').val()),
		roll : parseInt($('#data-control-input-roll').val())
	}
}

SelectedDataController.prototype.getPositionInfoFromGeolocationData = function(geoLocData) {
	var geoCoord = geoLocData.geographicCoord;
	
	return {
		longitude : geoCoord.longitude,
		latitude : geoCoord.latitude,
		altitude : geoCoord.altitude,
		heading : geoLocData.heading,
		pitch : geoLocData.pitch,
		roll : geoLocData.roll,
	}
}

SelectedDataController.prototype.toggleWrap = function(show) {
	if(show) {
		$('#data-control-wrap').show();
	} else {
		$('#data-control-wrap').hide();
	}
	/*var isRightMenuButton = false;
	if(show) {
		$('#dataControlWrap').css({
			width : '340px'
		});
		$('#mapSettingWrap').css({
			width :'0px'
		});
		isRightMenuButton = true;
	} else {
		$('#dataControlWrap').css({
			width : '0px'
		});
		if($('#mapCtrlSetting').hasClass('on')) {
			$('#mapSettingWrap').css({
				width : '340px'
			});
			isRightMenuButton = true;
		}
	}
	if(isRightMenuButton) {
		$('#mapCtrlWrap').css({right:'340px'});
		$('#baseMapToggle').css({right:'392px'});
		$('#terrainToggle').css({right:'612px'});
	} else {
		$('#mapCtrlWrap').css({right:'0px'});
		$('#baseMapToggle').css({right:'50px'});
		$('#terrainToggle').css({right:'270px'});
	}*/
}
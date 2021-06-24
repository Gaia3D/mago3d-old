const SmltWater =(function() {
	const SmltWater = function(magoInstance) {
		this.magoInstance = magoInstance;
		this._active = false;
		this._step = SmltWater.STATUS.UNREADY;
		this._mode = SmltWater.MODE.WAITING;
		this._createType = SmltWater.CREATE_TYPE.NONE;
		this.action;
		
		const viewer = this.magoInstance.getViewer();
		this.cesiumEventHandler = new Cesium.ScreenSpaceEventHandler(viewer.canvas);
		
		let self = this;
		const _setEventHandler = function() {
			const $waterBtns = $('#smlt-natural-water-sub button'); 
			$waterBtns.click(function() {
				if(!self.active) return;
				
				const $clicked = $(this); 
				let mode = $clicked.data('mode');
				
				if(mode === SmltWater.MODE.CLOSE) return;
				
				SmltWater.offButton();
				$clicked.addClass('on');
				
				self.mode = mode;
				self.createType = undefined;
				if(mode === SmltWater.MODE.CREATE) self.createType = $clicked.data('type'); 
			});
		}
		
		_setEventHandler();
	}
	
	SmltWater.STATUS = {
		UNREADY : 0,
		READY : 1
	}
	
	SmltWater.MODE = {
		WAITING : 'waiting',
		AREA : 'area',
		CREATE : 'create',
		MOVING : 'moving',
		DELETE : 'delete',
		CLOSE : 'close',
		CLEAR : 'clear'
	}
	SmltWater.MODULE_TYPE = {
		MAGO3D : 0,
		CESIUM : 1,
		NORMAL : 2
	}
	SmltWater.CREATE_TYPE = {
		NONE : 'none',
		WATER : 'water',
		POLUTION : 'polution',
		WEIR : 'weir'
	}
	
	Object.defineProperties(SmltWater.prototype, {
		active : {
			get : function () {
				return this._active;
			},
			set : function (active) {
				if(!active) {
					this.unbindMouseEvent();
					this.clear();
				}
				
				this._active = active;
			}
		},
		step : {
			get : function () {
				return this._step;
			},
			set : function(step) {
				if(step === SmltWater.STATUS.UNREADY) {
					SmltWater.hideActionButton();
				} else {
					SmltWater.showActionButton();
				}
				
				this._step = step;
			}
		},
		mode : {
			get : function () {
				return this._mode;
			},
			set : function(mode) {
				const action = _getModeAction(mode);
				if(action) {
					this.action = action;
					this.action.run.call(this);
				}
				this._mode = mode;
			}
		},
		createType : {
			get : function () {
				return this._createType;
			},
			set : function(createType) {
				this._createType = createType;
			}
		}
	});
	
	SmltWater.prototype.bindMouseEvent = function() {
		if(!this.action) return;
		
		const bindEventList = this.action.eventList;
		
		for(let key in bindEventList) {
			let eventType = bindEventList[key];
			let event = this.action[bindEventList[key]].bind(this);
			
			this.cesiumEventHandler.setInputAction(event, eventType);
		}
	}
	
	SmltWater.prototype.unbindMouseEvent = function() {
		this.cesiumEventHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOWN);
		this.cesiumEventHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_UP);
		this.cesiumEventHandler.removeInputAction(Cesium.ScreenSpaceEventType.MOUSE_MOVE);
		this.cesiumEventHandler.removeInputAction(Cesium.ScreenSpaceEventType.RIGHT_CLICK);
	}
	
	SmltWater.prototype.clear = function() {
		this.step = SmltWater.STATUS.UNREADY;
		this.mode = SmltWater.MODE.WAITING;
		
		_clear(this.magoInstance);
	}
	
	let leftDown = false;
	let guidePoint;
	let leftDownCoord;
	let leftUpCoord;
	let rectangle;
	let polyline;
	
	let objects = {};
	
	const _Area = {
		type : SmltWater.MODULE_TYPE.CESIUM,
		eventList : [Cesium.ScreenSpaceEventType.LEFT_DOWN, Cesium.ScreenSpaceEventType.LEFT_UP, Cesium.ScreenSpaceEventType.MOUSE_MOVE]
	}
	_Area['run'] = function() {
		_clear(this.magoInstance);
		this.unbindMouseEvent();
		this.bindMouseEvent();
	}
	_Area['terminate'] = function() {
		_clearGuidePoint(this.magoInstance.getViewer());
		leftDownCoord = undefined;
		leftUpCoord = undefined;
		leftDown = false;
		
		this.magoInstance.getMagoManager().setCameraMotion(true);
		this.step = SmltWater.STATUS.READY;
		this.mode = SmltWater.MODE.WAITING;
		
		this.action = undefined;
		this.unbindMouseEvent();
		
		SmltWater.offButton();
	}
	_Area[Cesium.ScreenSpaceEventType.LEFT_DOWN] = function(event) {
		if(!this.active || !guidePoint) return;
		const magoManager = this.magoInstance.getMagoManager();
		
		_leftDown(event.position, '사각형이 끝날 지점에서 왼쪽 버튼에서 손떼시요ㅋ',magoManager);
	}
	_Area[Cesium.ScreenSpaceEventType.LEFT_UP] = function(event) {
		if(!this.active) return;
		
		rectangle.rectangle.coordinates = rectangle.rectangle.coordinates.getValue();
		
		this.action.terminate.call(this);
	}
	_Area[Cesium.ScreenSpaceEventType.MOUSE_MOVE] = function(event) {
		const _beforeLeftDown = function(position, magoInstance) {
			if(!guidePoint) _createGuidePoint(magoInstance.getViewer(), {
				text: '시작할 지점을 먼저 좌클릭해라ㅎ 네모그릴거임ㅋ',
				pixelOffset : new Cesium.Cartesian2(200,0)
			});
			
			guidePoint.position = position;
		}
		
		const _afterLeftDown = function(position, magoInstance) {
			if(!guidePoint) return;
			
			guidePoint.position = leftUpCoord = position;
			if(!rectangle) _createRectangle(magoInstance.getViewer());
		}
		
		if(!this.active) return;
		
		const cartesian = _screenToCartesian3(event.endPosition, this.magoInstance.getMagoManager());
		if(leftDown) {
			_afterLeftDown(cartesian, this.magoInstance);
		} else {
			_beforeLeftDown(cartesian, this.magoInstance);
		}
	}
	
	const _Create = {
		type : SmltWater.MODULE_TYPE.CESIUM,
		eventList : [Cesium.ScreenSpaceEventType.LEFT_DOWN, Cesium.ScreenSpaceEventType.LEFT_UP, Cesium.ScreenSpaceEventType.MOUSE_MOVE]	
	}
	_Create['run'] = function() {
		this.unbindMouseEvent();
		this.bindMouseEvent();
	}
	_Create['terminate'] = function() {
		_clearGuidePoint(this.magoInstance.getViewer());
		_clearPolyline(this.magoInstance.getViewer());
		leftDownCoord = undefined;
		leftUpCoord = undefined;
		leftDown = false;
		
		this.magoInstance.getMagoManager().setCameraMotion(true);
		this.mode = SmltWater.MODE.WAITING;
		
		this.action = undefined;
		this.unbindMouseEvent();
		
		SmltWater.offButton();
	}
	_Create[Cesium.ScreenSpaceEventType.LEFT_DOWN] = function(event) {
		if(!this.active || !guidePoint) return;
		
		if(this.createType !== SmltWater.CREATE_TYPE.WEIR) {
			leftDownCoord = event.position;	
		} else {
			_leftDown(event.position, '보를 완성할 지점에서 왼쪽 버튼에서 손떼시요ㅋ', this.magoInstance.getMagoManager());
		}
	}
	_Create[Cesium.ScreenSpaceEventType.LEFT_UP] = function(event) {
		const self = this;
		
		const _createObject = function(crts, type) {
			let entity;
			const viewer = self.magoInstance.getViewer();
			if(type !== SmltWater.CREATE_TYPE.WEIR) {
				entity = viewer.entities.add({
					position : crts,
					point : {
						color : (type === SmltWater.CREATE_TYPE.WATER) ? Cesium.Color.ALICEBLUE : Cesium.Color.DARKRED,
						pixelSize : 30
					},
					label : {
						text: (type === SmltWater.CREATE_TYPE.WATER) ? '물나오는곳':'똥나오는곳',
			            scale :0.5,
			            font: "normal normal bolder 35px Helvetica",
			            fillColor: Cesium.Color.BLACK,
			            outlineColor: Cesium.Color.WHITE,
			            outlineWidth: 1,
						pixelOffset : new Cesium.Cartesian2(50,0), 
			            heightReference : Cesium.HeightReference.CLAMP_TO_GROUND,
			            style: Cesium.LabelStyle.FILL_AND_OUTLINE,
			            distanceDisplayCondition : new Cesium.DistanceDisplayCondition(0.0, 100000)
					}
				});
			} else {
				entity = viewer.entities.add({
					polyline : {
						positions : _polylineCoordinates(),
						material : Cesium.Color.DARKORANGE.withAlpha(1),
						width : 20
					}
				});
			}
			
			objects[entity.id] = entity;
		}
		if(!this.active) return;
		
		if(!leftDownCoord) return;
		
		const position = event.position;
		if(this.createType !== SmltWater.CREATE_TYPE.WEIR && Cesium.Cartesian2.distance(leftDownCoord,position) > 10) return;
		
		const cartesian = _screenToCartesian3(position, this.magoInstance.getMagoManager());
		if(!_checkAreaContainPoint(cartesian)) {
			alert('시뮬레이션 영역내에서 선택해주십시요.');
			return false;
		}
		
		_createObject(cartesian, this.createType);
		
		this.action.terminate.call(this);
	}
	_Create[Cesium.ScreenSpaceEventType.MOUSE_MOVE] = function(event) {
		if(!this.active) return;
		
		function getTextAndOffset(t) {
			let text;
			let pixelOffset;
			
			switch(t) {
				case SmltWater.CREATE_TYPE.WATER : {
					text = '클릭한 지점에서 물이 지면을 따라 흐릅니다.';
					pixelOffset = new Cesium.Cartesian2(200,0);
					break;
				}
				case SmltWater.CREATE_TYPE.POLUTION : {
					text = '클릭한 지점에서 오염원이 지면을 따라 흐릅니다.';
					pixelOffset = new Cesium.Cartesian2(200,0);
					break;
				}
				case SmltWater.CREATE_TYPE.WEIR : {
					text = leftDown ? '보를 완성할 지점에서 왼쪽 버튼에서 손떼시요ㅋ':'드래그를 하여 선을 그리십시요. 물길을 막는 보가 생성됩니다.';
					pixelOffset = new Cesium.Cartesian2(200,0);
					break;
				}
			}
			
			return {
				text,
				pixelOffset
			}
		} 
		const cartesian = _screenToCartesian3(event.endPosition, this.magoInstance.getMagoManager());
		const viewer = this.magoInstance.getViewer();
		if(leftDown) {
			leftUpCoord = cartesian;
			if(!polyline) _createPolyline(viewer);
		} else {
			if(!guidePoint) {
				_createGuidePoint(viewer, getTextAndOffset(this.createType));
			}
		}
		
		guidePoint.position = cartesian;
		
		if(!_checkAreaContainPoint(cartesian)) {
			guidePoint.label.text = '시뮬레이션 영역내에서 선택해주세요.';
			guidePoint.label.fillColor = Cesium.Color.RED;
		} else {
			let {text, pixelOffset} = getTextAndOffset(this.createType);
			guidePoint.label.text = text;
			guidePoint.label.pixelOffset = pixelOffset;
			guidePoint.label.fillColor = Cesium.Color.BLACK;	
		}
	}
	
	const _Clear = {
		type : SmltWater.MODULE_TYPE.NORMAL	
	}
	_Clear['run'] = function() {
		if(confirm('초기화하시겠습니까?')) {
			_clear(this.magoInstance);
			this.step = SmltWater.STATUS.UNREADY;
			this.mode = SmltWater.MODE.WAITING;
			this.action = undefined;
			this.unbindMouseEvent();	
		}
		
		SmltWater.offButton();
	}

	const _clear = function(magoInstance) {
		const viewer = magoInstance.getViewer();
		_clearGuidePoint(viewer);
		_clearRectangle(viewer);
		_clearPolyline(viewer);
		_clearObjects(viewer);
		
		leftDown = false;
		leftDownCoord = undefined;
		leftUpCoord = undefined;
		
		magoInstance.getMagoManager().setCameraMotion(true);
	}
	const _leftDown = function(position, text, magoManager) {
		magoManager.setCameraMotion(false);
		leftDownCoord = _screenToCartesian3(position, magoManager);
		leftDown = true;
		guidePoint.point.pixelSize = 0.1;
		guidePoint.label.text = text;
	}
	
	const _clearObjects = function(viewer) {
		for(let id in objects) {
			viewer.entities.removeById(id);
		}
		objects = {};
	}
	
	const _createGuidePoint = function(viewer, labelOption) {
		let _labelOption = {
			text: '시작할 지점을 먼저 좌클릭해라ㅎ 네모그릴거임ㅋ',
            scale :0.5,
            font: "normal normal bolder 35px Helvetica",
            fillColor: Cesium.Color.BLACK,
            outlineColor: Cesium.Color.WHITE,
            outlineWidth: 1,
			pixelOffset : new Cesium.Cartesian2(200,0), 
            heightReference : Cesium.HeightReference.CLAMP_TO_GROUND,
            style: Cesium.LabelStyle.FILL_AND_OUTLINE,
            distanceDisplayCondition : new Cesium.DistanceDisplayCondition(0.0, 100000)
		}
		
		guidePoint = viewer.entities.add({
			point : {
				color : Cesium.Color.DODGERBLUE,
				pixelSize : 10
			},
			label : Object.assign(_labelOption, labelOption)
		});
		
		return guidePoint;
	}
	
	const _clearGuidePoint = function(viewer) {
		if(guidePoint) {
			viewer.entities.remove(guidePoint);
			guidePoint = undefined;	
		}
	}
	
	const _areaRectangleCoordinates = function() {
		let topLeftCarto = Cesium.Cartographic.fromCartesian(leftDownCoord);
		let bottomRightCarto = Cesium.Cartographic.fromCartesian(leftUpCoord);
		
		let west = (topLeftCarto.longitude < bottomRightCarto.longitude) ? topLeftCarto.longitude : bottomRightCarto.longitude;
		let south = (topLeftCarto.latitude < bottomRightCarto.latitude) ? topLeftCarto.latitude : bottomRightCarto.latitude;
		let east = (topLeftCarto.longitude < bottomRightCarto.longitude) ? bottomRightCarto.longitude : topLeftCarto.longitude;
		let north = (topLeftCarto.latitude < bottomRightCarto.latitude) ? bottomRightCarto.latitude : topLeftCarto.latitude;
		
		return new Cesium.Rectangle(west, south, east, north);
	};
	
	const _createRectangle = function(viewer) {
		rectangle = viewer.entities.add({
			rectangle : {
				coordinates : new Cesium.CallbackProperty(_areaRectangleCoordinates),
				material : Cesium.Color.DODGERBLUE.withAlpha(0.2)
			}
		});
		return rectangle;
	}
	
	const _clearRectangle = function(viewer) {
		if(rectangle) {
			viewer.entities.remove(rectangle);
			rectangle = undefined;	
		}
	}
	
	const _polylineCoordinates = function() {
		return [leftDownCoord, leftUpCoord];
	};
	
	const _createPolyline = function(viewer) {
		polyline = viewer.entities.add({
			polyline : {
				positions : new Cesium.CallbackProperty(_polylineCoordinates),
				material : Cesium.Color.DARKORANGE.withAlpha(1),
				width : 5
			}
		});
		return polyline;
	}
	
	const _clearPolyline = function(viewer) {
		if(polyline) {
			viewer.entities.remove(polyline);
			polyline = undefined;	
		}
	}
	
	const _screenToCartesian3 = function(position, magoManager) {
		return API.Converter.magoToCesiumForPoint3D(
			API.Converter.screenCoordToMagoPoint3D(position.x, position.y, magoManager)	
		);
	}
	
	const _checkAreaContainPoint = function(cartesian) {
		return Cesium.Rectangle.contains(rectangle.rectangle.coordinates.getValue(), Cesium.Cartographic.fromCartesian(cartesian));
	}
	
	const _getModeAction = function(mode) {
		let action;
		switch(mode) {
			case SmltWater.MODE.AREA : action = _Area;break;
			case SmltWater.MODE.CREATE : action = _Create;break;
			case SmltWater.MODE.CLEAR : action = _Clear;break;
		}
		
		return action;
	}
	
	return SmltWater;
})(); 

SmltWater.offButton = function() {
	$('#smlt-natural-water-sub button').removeClass('on');	
}


SmltWater.hideActionButton = function() {
	$('.water-action-btn').hide();
}
SmltWater.showActionButton = function() {
	$('.water-action-btn').show();
}


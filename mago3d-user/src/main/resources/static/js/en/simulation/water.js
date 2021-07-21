const SmltWater =(function() {
	const SmltWater = function(magoInstance) {
		this.magoInstance = magoInstance;
		this._active = false;
		this._step = SmltWater.STATUS.UNREADY;
		this._mode = SmltWater.MODE.WAITING;
		this._createType = SmltWater.CREATE_TYPE.NONE;
		this._action;
		
		this.selectInteraction = new Mago3D.PointSelectInteraction();
		this.translateInteraction = new Mago3D.TranslateInteraction();
		
		var mm = this.magoInstance.getMagoManager();
		mm.interactionCollection.add(this.selectInteraction);
		mm.interactionCollection.add(this.translateInteraction);
		
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
				
				self.createType = undefined;
				if(!$clicked.hasClass('on')) {
					SmltWater.offButton();
					$clicked.addClass('on');
					
					self.mode = mode;
					self.createType = undefined;
					if(mode === SmltWater.MODE.CREATE) self.createType = $clicked.data('type');
					
					const action = self.getModeAction(mode);
					self.action = action;
					self.action.run.call(self);
				} else {
					$clicked.removeClass('on');
					
					self.mode = SmltWater.MODE.WAITING;
					
					if(self.action && self.action.terminate) {
						self.action.terminate.call(self);
						self.action = undefined;
					}
				}
			});
			
			self.magoInstance.getMagoManager().on(Mago3D.MagoManager.EVENT_TYPE.SELECTEDGENERALOBJECT, function(e) {
				if(self.mode === SmltWater.MODE.DELETE) {
					self.action.delete.call(self, e.selected, false);
				}
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
		MOVING : 'move',
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
				this._mode = mode;
			}
		},
		action : {
			get : function () {
				return this._action;
			},
			set : function(action) {
				this._action = action;
	
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
		
		const bindEventList = [Cesium.ScreenSpaceEventType.LEFT_DOWN, Cesium.ScreenSpaceEventType.LEFT_UP, Cesium.ScreenSpaceEventType.MOUSE_MOVE];
		
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
		
		const magoManager = this.magoInstance.getMagoManager();
		
		_toggleDeleteResource(this, false);
		_toggleMovingResource(this, false);
	}
	
	SmltWater.prototype.clear = function() {
		this.step = SmltWater.STATUS.UNREADY;
		this.mode = SmltWater.MODE.WAITING;
		
		_clear(this);
	}
	
	SmltWater.prototype.getModeAction = function(mode) {
		switch(mode) {
			case SmltWater.MODE.AREA : return _Area;
			case SmltWater.MODE.CREATE : return _Create;
			case SmltWater.MODE.CLEAR : return _Clear;
			case SmltWater.MODE.MOVING : return _Moving;
			case SmltWater.MODE.DELETE : return _Delete;
		}
	}
	
	SmltWater.prototype.getWaterObject = function() {
		return objects;
	}
	
	let leftDown = false;
	let guidePoint;
	let leftDownCoord;
	let leftUpCoord;
	let rectangle;
	let polyline;
	
	let objects = {};
	
	const _Area = function() {}
	
	_Area.run = function() {
		this.step = SmltWater.STATUS.UNREADY;
		_clear(this);
		this.unbindMouseEvent();
		this.bindMouseEvent();
	}
	_Area.terminate = function() {
		_clearGuidePoint(this.magoInstance.getViewer());
		leftDownCoord = undefined;
		leftUpCoord = undefined;
		leftDown = false;
		
		this.magoInstance.getMagoManager().setCameraMotion(true);
		this.mode = SmltWater.MODE.WAITING;
		this.unbindMouseEvent();
		
		SmltWater.offButton();
	}
	_Area._createRectangle = function(rectCoordinates) {
		_createRectangle(this.magoInstance.getViewer(), rectCoordinates);	
	}
	_Area.done = function(rectCoordinates) {
		this.step = SmltWater.STATUS.READY;
		let minLon = API.Converter.radToDeg(rectCoordinates.west);
		let minLat = API.Converter.radToDeg(rectCoordinates.south);
		let maxLon = API.Converter.radToDeg(rectCoordinates.east);
		let maxLat = API.Converter.radToDeg(rectCoordinates.north);
		
		let geoExtent = new Mago3D.GeographicExtent(minLon, minLat, 0, maxLon, maxLat, 0);
		
		const magoManager = this.magoInstance.getMagoManager();
		const waterOptions = {};
		waterOptions.terrainDemSourceType = "QUANTIZEDMESH";
		waterOptions.terrainProvider = this.magoInstance.getViewer().terrainProvider;
		waterOptions.simulationGeographicExtent = geoExtent;
		waterOptions.renderParticles = false;
		magoManager.waterManager = new Mago3D.WaterManager(magoManager, waterOptions);
		
		magoManager.waterManager.bSsimulateWater = true;
	}
	_Area[Cesium.ScreenSpaceEventType.LEFT_DOWN] = function(event) {
		if(!this.active || !guidePoint) return;
		const magoManager = this.magoInstance.getMagoManager();
		
		_leftDown(event.position, 'Rectangle의 끝점에서 손을 떼어 주세요.',magoManager);
	}
	_Area[Cesium.ScreenSpaceEventType.LEFT_UP] = function(event) {
		if(!this.active) return;
		const rectCoordinates = rectangle.rectangle.coordinates.getValue();
		rectangle.rectangle.coordinates = rectCoordinates;
		
		this.action.done.call(this, rectCoordinates); 
		
		this.action.terminate.call(this);
	}
	_Area[Cesium.ScreenSpaceEventType.MOUSE_MOVE] = function(event) {
		const _beforeLeftDown = function(position, magoInstance) {
			if(!guidePoint) _createGuidePoint(magoInstance.getViewer(), {
				text: 'Rectangle의 시작점부터 드래그하여 주세요.',
				pixelOffset : new Cesium.Cartesian2(200,0)
			});
			
			guidePoint.position = position;
		}
		
		const _afterLeftDown = function(position, magoInstance) {
			if(!guidePoint) return;
			
			guidePoint.position = leftUpCoord = position;
			if(!rectangle) _createRectangle(magoInstance.getViewer(), new Cesium.CallbackProperty(_areaRectangleCoordinates));
		}
		
		if(!this.active) return;
		
		const cartesian = _screenToCartesian3(event.endPosition, this.magoInstance.getMagoManager());
		if(leftDown) {
			_afterLeftDown(cartesian, this.magoInstance);
		} else {
			_beforeLeftDown(cartesian, this.magoInstance);
		}
	}
	
	const _Create = function() {}
	
	_Create.run = function() {
		_clearCreateResource(this.magoInstance.getViewer());
		this.unbindMouseEvent();
		this.bindMouseEvent();
	}
	_Create.done = function(crts) {
		let entity;
		const magoManager = this.magoInstance.getMagoManager();
		const DEPTH = 6;
		if(this.createType !== SmltWater.CREATE_TYPE.WEIR) {
			let name = 'waterGenerator';
			let color = {r:0.2, g:0.5, b:1.0, a:1.0};
			
			if(this.createType !== SmltWater.CREATE_TYPE.WATER) {
				name = 'contaminationGenerator';
				color = {r:1, g:0.5, b:0.2, a:1.0};
			}
			
			let initialPosition = API.Converter.Cartesian3ToMagoGeographicCoord(crts);
			entity = new Mago3D.Box(20, 20, 40, name);
			entity.setGeographicPosition(initialPosition, 0, 0, 0);
			entity.attributes.isMovable = true;
		  	entity.setOneColor(color.r, color.b, color.b, color.a);
		  	entity.options = {};
	
			magoManager.waterManager.addObject(entity, DEPTH);
		} else {
			let geoCoordsArray = crts.map(API.Converter.Cartesian3ToMagoGeographicCoord);
			entity = Mago3D.Modeler.getLoftMesh(new Mago3D.GeographicCoordsList(geoCoordsArray)/*, positions*/);
			magoManager.modeler.addObject(entity, DEPTH);
		}
		
		objects[entity.guid] = entity;
	}
	_Create.terminate = function() {
		_clearCreateResource(this.magoInstance.getViewer());
		this.magoInstance.getMagoManager().setCameraMotion(true);
		this.unbindMouseEvent();
		
		SmltWater.offButton();
	}
	_Create[Cesium.ScreenSpaceEventType.LEFT_DOWN] = function(event) {
		if(!this.active || !guidePoint) return;
		
		const position = event.position;
		
		if(this.createType !== SmltWater.CREATE_TYPE.WEIR) {
			leftDownCoord = position;	
		} else {
			const magoManager = this.magoInstance.getMagoManager();
			const cartesian = _screenToCartesian3(position, magoManager);
			if(!_checkAreaContainPoint(cartesian)) {
				return false;
			}
			_leftDown(position, '보를 완성할 지점에서 손을 떼어 주세요.', magoManager);
		}
	}
	_Create[Cesium.ScreenSpaceEventType.LEFT_UP] = function(event) {
		if(!this.active) return;
		
		if(!leftDownCoord) return;
		
		const position = event.position;
		if(this.createType !== SmltWater.CREATE_TYPE.WEIR && Cesium.Cartesian2.distance(leftDownCoord,position) > 10) return;
		
		const cartesian = _screenToCartesian3(position, this.magoInstance.getMagoManager());
		if(!_checkAreaContainPoint(cartesian)) {
			alert('시뮬레이션 영역내에서 선택해주십시요.');
			return false;
		}
		
		this.action.done.call(this, this.createType !== SmltWater.CREATE_TYPE.WEIR ? cartesian : _polylineCoordinates());
		
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
					text = leftDown ? '보를 완성할 지점에서 손을 떼어 주세요.':'드래그를 하여 선을 그리십시오. 물길을 막는 보가 생성됩니다.';
					pixelOffset = new Cesium.Cartesian2(200,0);
					break;
				}
			}
			
			return { text, pixelOffset}
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
	
	const _Clear = function() {}
	
	_Clear.run = function() {
		if(confirm('초기화하시겠습니까?')) {
			_clear(this);
			this.step = SmltWater.STATUS.UNREADY;
			this.mode = SmltWater.MODE.WAITING;
			this.unbindMouseEvent();	
		}
		
		SmltWater.offButton();
	}
	
	let isMoving = false;
	const _Moving = function() {}
	_Moving.run = function() {
		_clearCreateResource(this.magoInstance.getViewer());
		this.unbindMouseEvent();
		_toggleDeleteResource(this, false);
		_toggleMovingResource(this, true);
	}
	_Moving.terminate = function() {
		this.unbindMouseEvent();
		_toggleMovingResource(this, false);
	}
	
	let isDelete = false;
	const _Delete = function() {}
	_Delete.run = function() {
		_clearCreateResource(this.magoInstance.getViewer());
		this.unbindMouseEvent();
		_toggleMovingResource(this, false);
		_toggleDeleteResource(this, true);
	}
	_Delete.delete = function(selected, silence) {
		if(!silence) {
			if(!confirm('삭제하시겠습니까?')) return;
		}
		
		
		delete objects[selected.guid];
		const magoManager = this.magoInstance.getMagoManager(); 
		if(selected.name) {
			magoManager.waterManager.removeObject(selected);	
		} else {
			magoManager.modeler.removeObject(selected);
		}
	}
	_Delete.terminate = function() {
		this.unbindMouseEvent();
		_toggleDeleteResource(this, false);
	}
	
	const _clear = function(water) {
		const viewer = water.magoInstance.getViewer();
		const magoManager = water.magoInstance.getMagoManager();
		_clearRectangle(viewer);
		_clearCreateResource(viewer);
		_toggleMovingResource(water, false);
		
		_clearObjects(magoManager);
		magoManager.setCameraMotion(true);
		magoManager.waterManager = undefined;
	}
	const _clearCreateResource = function(viewer) {
		_clearGuidePoint(viewer);
		_clearPolyline(viewer);
		leftDownCoord = undefined;
		leftUpCoord = undefined;
		leftDown = false;
	}
	const _toggleMovingResource = function(water, is) {
		isMoving = is;
		_setSelect(water, isMoving);
		_setTranslate(water, isMoving);
	}
	const _toggleDeleteResource = function(water, is) {
		isDelete = is;
		_setSelect(water, isDelete);
	}
	
	const _leftDown = function(position, text, magoManager) {
		magoManager.setCameraMotion(false);
		leftDownCoord = _screenToCartesian3(position, magoManager);
		leftDown = true;
		guidePoint.point.pixelSize = 0.1;
		guidePoint.label.text = text;
	}
	
	
	const _setSelect = function(water, active) {
		const select = water.selectInteraction;
		select.setActive(active);
		if(active) select.setTargetType(Mago3D.DataType.NATIVE);
	}
	
	const _setTranslate = function(water, active) {
		const translate = water.translateInteraction;
		translate.setActive(active);
		if(active) translate.setTargetType(Mago3D.DataType.NATIVE);
	}
	
	const _clearObjects = function(magoManager) {
		for(let id in objects) {
			magoManager.modeler.removeObject(objects[id]);
		}
		objects = {};
	}
	
	const _createGuidePoint = function(viewer, labelOption) {
		let _labelOption = {
			text: 'Rectangle의 시작점을 클릭하여 주세요.',
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
				pixelSize : 10,
				heightReference : Cesium.HeightReference.CLAMP_TO_GROUND
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
	
	const _createRectangle = function(viewer, coordinates) {
		rectangle = viewer.entities.add({
			rectangle : {
				coordinates : coordinates,
				material : Cesium.Color.DODGERBLUE.withAlpha(0.1),
				heightReference : Cesium.HeightReference.CLAMP_TO_GROUND
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
				width : 5,
				clampToGround : true
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

SmltWater.reservedWork = {
	interval : undefined,
	timeout : []
}

SmltWater.onAutoRun = function(water) {
	SmltWater.reservedWork.interval = setInterval(function() {
		SmltWater.runOnLoaded(water);
	}, 45000);
	SmltWater.runOnLoaded(water);
}

SmltWater.offAutoRun = function() {
	if(SmltWater.reservedWork.interval !== undefined) {
		clearInterval(SmltWater.reservedWork.interval);
	}
	for(var i in SmltWater.reservedWork.timeout) {
		clearTimeout(SmltWater.reservedWork.timeout[i]);
	}
}

SmltWater.runOnLoaded = function(water) {
	for(var i in SmltWater.reservedWork.timeout) {
		clearTimeout(SmltWater.reservedWork.timeout[i]);
	}
	SmltWater.reservedWork.timeout = [];
	
	water.active = false;
	water.active = true;
	
	water.mode = "area";
	water.action = water.getModeAction("area");
	
	const areaRectangle = new Cesium.Rectangle(2.210865731694948, 0.6518091533419034, 2.2117441433463463, 0.6524349961328941);
	
	water.action._createRectangle.call(water, areaRectangle);
	water.action.done.call(water, areaRectangle);
	water.action.terminate.call(water);
	
	$('#smlt-natural-water-sub button[data-type="water"]').trigger('click');
	
	water.mode = "create";
	water.action = water.getModeAction("create");
	water.createType = "water";
	
	water.action.done.call(water, new Cesium.Cartesian3(-3034075.8725879397, 4067642.109698935, 3850841.4283819026));
	water.action.terminate.call(water);
	
	SmltWater.reservedWork.timeout.push(setTimeout(function() {
		water.action = water.getModeAction("create");
		water.createType = 'polution';
		water.action.done.call(water, new Cesium.Cartesian3(-3034165.279975353, 4067895.8444491215, 3850509.3870103415));
		water.action.terminate.call(water);
	},15000));
	
	
	SmltWater.reservedWork.timeout.push(setTimeout(function() {
		water.action = water.getModeAction("create");
		water.createType = 'weir';
		water.action.done.call(water, [new Cesium.Cartesian3(-3033145.9015633147, 4067928.9218430365, 3851331.8254415216),
		new Cesium.Cartesian3(-3034202.0011983807, 4068237.234362638, 3850186.5591085064)]);
		water.action.terminate.call(water);
	},17000));
	
	SmltWater.reservedWork.timeout.push(setTimeout(function() {
		var waterObject = water.getWaterObject();
		var weirs = [];
		for(var key in waterObject) {
			var entity = waterObject[key];
			if(entity instanceof Mago3D.RenderableObject) {
				weirs.push(entity);
			}
		}
		
		water.action = water.getModeAction("delete");
		for(var i in weirs) {
			water.action.delete.call(water, weirs[i], true);
		}
		water.action.terminate.call(water);
	},27000));
}

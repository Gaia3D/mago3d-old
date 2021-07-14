const Measure = function(magoInstance) {
	this.magoInstance = magoInstance;
	
	this.drawer;
	this._type = Measure.TYPE.NONE;
	
	this.setEventHandler();
}

Object.defineProperties(Measure.prototype, {
	type : {
		get : function() {
			return this._type;
		},
		set : function(type) {
			this._type = type;
			this.setDrawer();
		}
	}
});

Measure.STATUS = {
	NOTSTART : 'notstart',
	READY : 'ready',
	NEEDSTARTPOINT : 'needstartpoint',
	NEEDLINE : 'needline',
	NEEDLASTPOINT : 'needlastpoint'
}

Measure.TYPE = {
	DISTANCE : 'distance',
	AREA : 'area',
	HEIGHT : 'height',
	NONE : 'none'
}

Measure.prototype.setEventHandler = function() {
	var self = this;
	
	var $btns = $('#toolbarWrap div.toolbox-measure button.toolbox-measure-btn');
	$btns.click(function(){
		var target = this;
		$btns.each(function(_, btn) {
			if(target !== btn) $(btn).removeClass('on');
		});
		
		$(target).toggleClass('on');
		self.type = $(target).hasClass('on') ?  $(target).data('type') : Measure.TYPE.NONE;
	});
	
	var popupObserver = new MutationObserver(function(mutations) {
		mutations.forEach(function(mutation) {
			var mutationStyle = window.getComputedStyle(mutation.target);
			if(mutationStyle.display === 'none') {
				self.destroyDrawer();
			} else {
				
			}
			return false;			
		});
	});
	
	popupObserver.observe(document.querySelector('.toolbox-measure'), { attributes: true, attributeFilter:['style'], subtree: false, childList:false, attributeOldValue:true});	
}



Measure.prototype.setDrawer = function() {
	this.destroyDrawer();
	
	if(!this.type) return;
	
	this.drawer = new Cesium.ScreenSpaceEventHandler(this.magoInstance.getViewer().canvas);
	this.drawer.result = {};
	this.drawer.status = DataLibraryDrawer.STATUS.NOTSTART;
	
	switch(this.type) {
		case Measure.TYPE.DISTANCE : {
			this.decorateDistance();
			break;
		}
		case Measure.TYPE.AREA : {
			this.decorateArea();
			break;
		}
		case Measure.TYPE.HEIGHT : {
			this.decorateHeight();
			break;
		}
	}
}

Measure.prototype.destroyDrawer = function() {
	if(!this.drawer) return;
	
	var viewer = this.magoInstance.getViewer();
	if(this.drawer.result) {
		for(var key in this.drawer.result) {
			var some = this.drawer.result[key];
			if(some instanceof Cesium.Entity) {
				viewer.entities.remove(some);
			}
			this.drawer.result[key] = undefined;
		}
	}
	
	this.drawer = this.drawer.destroy();
}

Measure.prototype.decorateArea = function() {
console.info('decorateArea');
}

Measure.prototype.decorateHeight = function() {
console.info('decorateHeight');
}

Measure.prototype.decoratePoint = function() {
	var viewer = this.magoInstance.getViewer();
	var magoManager = this.magoInstance.getMagoManager();
	var self = this;
	
	var _complete = function(positions) {
		var cartographic = positions[0];
		
		self.dataLibrary.draw(API.Converter.CesiumToMagoForGeographic(cartographic));
		
		self.setDrawer();
	}
	
	var _click = function(e){
		var point3d = API.Converter.screenCoordToMagoPoint3D(e.position.x, e.position.y, self.magoInstance.getMagoManager());
		var crts3 = API.Converter.magoToCesiumForPoint3D(point3d);
		var geographicCoord = API.Converter.Cartesian3ToMagoGeographicCoord(crts3);
		var cartographic = API.Converter.magoToCesiumForGeographic(geographicCoord);
		
		Cesium.sampleTerrain(viewer.terrainProvider, Mago3D.MagoManager.getMaximumLevelOfTerrainProvider(viewer.terrainProvider), [cartographic]).then(_complete)
	}
	var _move = function(e) {
		var point3d = API.Converter.screenCoordToMagoPoint3D(e.endPosition.x, e.endPosition.y, magoManager);
		var crts3 = API.Converter.magoToCesiumForPoint3D(point3d);
		
		var drawer = self.drawer;
		
		if(drawer.status === DataLibraryDrawer.STATUS.NOTSTART) {
			drawer.result.point = viewer.entities.add({
				point : {
					color : Cesium.Color.BLANCHEDALMOND,
					pixelSize : 10
				}
			});
			drawer.status = DataLibraryDrawer.STATUS.READY;
		}
		
		drawer.result.point.position = crts3;
	}
	this.drawer.setInputAction(_click ,Cesium.ScreenSpaceEventType.LEFT_CLICK);
	this.drawer.setInputAction(_move ,Cesium.ScreenSpaceEventType.MOUSE_MOVE);
}
Measure.STATUS = {
	NOTSTART : 'notstart',
	READY : 'ready',
	NEEDSTARTPOINT : 'needstartpoint',
	NEEDLINE : 'needline',
	NEEDLASTPOINT : 'needlastpoint',
	NEEDLASTPOINT : 'needlastpoint'
}
Measure.prototype.decorateDistance = function() {
	var viewer = this.magoInstance.getViewer();
	var magoManager = this.magoInstance.getMagoManager();
	
	var self = this;
	
	var _complete = function(crts3s) {
		var [g1,g2] = crts3s.map(function(crts3) {
			return API.Converter.Cartesian3ToMagoGeographicCoord(crts3);
		});
		
		var geocoordArray = Mago3D.GeographicCoordSegment.getArcInterpolatedGeoCoords(g1, g2, 10);
		var cartoArray = geocoordArray.map(function(geocoord){
			return API.Converter.magoToCesiumForGeographic(geocoord);
		});
		
		Cesium.sampleTerrain(viewer.terrainProvider, Mago3D.MagoManager.getMaximumLevelOfTerrainProvider(viewer.terrainProvider), cartoArray).then(function(positions) {
			self.dataLibrary.draw(positions.map(function(cartographic) {
				return API.Converter.CesiumToMagoForGeographic(cartographic);		
			}));
		
			self.setDrawer();
		});
	}
	
	var _lineCoordinate = function() {
		return [self.drawer.result.startPoint.position.getValue(), self.drawer.result.lastPosition];
	}
	
	var _click = function(e){
		var drawer = self.drawer;
		
		var point3d = API.Converter.screenCoordToMagoPoint3D(e.position.x, e.position.y, self.magoInstance.getMagoManager());
		var crts3 = API.Converter.magoToCesiumForPoint3D(point3d);
		var geographicCoord = API.Converter.Cartesian3ToMagoGeographicCoord(crts3);
		var cartographic = API.Converter.magoToCesiumForGeographic(geographicCoord);
		
		if(drawer.status === DataLibraryDrawer.STATUS.NEEDSTARTPOINT) {
			drawer.result.points[0].position = crts3;
			drawer.status = DataLibraryDrawer.STATUS.NEEDLINE;
		}
		
		if(drawer.status === DataLibraryDrawer.STATUS.NEEDLASTPOINT) {
			_complete(_lineCoordinate());
		}
	}
	var _move = function(e) {
		var point3d = API.Converter.screenCoordToMagoPoint3D(e.endPosition.x, e.endPosition.y, magoManager);
		var crts3 = API.Converter.magoToCesiumForPoint3D(point3d);
		
		var drawer = self.drawer;
		if(drawer.status === DataLibraryDrawer.STATUS.NOTSTART) {
			drawer.result.points = [];
			drawer.result.points.push(viewer.entities.add({
				point : {
					color : Cesium.Color.PALEVIOLETRED,
					pixelSize : 10,
					heightReference : Cesium.HeightReference.CLAMP_TO_GROUND
				}
			}));
			
			drawer.status = DataLibraryDrawer.STATUS.NEEDSTARTPOINT;
		}
		
		if(drawer.status === DataLibraryDrawer.STATUS.NEEDSTARTPOINT) {
			drawer.result.points[0].position = crts3;	
		}
		
		if(drawer.status === DataLibraryDrawer.STATUS.NEEDLINE) {
			drawer.result.line = viewer.entities.add({
				polyline : {
					positions : new Cesium.CallbackProperty(_lineCoordinate),
					width : 10,
					clampToGround : true
				}
			});
			drawer.status = DataLibraryDrawer.STATUS.NEEDLASTPOINT;
		}
		
		if(drawer.status === DataLibraryDrawer.STATUS.NEEDLASTPOINT) {
			drawer.result.lastPosition = crts3;
		}
	}
	this.drawer.setInputAction(_click ,Cesium.ScreenSpaceEventType.LEFT_CLICK);
	this.drawer.setInputAction(_move ,Cesium.ScreenSpaceEventType.MOUSE_MOVE);
}
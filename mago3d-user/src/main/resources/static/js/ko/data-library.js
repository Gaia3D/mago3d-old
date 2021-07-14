const DataLibraryController = function(magoInstance) {
	this.magoInstance = magoInstance;
	this.ready = false;
	
	this._dataLibraries = [];
	
	this._selected;
	this.drawer = new DataLibraryDrawer(magoInstance);
	
	this.setEventHandler();
}

Object.defineProperties(DataLibraryController.prototype, {
	selected : {
		get : function() {
			return this._selected;
		},
		set : function(selected) {
			this.drawer.dataLibrary = selected;
			this._selected = selected;
		}
	},
	dataLibraries : {
		get : function() {
			return this._dataLibraries;
		},
		set : function(dataLibraries) {
			this._dataLibraries = dataLibraries;
			this.renderHtml();
		}
	}
});

DataLibraryController.prototype.setEventHandler = function() {
	var self = this;
	//셀렉트 옆에 검색
	$('#data-library-select-group-search').click(function() {
		var groupId = $('#data-library-select-group').val();
		
		self.search(parseInt(groupId));
	});
	
	//체크박스 셀렉트
	$('#data-library-popup div.tbl-library').on('change', 'input[type="checkbox"]', function() {
		var target = this;
		var $target = $(target);
		var checked = $target.prop('checked');
		if(checked) {
			DataLibraryController.falseCheckbox(target);
			if(self.selected) {
				self.clearSelect();
			}
		}
		
		self.select(parseInt($(this).parents('tr').data('id')), checked);
	});
	
	//배치방법
	$('#data-library-popup').on('change', 'input[name="data-library-radio-draw-type"]', function() {
		self.drawer.drawType = $(this).data('type'); 
	});
	
	var popupObserver = new MutationObserver(function(mutations) {
		mutations.forEach(function(mutation) {
			if(Array.prototype.indexOf.call(mutation.target.classList, 'on') < 0) {
				DataLibraryController.falseCheckbox();
				if(self.selected) {
					self.clearSelect();
				}
				self.drawer.drawType = DataLibrary.DRAW_TYPE.NONE;				
			}
			return false;			
		});
	});
	
	popupObserver.observe(document.getElementById('data-library-popup'), { attributes: true, attributeFilter:['class'], subtree: false, childList:false, attributeOldValue:true});	
}

DataLibraryController.falseCheckbox= function(excluded) {
	$('#data-library-popup div.tbl-library input[type="checkbox"]').each(function(_, input) {
		var $checkbox = $(input);
		if(excluded !== input) $checkbox.prop('checked', false);
	});
}

DataLibraryController.prototype.load = function() {
	if(!this.ready) {
		var self = this;
		return $.ajax({
			url: "/api/data-libraries",
			type: "GET",
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(msg){
				self.ready = true;
				
				var embedded = msg._embedded;
				if(!embedded) {
					self.dataLibraries = [];
					return;	
				}
				
				var groups = {};
				
				self.dataLibraries = embedded.dataLibraries.map(function(construc_opt) {
					var dl = new DataLibrary(construc_opt, self.magoInstance);
					
					if(!groups[dl.groupId]) {
						groups[dl.groupId] = dl.groupName;
					}
					return dl;
				});
				
				var selectOption = '<option value="0">전체</option>';
				for(var groupId in groups) {
					var groupName = groups[groupId];
					selectOption +=  `<option value="${groupId}">${groupName}</option>`;
				}
				document.querySelector('#data-library-select-group').innerHTML = selectOption;
			}
		});
	}
	return;
}

DataLibraryController.prototype.select = function(dataLibraryId, checked) {
	if(!checked) {
		this.clearSelect();
		return;
	}
	
	this.selected = this.dataLibraries.find(function(dataLibrary) {
		return dataLibrary.dbId === dataLibraryId;
	});
	if(this.selected) this.selected.select = true;
}

DataLibraryController.prototype.clearSelect = function() {
	this.selected.select = false;
	this.selected = undefined;
	
	$('#data-library-draw-point').prop('disabled', true);
	$('#data-library-draw-line').prop('disabled', true);
}

DataLibraryController.prototype.search = function(dataGroupId) {
	DataLibraryController.falseCheckbox();
	if(this.selected) {
		this.clearSelect();
	}
	this.drawer.drawType = DataLibrary.DRAW_TYPE.NONE;
	
	this.dataLibraries = this.dataLibraries.map(function(dataLibrary) {
		if(!dataGroupId) {
			dataLibrary.render = true;
			return dataLibrary;
		}
		dataLibrary.render = dataLibrary.groupId === parseInt(dataGroupId);
		return dataLibrary;
	});
}

DataLibraryController.prototype.renderHtml = function() {
	var html = '';
	for(var i in this.dataLibraries) {
		var dl = this.dataLibraries[i];
		if(!dl.render) continue;
		html += dl.getRowHtml();
	}
	document.querySelector('#data-library-popup .tbl-library table tbody').innerHTML = html;
}

const DataLibrary = function(constructorOption, magoInstance) {
	this.magoInstance = magoInstance;
	
	this.render = true;
	this._select = false;
	
	this.groupId = constructorOption.dataLibraryGroupId;
	this.groupName = constructorOption.dataLibraryGroupName;
	this.dbId = constructorOption.dataLibraryId;
	this.name = constructorOption.dataLibraryName;
	this.thumbnailUrl = constructorOption.dataLibraryThumbnail;
	this.key = constructorOption.dataLibraryKey;
	this.path = constructorOption.dataLibraryPath;
	
	if(constructorOption.attributes) {
		this.attributes = JSON.parse(constructorOption.attributes);	
	}
	
	this.instanceArray = [];
	
	var buildingFolderName = `F4D_${this.key}`; 
	var magoManager = this.magoInstance.getMagoManager(); 
	magoManager.addStaticModel({
		projectId : this.key,
		projectFolderName : this.path.split(buildingFolderName)[0],
		buildingFolderName : buildingFolderName
	});
}

DataLibrary.DRAW_TYPE = {
	NONE : 'none',
	BOTH : 'both',
	POINT : 'point',
	LINE : 'line'
}

Object.defineProperties(DataLibrary.prototype, {
	select : {
		get : function() {
			return this._select;
		},
		set : function(select) {
			this._select = select;
			this.doSelect();
		}
	}
});

DataLibrary.prototype.draw = function(position) {
	if(Array.isArray(position)) {
		var self = this;
		position.forEach(function(p) {
			self.draw(p);	
		});
		return;
	}
	
	var instanceId = Mago3D.createGuid();
	
	this.magoInstance.getMagoManager().instantiateStaticModel({
		projectId : this.key,
		instanceId : instanceId,
		longitude : position.longitude,
		latitude : position.latitude,
		height : position.altitude
	});
	this.instanceArray.push(instanceId);
}

DataLibrary.prototype.doSelect = function() {
	var self = this;
	var _off = function() {
		$('#data-library-draw-point').prop('disabled', true);
		$('#data-library-draw-line').prop('disabled', true);
	}
	
	var _on = function() {
		$('#data-library-draw-point').prop('disabled', false);
		$('#data-library-draw-line').prop('disabled', false);
		
		var drawType = self.attributes.drawType;
		if(drawType === DataLibrary.DRAW_TYPE.POINT) {
			$('#data-library-draw-line').prop('disabled', true);
		} else if(drawType === DataLibrary.DRAW_TYPE.LINE) {
			$('#data-library-draw-point').prop('disabled', true);
		}
	}
	$('#data-library-draw-none').prop('checked', true).change();
	if(!this.select) {
		_off();
		return;
	}
	_on();
}

DataLibrary.prototype.getRowHtml = function() {
	var html = `<tr data-id=${this.dbId}>`;
	html += '<td><input type="checkbox" /></td>';
	html += `<td>${this.groupName}</td>`;
	html += `<td>${this.name}<span class="sumnail"><img src="/f4d/${this.thumbnailUrl}" /></span></td>`;
	html += '</tr>';
	
	return html;
}
const DataLibraryDrawer = (function() {
	const DataLibraryDrawer = function(magoInstance) {
		this.magoInstance = magoInstance;
		this.dataLibrary;
		this._drawType = DataLibrary.DRAW_TYPE.NONE;
		this.drawer;
	}
	
	DataLibraryDrawer.STATUS = {
		NOTSTART : 'notstart',
		READY : 'ready',
		NEEDSTARTPOINT : 'needstartpoint',
		NEEDLINE : 'needline',
		NEEDLASTPOINT : 'needlastpoint'
	}
	
	DataLibraryDrawer.prototype.setDrawer = function() {
		this.destroyDrawer();
		
		this.drawer = new Cesium.ScreenSpaceEventHandler(this.magoInstance.getViewer().canvas);
		this.drawer.result = {};
		this.drawer.status = DataLibraryDrawer.STATUS.NOTSTART;
		
		switch(this.drawType) {
			case DataLibrary.DRAW_TYPE.POINT : {
				this.decoratePoint();
				break;
			}
			case DataLibrary.DRAW_TYPE.LINE : {
				this.decorateLine();
				break;
			}
		}
	}
	
	DataLibraryDrawer.prototype.destroyDrawer = function() {
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
	
	DataLibraryDrawer.prototype.decoratePoint = function() {
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
	
	DataLibraryDrawer.prototype.decorateLine = function() {
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
				drawer.result.startPoint = viewer.entities.add({
					position : crts3,
					point : {
						color : Cesium.Color.BLANCHEDALMOND,
						pixelSize : 10
					}
				});
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
				drawer.result.guidePoint = viewer.entities.add({
					point : {
						color : Cesium.Color.BLANCHEDALMOND,
						pixelSize : 10
					}
				});
				
				drawer.status = DataLibraryDrawer.STATUS.NEEDSTARTPOINT;
			}
			
			if(drawer.status === DataLibraryDrawer.STATUS.NEEDSTARTPOINT) {
				drawer.result.guidePoint.position = crts3;	
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
	
	Object.defineProperties(DataLibraryDrawer.prototype, {
		drawType : {
			get : function() {
				return this._drawType;
			},
			set : function(drawType) {
				this._drawType = drawType;
				this.setDrawer();
			}
		}
	});
	
	return DataLibraryDrawer;
})();

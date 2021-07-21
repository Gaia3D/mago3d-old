var BackgroundRunningBuilding = function(magoInstance) {
	this.magoInstance = magoInstance;
	this._buildings = {};
	this._ready = false;
	
	var BUILDING_OPTION = {
		7 : {
			color : '#F9F6A9',
			minFloor : 25,
			maxFloor : 29
		},
		8 : {
			color : '#E3FAF0',
			minFloor : 25,
			maxFloor : 29
		},
		9 : {
			color : '#FFEDFB',
			minFloor : 24,
			maxFloor : 29
		},
		10 : {
			color : '#C9D2FD',
			minFloor : 29,
			maxFloor : 29
		},
		11 : {
			color : '#EE9DB0',
			minFloor : 22,
			maxFloor : 29
		}
	}
	var self = this;
	var geojsonToExtrusionBuildings = function(json) {
		var features = json.features;
		var featureCnt = features.length; 
		for(var i=0;i<featureCnt;i++) {
			var feature = features[i];
			var prop = feature.properties;
			if(!prop.name) continue;
			var type = Math.floor(parseInt(prop.name) / 100);
			if(!BUILDING_OPTION.hasOwnProperty(type)) continue;
			
			var option = BUILDING_OPTION[type];
			var geographicCoordsList = Mago3D.KoreaBuilding.geometryToGeographicCoordsList(feature.geometry);
			
			var floor = randomNumber(option.minFloor, option.maxFloor);
			var height = 5 + (floor - 1) * 3; 
			var building = new Mago3D.ExtrusionBuilding(geographicCoordsList, parseFloat(height), {
				color : Mago3D.Color.fromHexCode(option.color),
				renderWireframe : false,
				heightReference : Mago3D.HeightReference.CLAMP_TO_GROUND,
				divideLevel : true
			});
			building.prop = prop;
			 
			self.magoInstance.getMagoManager().modeler.addObject(building);
			
			if(!self.buildings[type]) self.buildings[type] = [];
			self.buildings[type].push(building);
		}
		self.ready = true;
	}
	
	$.getJSON('/sample/json/baegot.geojson').then(geojsonToExtrusionBuildings);
}

BackgroundRunningBuilding.prototype.run = function() {
	var keys = [0];
	keys.push.apply(keys, Object.keys(this._buildings).map(function(key) {
		return parseInt(key);
	}));
	
	var self = this;
	var effectManager = self.magoInstance.getMagoManager().effectsManager;
	var index = -1;
	setInterval(function() {
		index = (index === keys.length - 1) ? 0 : index+1;
		
		var key = keys[index];
		if(!self.buildings[key]) {
			for(var i in self.buildings) {
				var buildingArray = self.buildings[i];
				buildingArray.forEach(function(bld) {
					bld.attributes.isVisible = false;
				});
			}
		} else {
			var buildingArray = self.buildings[key];
			buildingArray.forEach(function(bld) {
				bld.attributes.isVisible = true;
				
				effectManager.addEffect(bld.guid, new Mago3D.Effect({
					effectType      : "zBounceSpring",
					durationSeconds : 0.5
				}))
			});
		}
		
	}, 2000);
}

Object.defineProperties(BackgroundRunningBuilding.prototype, {
	buildings : {
		get : function() {
			return this._buildings;
		},
		set : function(buildings) {
			this._buildings = buildings;
		}
	},
	ready : {
		get : function() {
			return this._ready;
		},
		set : function(ready) {
			this._ready = ready;
			if(this._ready) this.run();
		} 
	}
});
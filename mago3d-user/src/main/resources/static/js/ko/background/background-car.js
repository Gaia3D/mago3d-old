var BackgroundRunningCar = function(magoInstance) {
	this.magoInstance = magoInstance;
	this._routes;
	this.runningCar = {};
	this.projectId = 'backgroudRunningcar'
	
	var magoManager = this.magoInstance.getMagoManager(); 
	magoManager.addStaticModel({
		projectId : this.projectId,
		projectFolderName : '/infra/data/car',
		buildingFolderName : 'F4D_Irisbus'
	});
	
	var that = this;
	magoManager.on(Mago3D.MagoManager.EVENT_TYPE.ANIMATIONEND, function(e){
		var car = e.f4d;
		var carInstanceId = car.data.nodeId;
		
		that.animation(carInstanceId);
	});
	
	$.when(
			$.getJSON('/sample/json/1.geojson'),
			$.getJSON('/sample/json/2.geojson'),
			$.getJSON('/sample/json/3.geojson')
	).then(function(a,b,c) {
		var arr = [];
		arr.push.apply(arr, extractCoordinates(a[0]));
		arr.push.apply(arr, extractCoordinates(b[0]));
		arr.push.apply(arr, extractCoordinates(c[0]));
		
		that.routes = arr;
		
		function extractCoordinates (featureCollection) {
			var coordinatesArray = [];
			var features = featureCollection.features;
			for(var i in features) {
				var feature = features[i];
				var geometry = feature.geometry.coordinates;
				coordinatesArray.push(geometry[0]);
			}
			return coordinatesArray;
		}
	});
}

BackgroundRunningCar.prototype.run = function() {
	var that = this;
	var magoManager = this.magoInstance.getMagoManager();
	var _runCarDrive = function(positions) {
		var geographicCoordsArray = positions.map(function(position) {
			return new Mago3D.GeographicCoord(API.Converter.radToDeg(position.longitude), 
				API.Converter.radToDeg(position.latitude),  position.height)
		});
		var carInstanceId = Mago3D.createGuid();
		
		magoManager.instantiateStaticModel({
			projectId : 'backgroudRunningcar',
			instanceId : carInstanceId,
			longitude : geographicCoordsArray[0].longitude,
			latitude : geographicCoordsArray[0].latitude,
			height : geographicCoordsArray[0].altitude
		});
		
		that.runningCar[carInstanceId] = {geographicCoordsArray: geographicCoordsArray, direction : false};
		that.animation(carInstanceId);
	}
	var terrainProvider = this.magoInstance.getViewer().terrainProvider;
	var maxZoom = Mago3D.MagoManager.getMaximumLevelOfTerrainProvider(terrainProvider);
	
	for(var i in this.routes) {
		var routes = this.routes[i];
		var cartographic = routes.map(function(point) {
			return Cesium.Cartographic.fromDegrees(point[0], point[1]);
		});  
		Cesium.sampleTerrain(terrainProvider, maxZoom, cartographic).then(_runCarDrive)
	}
}

BackgroundRunningCar.prototype.animation = function(nodeId) {
	
	var runningInfo = this.runningCar[nodeId];
	var copyCoordsArray = runningInfo.geographicCoordsArray.slice();
	var geographicCoordsArray = runningInfo.direction ? copyCoordsArray.reverse() : copyCoordsArray;
		
	runningInfo.direction = !runningInfo.direction;
		
	var animationOption = {
		animationType                : Mago3D.CODE.animationType.PATH,
		path                         : new Mago3D.Path3D(geographicCoordsArray),
		linearVelocityInMetersSecond : randomNumber(10,40)*2,
		autoChangeRotation           : true
	};
	
	this.magoInstance.getMagoManager().changeLocationAndRotation(this.projectId, nodeId, undefined, undefined, undefined, undefined, undefined, undefined, animationOption);
}

Object.defineProperties(BackgroundRunningCar.prototype, {
	routes : {
		get : function() {
			return this._routes;
		},
		set : function(routes) {
			this._routes = routes;
			this.run();
		}
	}
});
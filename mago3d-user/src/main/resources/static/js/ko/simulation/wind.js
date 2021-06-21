const SmltWind = function(magoInstance) {
	this.magoInstance = magoInstance;
	this._active = false;
	this.position = {
		destination : Cesium.Cartesian3.fromDegrees(127.00268356518497, 37.433816500134114, 1000),
		orientation : {
			heading : 0.004374180535735128,
			pitch : -0.6638900518113893,
			roll : 0.00001817433718898087
		}
	}
}

Object.defineProperties(SmltWind.prototype, {
	active : {
		get : function () {
			return this._active;
		},
		set : function (active) {
			if(active) {
				this.run();
			} else {
				this.clear();
			}
			this._active = active;
		}
	}
});

SmltWind.prototype.initCamera = function() {
	this.magoInstance.getViewer().camera.flyTo(this.position);
}

SmltWind.prototype.run = function() {
	this.initCamera();
	const orgin = this.magoInstance;
	MapControll.divideMap(function(e) {
		const divided = e;
		
		$.ajax({
			url: "/api/wind",
			type: "GET",
			data: { direction  : 'n', causality : 'before'},
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(geojson){
				divided.getMagoManager().weatherStation.addWind(geojson);
			}
		});
		
		$.ajax({
			url: "/api/wind",
			type: "GET",
			data: { direction  : 'n', causality : 'after'},
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(geojson){
				orgin.getMagoManager().weatherStation.addWind(geojson);
			}
		});
	});
}

SmltWind.prototype.clear = function() {
	this.magoInstance.getMagoManager().weatherStation.deleteWindVolumes();
	if(MAGO3D_DIVIDE_INSTANCE) {
		MAGO3D_DIVIDE_INSTANCE.getMagoManager().weatherStation.deleteWindVolumes();
	}
	MapControll.undivideMap();
}
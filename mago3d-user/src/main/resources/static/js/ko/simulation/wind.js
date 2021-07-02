const SmltWind = function(magoInstance) {
	this.magoInstance = magoInstance;
	this._active = false;
	this.position = {
		destination : Cesium.Cartesian3.fromDegrees(126.69918823242188, 37.34914779663086, 1000),
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
	$.ajax({
		url: "/api/wind/date",
		type: "GET",
		data: { date : '2019100708' },
		dataType: "json",
		headers: {"X-Requested-With": "XMLHttpRequest"},
		success: function(geojson){
			orgin.getMagoManager().weatherStation.addWind(geojson);
		}
	});

	/*
	MapControll.divideMap(function(e) {
		const divided = e;
		$.ajax({
			url: "/api/wind/date",
			type: "GET",
			data: { date : '2019090807' },
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(geojson){
				divided.getMagoManager().weatherStation.addWind(geojson);
			}
		});
		$.ajax({
			url: "/api/wind/date",
			type: "GET",
			data: { date : '2019090700' },
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(geojson){
				orgin.getMagoManager().weatherStation.addWind(geojson);
			}
		});
	});
	*/
}

SmltWind.prototype.clear = function() {
	this.magoInstance.getMagoManager().weatherStation.deleteWindVolumes();
	if(MAGO3D_DIVIDE_INSTANCE) {
		MAGO3D_DIVIDE_INSTANCE.getMagoManager().weatherStation.deleteWindVolumes();
	}
	MapControll.undivideMap();
}
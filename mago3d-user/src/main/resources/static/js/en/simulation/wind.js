const SmltWind = function(magoInstance) {
	this.magoInstance = magoInstance;
	this._active = false;
	this._date = '2019100700';
	this.position = {
		destination : Cesium.Cartesian3.fromDegrees(126.72588055371104, 37.37429635539057, 6202.398130875977),
		orientation : {
			heading : 3.128764647505197,
			pitch : -1.4599890461605018,
			roll : 3.141957219539732
		}
	}
	this.playId = [];

	var _this = this;
	// 시간선택
	$('#smlt-natural-wind-select').change(function() {
		var selected = $(this).find("option:selected").text();
		$('#smlt-natural-time').text(selected);
		var date = $(this).val();
		if (!date) return;
		_this.date = date;
	});
	// 재생
	$('#smlt-natural-wind-play').click(function() {
		if ($(this).hasClass('on')) {
			// 정지
			_this.stop();
			$(this).text("재생");
		} else {
			// 재생
			_this.play();
			$(this).text("정지");
		}
		$(this).toggleClass('on');
	});

}

Object.defineProperties(SmltWind.prototype, {
	active : {
		get : function () {
			return this._active;
		},
		set : function (active) {
			if(active) {
				this.initCamera();
				this.run();
			} else {
				this.clear();
			}
			this._active = active;
		}
	},
	date : {
		get : function () {
			return this._date;
		},
		set : function (date) {
			this._date = date;
			this.clear();
			this.run();
		}
	}
});

SmltWind.prototype.initCamera = function() {
	this.magoInstance.getViewer().camera.flyTo(this.position);
}

SmltWind.prototype.load = function(instance, date) {
	$.ajax({
		url: "/api/wind/date",
		type: "GET",
		data: {date: date},
		dataType: "json",
		headers: {"X-Requested-With": "XMLHttpRequest"},
		success: function (geojson) {
			instance.getMagoManager().weatherStation.addWind(geojson);
		}
	});
}

SmltWind.prototype.run = function() {
	const orgin = this.magoInstance;
	const _this = this;

	// 임시
	const compareDate = parseInt(_this.date) + 10000;

	if (MapControll.divided) {
		//const magoMap = new mapInit(MAGO3D_DIVIDE_INSTANCE, MAGO.baseLayers, MAGO.policy);
		//magoMap.initLayer(false);
		_this.load(MAGO3D_DIVIDE_INSTANCE, _this.date);
		_this.load(orgin, compareDate);
	} else {
		MapControll.divideMap(function (divided) {
			//const magoMap = new mapInit(divided, MAGO.baseLayers, MAGO.policy);
			//magoMap.initLayer(false);
			_this.load(divided, _this.date);
			_this.load(orgin, compareDate);
		});
	}

/*
	$.ajax({
		url: "/api/wind/date",
		type: "GET",
		data: { date : '2019110700' },
		dataType: "json",
		headers: {"X-Requested-With": "XMLHttpRequest"},
		success: function(geojson){
			orgin.getMagoManager().weatherStation.addWind(geojson);
		}
	});
*/

}

SmltWind.prototype.clear = function () {
	this.magoInstance.getMagoManager().weatherStation.deleteWindVolumes();
	if (MAGO3D_DIVIDE_INSTANCE) {
		MAGO3D_DIVIDE_INSTANCE.getMagoManager().weatherStation.deleteWindVolumes();
		MapControll.undivideMap();
	}
	//MapControll.undivideMap();
}

SmltWind.prototype.play = function() {
	var _this = this;
	$('#smlt-natural-wind-select option').each(function(idx) {
		const date = this.value;
		if (!date) return true;
		const text = this.text;
		_this.playId[idx] = setTimeout(function() {
			$('#smlt-natural-time').text(text);
			_this.date = date;
			_this.clear();
			_this.run();
		}, 5000 * idx);
	});
}

SmltWind.prototype.stop = function() {
	if (this.playId.length > 0) {
		for (const i in this.playId) {
			clearTimeout(this.playId[i]);
		}
	}
}
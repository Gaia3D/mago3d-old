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

SmltWind.prototype.run = function() {
	const orgin = this.magoInstance;
	$.ajax({
		url: "/api/wind/date",
		type: "GET",
		data: { date : this.date },
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
			data: { date : '2019090802' },
			dataType: "json",
			headers: {"X-Requested-With": "XMLHttpRequest"},
			success: function(geojson){
				divided.getMagoManager().weatherStation.addWind(geojson);
			}
		});
		$.ajax({
			url: "/api/wind/date",
			type: "GET",
			data: { date : '2019090802' },
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
		}, 3000 * idx);
	});
}

SmltWind.prototype.stop = function() {
	if (this.playId.length > 0) {
		for (const i in this.playId) {
			clearTimeout(this.playId[i]);
		}
	}
}
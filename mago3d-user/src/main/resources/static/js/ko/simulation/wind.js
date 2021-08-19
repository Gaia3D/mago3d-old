const SmltWind = function(magoInstance) {
	this.magoInstance = magoInstance;
	this._active = false;
	this._date = '2019100700';
	//this._date = '201705020000';
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
				this.run(true);
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
			//this.clear();
			this.run(false);
		}
	}
});

/**
 * geojson 영역으로 이동
 * @param geojson
 */
SmltWind.prototype.initCamera = function(instance, geojson) {
	const pointArray = [];
	const bbox = geojson.features[0].bbox;
	const minX = bbox[0];
	const minY = bbox[1];
	const maxX = bbox[3];
	const maxY = bbox[4];
	pointArray[0] = Mago3D.ManagerUtils.geographicCoordToWorldPoint(minX, minY, 0);
	pointArray[1] = Mago3D.ManagerUtils.geographicCoordToWorldPoint(maxX, maxY, 0);
	instance.getMagoManager().flyToBox(pointArray);
	//this.magoInstance.getViewer().camera.flyTo(this.position);
}

/**
 * wind 시뮬레이션 데이터 로드
 * @param instance
 * @param date
 */
SmltWind.prototype.load = function(instance, date, flag) {
	const _this = this;
	instance.getMagoManager().weatherStation.deleteWindVolumes();
	$.ajax({
		url: "/api/wind/date",
		type: "GET",
		data: {date: date},
		dataType: "json",
		headers: {"X-Requested-With": "XMLHttpRequest"},
		success: function (geojson) {
			instance.getMagoManager().weatherStation.addWind(geojson);
			if (flag) {
				_this.initCamera(instance, geojson);
			}
		}
	});
}

/**
 * wind 시뮬레이션 실행
 */
SmltWind.prototype.run = function(flag) {
	const orgin = this.magoInstance;
	const _this = this;

	// 바람장 단일
	// _this.load(orgin, _this.date, flag);

	// 바람장 비교

	const compareDate = parseInt(_this.date) + 10000;
	if (MapControl.divided) {
		//const magoMap = new mapInit(MAGO3D_DIVIDE_INSTANCE, MAGO.baseLayers, MAGO.policy);
		//magoMap.initLayer(false);
		_this.load(MAGO3D_DIVIDE_INSTANCE, _this.date, flag);
		_this.load(orgin, compareDate, flag);
	} else {
		MapControl.divideMap(function (divided) {
			//const magoMap = new mapInit(divided, MAGO.baseLayers, MAGO.policy);
			//magoMap.initLayer(false);
			_this.load(divided, _this.date, flag);
			_this.load(orgin, compareDate, flag);
		});
	}

}

/**
 * wind 시뮬레이션 초기화
 */
SmltWind.prototype.clear = function () {
	this.magoInstance.getMagoManager().weatherStation.deleteWindVolumes();
	if (MAGO3D_DIVIDE_INSTANCE) {
		MAGO3D_DIVIDE_INSTANCE.getMagoManager().weatherStation.deleteWindVolumes();
		MapControl.undivideMap();
	}
}

/**
 * wind 시뮬레이션 재생
 */
SmltWind.prototype.play = function() {
	var _this = this;
	$('#smlt-natural-wind-select option').each(function(idx) {
		const date = this.value;
		if (!date) return true;
		const text = this.text;
		_this.playId[idx] = setTimeout(function() {
			$('#smlt-natural-time').text(text);
			_this.date = date;
			//_this.clear();
			_this.run();
		}, 5000 * idx);
	});
}

/**
 * wind 시뮬레이션 정지
 */
SmltWind.prototype.stop = function() {
	if (this.playId.length > 0) {
		for (const i in this.playId) {
			clearTimeout(this.playId[i]);
		}
	}
}
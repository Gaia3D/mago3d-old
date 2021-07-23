const ArchInfoController = function(archInfoList, magoManager) {
	this.hash = {};
	this.magoManager = magoManager;
	
	//temp
	this.background = [];
	
	let html = '';
	for(let i in archInfoList) {
		let archInfo = archInfoList[i];
		let guid = archInfo.buildingMaster.guid;
		this.hash[guid] = archInfo;
		html += `<tr data-id="${guid}">`; 
		html += `<td>${archInfo.name}</td>`;
		html += '<td>';
		html += '<button class="master-plan-color" style=" height:20px; width:19px;background: white url(/images/ko/ic_toolbox.png) no-repeat -376px -12px;"></button>';
		html += `<input type="color" value="${archInfo.color.getHexCode()}" style="width: 1px;height: 1px;right: 70px;top: 65px;visibility: hidden;"/>`;
		html += '</td>'; 
		html += '<td>';
		html += '<div class="data-go">';								
		html += '<label class="switch">';
		html += `<input type="checkbox" ${archInfo.buildingMaster.show?"checked":""}/>`;
		html += '<span class="slider"></span>';
		html += '</label>';
		html += '<a class="move" href=""></a>';
		html += '</div>';
		html += '</td>';
		html += '</tr>';
	}
	
	document.querySelector('#master-plan-popup .tbl-layer table tbody').insertAdjacentHTML('beforeend', html);
	
	var self = this;
	var geojsonToExtrusionBuildings = function(arrayBuffer) {
		var json = Mago3D.geobufDecoder(arrayBuffer)
		var features = json.features;
		var featureCnt = features.length; 
		for(var i=0;i<featureCnt;i++) {
			var feature = features[i];
			var geographicCoordsList = Mago3D.KoreaBuilding.geometryToGeographicCoordsList(feature.geometry);
			
			if(Mago3D.GeographicCoordsList.isClockwise(geographicCoordsList.geographicCoordsArray)) geographicCoordsList.geographicCoordsArray.reverse();
			
			var floor = randomNumber(1, 40);
			var height = 5 + (floor - 1) * 3; 
			var building = new Mago3D.ExtrusionBuilding(geographicCoordsList, parseFloat(height), {
				color : new Mago3D.Color(159/255, 172/255, 179/255 ,1),
				renderWireframe : false,
				heightReference : Mago3D.HeightReference.CLAMP_TO_GROUND
			});
			 
			self.magoManager.modeler.addObject(building);
			self.background.push(building);
		}
	}
	
	
	$.ajax({
		responseType: 'arraybuffer',
		dataType: 'binary',
		url : '/sample/json/around_building.pbf',
		xhrFields: {
		    responseType: 'arraybuffer'
		}
	}).then(geojsonToExtrusionBuildings);
}

ArchInfoController.prototype.getArchInfoById = function(id) {
	return this.hash[id];
}

ArchInfoController.createMockData = function(magoManager) {
	let getUrl = function(name) {
		return `http://222.122.118.28:9991/${name}/15/`;
	}
	let archInfoList = [];
	const info = [
		{name : '강원', format : 'pbf', option:{show:false}},
		{name : '세종', format : 'pbf', option:{show:false}},
		{name : '경기', format : 'geojson', option:{show:true}},
		{name : '경남', format : 'geojson', option:{show:false}},
		{name : '경북', format : 'geojson', option:{show:false}},
		{name : '광주', format : 'geojson', option:{show:false}},
		{name : '대구', format : 'geojson', option:{show:false}},
		{name : '대전', format : 'geojson', option:{show:false}},
		{name : '부산', format : 'geojson', option:{show:false}},
		{name : '서울', format : 'geojson', option:{show:false}},
		{name : '울산', format : 'geojson', option:{show:false}},
		{name : '인천', format : 'geojson', option:{show:false}},
		{name : '전남', format : 'geojson', option:{show:false}},
		{name : '전북', format : 'geojson', option:{show:false}},
		{name : '제주', format : 'geojson', option:{show:false}},
		{name : '충남', format : 'geojson', option:{show:false}},
		{name : '충북', format : 'geojson', option:{show:false}}
	]
	
	for(let i in info) {
		let archInfo = info[i];
		archInfo.url = getUrl(archInfo.name);
		//archInfo.option.color = new Mago3D.Color(200/255, 200/255, 200/255 ,1);
		archInfo.option.color = new Mago3D.Color(159/255, 172/255, 179/255 ,1);
		archInfoList.push(new ArchInfo(archInfo, magoManager));
	}
	
	return archInfoList;
}

const ArchInfo = function(contructionOption, magoManager) {
	this.name = contructionOption.name;
	this.buildingMaster = magoManager.addKoreaBuildingMaster(contructionOption.url, contructionOption.format, contructionOption.option);
}

Object.defineProperties(ArchInfo.prototype, {
	show : {
		get : function() {
			return this.buildingMaster.show;
		},
		set : function(show) {
			console.info(this);
			this.buildingMaster.show = show;
		}
	},
	color : {
		get : function() {
			return this.buildingMaster.color;
		},
		set : function(color) {
			this.buildingMaster.color = color;
		}
	}
});
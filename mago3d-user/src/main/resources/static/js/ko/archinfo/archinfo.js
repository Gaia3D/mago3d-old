const ArchInfoController = function(archInfoList, magoManager) {
	this.hash = {};
	this.magoManager = magoManager;
	
	let html = '';
	for(let i in archInfoList) {
		let archInfo = archInfoList[i];
		let guid = archInfo.buildingMaster.guid;
		this.hash[guid] = archInfo;
		 
		html += '<tr>'; 
		html += `<td>${archInfo.name}</td>`;
		html += '<td>molayo</td>'; 
		html += '<td>';
		html += `<div class="data-go" data-id="${guid}">`;								
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
}

ArchInfoController.prototype.getArchInfoById = function(id) {
	return this.hash[id];
}

ArchInfoController.createMockData = function(magoManager) {
	let getUrl = function(name) {
		return `http://192.168.10.11:29090/${name}/15/`;
	}
	let archInfoList = [];
	const info = [
		{name : '강원', format : 'pbf'},
		{name : '세종', format : 'pbf'},
		{name : '경기', format : 'geojson'},
		{name : '경남', format : 'geojson'},
		{name : '경북', format : 'geojson'},
		{name : '광주', format : 'geojson'},
		{name : '대구', format : 'geojson'},
		{name : '대전', format : 'geojson'},
		{name : '부산', format : 'geojson'},
		{name : '서울', format : 'geojson'},
		{name : '울산', format : 'geojson'},
		{name : '인천', format : 'geojson'},
		{name : '전남', format : 'geojson'},
		{name : '전북', format : 'geojson'},
		{name : '제주', format : 'geojson'},
		{name : '충남', format : 'geojson'},
		{name : '충북', format : 'geojson'}
	]
	
	for(let i in info) {
		let archInfo = info[i];
		archInfo.url = getUrl(archInfo.name);
		archInfo.option = {};
		archInfo.option.show = false;
		//archInfo.option.color = new Mago3D.Color(151/255, 172/255, 180/255 ,1);
		//archInfo.option.color = new Mago3D.Color(115/255, 146/255, 160/255 ,1);
		//archInfo.option.color = new Mago3D.Color(168/255, 185/255, 192/255 ,1);
		//archInfo.option.color = new Mago3D.Color(93/255, 96/255, 136/255 ,1);
		archInfo.option.color = new Mago3D.Color(125/255, 125/255, 125/255 ,1);
		archInfoList.push(new ArchInfo(archInfo, magoManager));
	}
	
	return archInfoList;
}

const ArchInfo = function(contructionOption, magoManager) {
	this.name = contructionOption.name;
	this.buildingMaster = magoManager.addKoreaBuildingMaster(contructionOption.url, contructionOption.format, contructionOption.option);
	// new Mago3D.KoreaBuildingMaster(contructionOption.url,contructionOption.format) 
}

Object.defineProperties(ArchInfo.prototype, {
	show : {
		get : function() {
			return this.buildingMaster.show;
		},
		set : function(show) {
			this.buildingMaster.show = show;
		}
	}
});
const DataLibraryController = function(magoInstance) {
	this.magoInstance = magoInstance;
	this.ready = false;
	this._dataLibraries = [];
	
	this.setEventHandler();
}

Object.defineProperties(DataLibraryController.prototype, {
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
	$('#data-library-popup div.tbl-library').on('click', 'input[type="checkbox"]', function() {
		var clicked = this;
		var $clicked = $(clicked);
		var checked = $clicked.prop('checked');
		if(checked) {
			$('#data-library-popup div.tbl-library input[type="checkbox"]').each(function(idx, input) {
				var $checkbox = $(input);
				if(clicked !== input) $checkbox.prop('checked', false);
			});	
		}
		
		self.select(parseInt($(this).parents('tr').data('id')), checked);
	});
}

DataLibraryController.prototype.load = function() {
	if(!this.ready) {
		var self = this;
		$.ajax({
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
					//if(dataGroupbla && dl.groupId !== dataGroupbla) dl.render = false;
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
}

DataLibraryController.prototype.select = function(dataLibraryId, checked) {
	if(!checked) {
		this.selected.select = false;
		this.selected = undefined;
		return;
	}
	
	this.selected = this.dataLibraries.find(function(dataLibrary) {
		return dataLibrary.dbId === dataLibraryId;
	});
	if(this.selected) this.selected.select = true;
}

DataLibraryController.prototype.search = function(dataGroupId) {
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
	
	var buildingFolderName = `F4D_${this.key}`; 
	var magoManager = this.magoInstance.getMagoManager(); 
	magoManager.addStaticModel({
		projectId : this.key,
		projectFolderName : this.path.split(buildingFolderName)[0],
		buildingFolderName : buildingFolderName
	});
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


DataLibrary.prototype.doSelect = function() {
	console.info(`select ${this.name}`);
}

DataLibrary.prototype.getRowHtml = function() {
	var html = `<tr data-id=${this.dbId}>`;
	html += '<td><input type="checkbox" /></td>';
	html += `<td>${this.groupName}</td>`;
	html += `<td>${this.name}<span class="sumnail"><img src="/f4d/${this.thumbnailUrl}" /></span></td>`;
	html += '</tr>';
	
	return html;
}
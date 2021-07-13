const DataLibraryController = function() {
	this.ready = false;
	this._dataLibrarys = [];
}

Object.defineProperties(DataLibraryController.prototype, {
	dataLibrarys : {
		get : function() {
			return this._dataLibrarys;
		},
		set : function(dataLibrarys) {
			this._dataLibrarys = dataLibrarys;
			this.renderHtml();
		}
	}
});

DataLibraryController.prototype.search = function(dataGroupbla) {
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
					self.dataLibrarys = [];
					return;	
				}
				
				self.dataLibrarys = embedded.dataLibraries.map(function(construc_opt) {
					var dl = new DataLibrary(construc_opt);
					//if(dataGroupbla && dl.groupId !== dataGroupbla) dl.render = false;
					return dl;
				});
			}
		});
	}
}

DataLibraryController.prototype.renderHtml = function() {
	var html = '';
	for(var i in this.dataLibrarys) {
		var dl = this.dataLibrarys[i];
		if(!dl.render) continue;
		html += dl.getRowHtml();
	}
	document.querySelector('#data-library-popup .tbl-library table tbody').innerHTML = html;
}

const DataLibrary = function(constructorOption) {
	this.render = true;
	
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
}

DataLibrary.prototype.getRowHtml = function() {
	var html = '<tr>';
	html += '<td><input type="checkbox" /></td>';
	html += `<td>${this.groupName}</td>`;
	html += `<td>${this.name}<span class="sumnail"><img src="/f4d/${this.thumbnailUrl}" /></span></td>`;
	html += '</tr>';
	
	return html;
}
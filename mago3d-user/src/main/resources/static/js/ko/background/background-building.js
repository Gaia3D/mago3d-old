var BackgroundRunningBuilding = function(magoInstance) {
	this.magoInstance = magoInstance;
	
	$.getJSON('/sample/json/baegot.geojson').then(function(json){
		//console.info(json)
	});
}

Object.defineProperties(BackgroundRunningBuilding.prototype, {
	/*routes : {
		get : function() {
			return this._routes;
		},
		set : function(routes) {
			this._routes = routes;
			this.run();
		}
	}*/
});
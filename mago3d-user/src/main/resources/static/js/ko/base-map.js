/**
 * @param {Cesium.Viewer} viewer
 */
var BaseMapController = function(viewer) {
	
	this.ID_PREFIX = 'toolbox-map-';
	this.baseMapElementContainer = document.querySelector('#toolbarWrap div.toolbox-map');
	this.toggleClassName = 'on';
	this.viewer = viewer;
	
	var imageryLayers = this.viewer.imageryLayers;
	var defaultBaseLayer = imageryLayers._layers.filter(function(l) {
		return l.isBaseLayer();
	})[0];
	defaultBaseLayer.baseMapName = 'satellite';
	
	var osmLayer = new Cesium.ImageryLayer(new Cesium.OpenStreetMapImageryProvider({
		url : 'https://a.tile.openstreetmap.org/',
		credit: '<a href="https://www.openstreetmap.org/copyright" target="_blank" style="">OpenStreetMap 기여자</span></a>'
	}), {show : false})
	osmLayer.baseMapName = 'normal';
	
	imageryLayers.add(osmLayer, 1);
	
	this.layers = [defaultBaseLayer, osmLayer];
	this.toggle(defaultBaseLayer, 0 ,true);
	
	this.setEventListener(); 
}

BaseMapController.prototype.toggle = function(layer,index,show) {
	var onName = layer.baseMapName;
	if(!onName) return false;
	var mapElement = this.baseMapElementContainer.children.namedItem(`${this.ID_PREFIX}${onName}`);
	
	show ? this.on(mapElement) : this.off(mapElement);
}
BaseMapController.prototype.off = function(elem) {
	var check = new RegExp(`(\\s|^)${this.toggleClassName}(\\s|$)`);
	elem.className = elem.className.replace(check, " ").trim();
}
BaseMapController.prototype.on = function(elem) {
	var addtext = this.toggleClassName;
	if(elem.className.length > 0 ) {
		addtext = ' ' + addtext;
	}
	elem.className += addtext;
}

BaseMapController.prototype.setEventListener = function() {
	var viewer = this.viewer; 
	viewer.imageryLayers.layerShownOrHidden.addEventListener(this.toggle.bind(this));
	var mapElementList = this.baseMapElementContainer.children;
	for(var i=0,len=mapElementList.length;i<len;i++) {
		var mapElement = mapElementList.item(i);
		mapElement.addEventListener('click', function() {
			var thisType = this.dataset.type;
			viewer.imageryLayers._layers.forEach(function(ly){
				if(ly.hasOwnProperty('baseMapName')) {
					ly.show = (ly.baseMapName === thisType) ? true : false;  
				}
			});
		}, false);
	}
}
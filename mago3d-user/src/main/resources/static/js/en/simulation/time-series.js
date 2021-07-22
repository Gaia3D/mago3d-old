const SmltTimeSeries = function(constructorOption, magoInstance) {
	this.magoInstance = magoInstance;
	this.name = constructorOption.name;
	this._active = false;
	this.targetGroupId = constructorOption.targetGroupId;
	this.position = constructorOption.position;
	this.initData = constructorOption.initDataFunction.bind(this);
	
	let self = this;
	
	const _initSlider = function(slider_opt) {
		const PREFIX = 'smlt-construction-';
		const SUFFIX = '-slider'
		const slider = noUiSlider.create(document.getElementById(PREFIX + self.name + SUFFIX), slider_opt);
 		slider.on('update', constructorOption.updateFunction.bind(self));
		return slider;
	}
	
	this.slider = _initSlider(constructorOption.sliderOption); 
}

Object.defineProperties(SmltTimeSeries.prototype, {
	active : {
		get : function () {
			return this._active;
		},
		set : function (active) {
			if(active) {
				this.initCamera();
			} else {
				this.initData();
			}
			this._active = active;
		}
	}
});

SmltTimeSeries.prototype.initCamera = function() {
	this.magoInstance.getViewer().camera.flyTo(this.position);
}

SmltTimeSeries.addEffect = function (id, bounceScnd, borningScnd, magoInstance) {
	magoInstance.getMagoManager().effectsManager.addEffect(id, new Mago3D.Effect({
		effectType      : "zBounceSpring",
		durationSeconds : bounceScnd
	}));
	magoInstance.getMagoManager().effectsManager.addEffect(id, new Mago3D.Effect({
		effectType      : 'borningLight',
		durationSeconds : borningScnd
	}));
}
	
const parseInt10 = function(value) {
	return parseInt(value, 10);
}
	
	
SmltTimeSeries.SMLT_TIMESERIES = {
	STEP : {
		name : 'step',
		position : {
			destination : Cesium.Cartesian3.fromDegrees(126.72432435118692, 37.35825647132306, 571.9734316882036),
			orientation : {
				heading : 5.978454587403672,
				pitch : -0.5843758071400851,
				roll : 6.282018611977811
			}
		},
		sliderOption : {
		    start: [1,1],
		    connect: [false,true,false],
			step : 1,
		    range: {
		        'min': 1,
		        'max': 4
		    },
			format : {
				to : parseInt10,
				from : parseInt10
			},
			pips: {
		        mode: 'steps',
		        stepped: false,
				values : [1, 2, 3, 4],
				format : {
					to : function(value) {
						return value + 'Step';
					}
				}
		    }
		},
		targetGroupId : '10000',
		updateFunction : function(e) {
			let self = this;
			const caseInRange = function(f4d) {
				if(!f4d.getVisible()) {
					f4d.setVisible(true);
					SmltTimeSeries.addEffect(f4d.guid, 0.8, 1.0, self.magoInstance);
				}
				let f4dData = f4d.data; 
				f4dData.isColorChanged = true;
				f4dData.aditionalColor = new Mago3D.Color();
				switch(f4dData.attributes.group) {
					case 0 : f4dData.aditionalColor.setRGB(129/255,193/255,71/255);break;
					case 1 : f4dData.aditionalColor.setRGB(255/255,196/255,89/255);break;
					case 2 : f4dData.aditionalColor.setRGB(150/255,123/255,220/255);break;
					case 3 : f4dData.aditionalColor.setRGB(84/255,193/255,240/255);break;
				}
			}
			
			const caseOutRange = function(f4d) {
				let f4dData = f4d.data; 
				f4dData.aditionalColor = undefined;
				f4dData.isColorChanged = false;
				f4d.setVisible(false);
			}
			
			if(this.active) {
				let dataGroup = this.magoInstance.getF4dController().getF4dGroup(this.targetGroupId);
				let [sVal, eVal] = e.map(function(v){return v-1});
				
				for(let i in dataGroup) {
					let node = dataGroup[i];
					
					if(!(node instanceof Mago3D.Node) || !node.data.attributes.isPhysical) {
						continue;
					}
					
					let groupNumber = node.data.attributes.group;
					
					if(groupNumber === undefined || groupNumber === null) {
						continue;
					}
					
					if(!(eVal >= groupNumber && sVal <= groupNumber)) {
						caseOutRange(node);
						continue;
						
					}
					
					caseInRange(node);
				}
			}
		},
		initDataFunction : function() {
			if(this.slider) {
				this.slider.set([1, 1]);
			}
			
			let dataGroup = this.magoInstance.getF4dController().getF4dGroup(this.targetGroupId);
			
			for(let i in dataGroup) {
				let node = dataGroup[i];
				
				if(!(node instanceof Mago3D.Node) || !node.data.attributes.isPhysical) {
					continue;
				}
				let f4dData = node.data; 
				f4dData.aditionalColor = undefined;
				f4dData.isColorChanged = false;
				node.setVisible(true);
			}
		}
	},
	LEVEL : {
		name : 'level',
		position : {
			destination : Cesium.Cartesian3.fromDegrees(126.72329039643742, 37.368644565326406, 62.23842400208409),
			orientation : {
				heading : 6.122626035323737,
				pitch : -0.7165622789814403,
				roll : 6.282497089780563
			}
		},
		sliderOption : {
		    start: 1,
			step : 1,
			direction: 'rtl',
			orientation : 'vertical',
		    range: {
		        'min': 1,
		        'max': 5
		    },
			format : {
				to : parseInt10,
				from : parseInt10
			},
			pips: {
		        mode: 'steps',
		        stepped: false,
				values : [1, 2, 3, 4, 5],
				format : {
					to : function(value) {
						return value + 'Step';
					}
				}
		    }
		},
		targetGroupId : '10001',
		updateFunction : function(e) {
			let self = this;
			const caseOver = function(f4d) {
				if(!f4d.getVisible()) {
					f4d.setVisible(true);
					SmltTimeSeries.addEffect(f4d.guid, 1.0, 1.5, self.magoInstance);
				}
			}
			
			const caseUnder = function(f4d) {
				f4d.setVisible(false);
			}
			if(this.active) {
				let dataGroup = this.magoInstance.getF4dController().getF4dGroup(this.targetGroupId);
				let sVal = e.map(function(v){return v-1});
				
				for(let i in dataGroup) {
					let node = dataGroup[i];
					
					if(!(node instanceof Mago3D.Node) || !node.data.attributes.isPhysical) {
						continue;
					}
					
					let groupNumber = node.data.attributes.group;
					
					if(groupNumber === undefined || groupNumber === null) {
						continue;
					}
					
					if(sVal < groupNumber) {
						caseUnder(node);
						continue;
					}
					
					caseOver(node);
				}	
			}
		},
		initDataFunction : function() {
			if(this.slider) {
				this.slider.set([1]);
			}
			
			let dataGroup = this.magoInstance.getF4dController().getF4dGroup(this.targetGroupId);
			
			for(let i in dataGroup) {
				let node = dataGroup[i];
				
				if(!(node instanceof Mago3D.Node) || !node.data.attributes.isPhysical) {
					continue;
				}
				node.setVisible(true);
			}
		}
	}
}
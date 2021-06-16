

const Simulation = function(magoInstance) {
	const parseInt10 = function(value) {
		return  parseInt(value, 10);
	}
	const SMLT_TIMESERIES = {
		STEP : {
			name : 'step',
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
							return value + '단계';
						}
					}
			    }
			},
			targetGroupId : ''
		},
		LEVEL : {
			name : 'level',
			sliderOption : {
				
			}
		}
	}

	const SmltTimeSeries = function(constructorOption) {
		const PREFIX = 'smlt-construction-';
		this.name = constructorOption.name;
		let self = this;
		
		const _initSlider = function(slider_opt) {
			const SUFFIX = '-slider'
			const slider = noUiSlider.create(document.getElementById(PREFIX + self.name + SUFFIX), slider_opt); 
	 		slider.on('update', function(e) {
				console.info(e);
			});
			return slider;
		}
		
		this.range = _initSlider(constructorOption.sliderOption); 
	}
	const ON_CLASS_NAME = 'on';
	
	const _ui = function() {
		const cssName = {
			menuClass : {
				'tab' : '.smlt_menu',
				'item' : '.smlt-item'
			},
			targetClass : {
				'tab' : '.smlt_contents',
				'item' : '.smlt .sub'
			},
			getMenuClassName : function(type) {
				return this.menuClass[type];
			},
			getTargetClassName : function(type) {
				return this.targetClass[type];
			}
		};
		
		const hideAll = (type) => {$(cssName.getTargetClassName(type)).hide()};
		const removeOnClass = (type) => {
			$(cssName.getMenuClassName(type)).removeClass(ON_CLASS_NAME);
		};
		const addOnClassTarget = ($target) => {$target.addClass(ON_CLASS_NAME)};
		
		const toggleSubMenu = ($menu) => {
			const $target = $menu.siblings('.sub');
			if(!$target.is(':visible')) {
				const type = 'item';
				
				hideAll(type);
				removeOnClass(type);
				addOnClassTarget($menu);
				
				$target.slideDown("slow");
			}
		}
		
		const toggleContents = ($menu) => {
			if(!$menu.hasClass(ON_CLASS_NAME)) {
				const type = 'tab';
				
				const convertId = $obj => {
					const id = $obj.get(0).id;
					return '#' + id.replace('menu', 'contents');
				}
				
				hideAll(type);
				removeOnClass(type);
				addOnClassTarget($menu);
				
				$(convertId($menu)).slideDown("slow");
			}
		}
		
		const clickItem = (event) => {
			toggleSubMenu($(event.delegateTarget));
		}
		
		const clickTab = (event) => {
			toggleContents($(event.delegateTarget));
		}
	
		//서브메뉴(item) 클릭
		$('.smlt-item').click(clickItem);
		
		//탭메뉴 클릭
		$('.smlt_menu').click(clickTab);
		
		//시작 시 첫메뉴 클릭.
		$('.smlt_menu:eq(0)').removeClass(ON_CLASS_NAME).trigger('click');
	}
	
	//버튼 클릭같은거
	_ui();
	
	//진도부터 뺌
	var step = new SmltTimeSeries(SMLT_TIMESERIES.STEP);
	
}




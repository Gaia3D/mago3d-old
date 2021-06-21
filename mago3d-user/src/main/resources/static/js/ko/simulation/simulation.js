

const Simulation = function(magoInstance) {
	const _ui = function() {
		const ON_CLASS_NAME = 'on';
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
		
		const hide = (type) => {
			hideAll(type);
			removeOnClass(type);
		}
		const addOnClassTarget = ($target) => {$target.addClass(ON_CLASS_NAME)};
		
		const toggleSubMenu = ($menu) => {
			const $target = $menu.siblings('.sub');
			if(!$target.is(':visible')) {
				const type = 'item';
				
				hide(type);
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
				
				hide('item');
				hide(type);
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
		
		//닫기 버튼 클릭 시 기능 중지
		$('.smlt .sub button.btn-cancel').click(function() {
			hide('item');
		});
		
		return {
			hideSubMenu : function() {
				hide('item');
			}
		}
	}
	
	const _run = function() {
		//const step, level;
		const _simulator = function() {
			const _timeSeries = function() {
				const elId = 'smlt_contents_timeseries';
				
				const step = new SmltTimeSeries(SmltTimeSeries.SMLT_TIMESERIES.STEP, magoInstance);
				const level = new SmltTimeSeries(SmltTimeSeries.SMLT_TIMESERIES.LEVEL, magoInstance);
				
				const smlts = {
					step,
					level	
				}
				
				return {
					getSimulation : function(type) {
						return smlts[type]; 
					},
					setActive : function(type, active) {
						this.getSimulation(type).active = active;
					},
					stop : function() {
						for(let key in smlts) {
							this.setActive(key, false);
						}
					},
					observerTarget : elId
				}
			}
			
			const _natural = function() {
				const elId = 'smlt_contents_natural';
				
				const smlts = {
					wind : new SmltWind(magoInstance)
				}
				
				return {
					getSimulation : function(type) {
						return smlts[type]; 
					},
					setActive : function(type, active) {
						this.getSimulation(type).active = active;
					},
					stop : function() {
						for(let key in smlts) {
							this.setActive(key, false);
						}
					},
					observerTarget : elId
				}
			}
			
			return {
				timeSeries : _timeSeries(),
				natural : _natural()
			}
		};
		
		
		let simulator;
		
		const simulatorProvider = _simulator();
		const tabConfig = { attributes: true, attributeFilter:['style', 'class'],subtree: true,childList:false, attributeOldValue:true};
		const menuConfig = { attributes: true, attributeFilter:['class'],subtree: true,childList:false, attributeOldValue:true};
		const MAIN_OBSERVE_TARGET = 'simulationContent';
		const menuObserver = new MutationObserver(function(mutations) {
			mutations.forEach(function(mutation) {
				const target = mutation.target;
				if(target.classList.contains('smlt-item')) {
					const on = target.classList.contains('on');
					const type = target.dataset.type;
					simulator.setActive(type,on);	
				}
			});
		});
		
		const TABOBSERVERTYPE = {
			'NO' : 0,
			'TAB' : 1,
			'MENU' : 2 
		}
		const tabObserver = new MutationObserver(function(mutations) {
			const _checkType = function (m) {
				const target = m.target;
				//처리대상아님
				let type = TABOBSERVERTYPE.NO;
				
				if(target.id === MAIN_OBSERVE_TARGET) {
					type = TABOBSERVERTYPE.TAB;
					return type;
				}
				
				if(target.classList.contains('smlt_menu') && target.classList.contains('on')) {
					type = TABOBSERVERTYPE.MENU;
					return type;
				}
				
				return type;
			}
			
			const _tab = function(m) {
				let mutationStyle = window.getComputedStyle(m.target);
				if(mutationStyle.display === 'none') {
					if(simulator) {
						simulator.stop();
						simulator = undefined;
					}
					ui.hideSubMenu();
					menuObserver.disconnect();
				} else {
					let onMenu = document.querySelector('#simulationContent .simulation-menu .smlt_menu.on');
					_onSimulation(onMenu);
				}
			}
			
			const _onSimulation = function(menu) {
				for(let key in simulatorProvider) {
					if(key !== menu.dataset.smlt) {
						simulatorProvider[key].stop();
						continue;
					}
					
					simulator = simulatorProvider[key];
				}

				menuObserver.observe(document.getElementById(simulator.observerTarget), menuConfig);
			}
			
			
			for(let i in mutations) {
				let mut = mutations[i];
				let mutationType = _checkType(mut);
				
				if(!mutationType) continue;
				
				if(mutationType === TABOBSERVERTYPE.TAB) {
					_tab(mut);
				} else {
					_onSimulation(mut.target);
				}
				
				return;
			}
		});
		
		tabObserver.observe(document.getElementById(MAIN_OBSERVE_TARGET), tabConfig);
	}
	
	//버튼 클릭같은거
	const ui = _ui();
	
	//프로그램 실행
	_run();
}
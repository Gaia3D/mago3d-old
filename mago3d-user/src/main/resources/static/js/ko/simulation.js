const Simulation = function(magoInstance) {
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
	
	
}


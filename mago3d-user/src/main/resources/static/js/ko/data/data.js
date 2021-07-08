const Data = function(magoInstance) {
    const _ui = function() {
        const ON_CLASS_NAME = 'actived';
        const cssName = {
            menuClass : {
                'tab' : '.data-menu',
                'item' : '.data-item'
            },
            targetClass : {
                'tab' : '.data-contents',
                'item' : '.data .sub'
            },
            getMenuClassName : function(type) {
                return this.menuClass[type];
            },
            getTargetClassName : function(type) {
                return this.targetClass[type];
            }
        };

        const hideAll = (type) => {$(cssName.getTargetClassName(type)).hide()};
        const removeOnClass = (type) => {$(cssName.getMenuClassName(type)).removeClass(ON_CLASS_NAME)};

        const hide = (type) => {
            hideAll(type);
            removeOnClass(type);
        }

        const addOnClassTarget = ($target) => {$target.addClass(ON_CLASS_NAME)};

        const toggleContents = ($menu) => {
            if(!$menu.hasClass(ON_CLASS_NAME)) {

                const convertId = $obj => {
                    const id = $obj.get(0).id;
                    return '#' + id.replace('tab', 'content');
                }

                hide('tab');
                addOnClassTarget($menu);

                $(convertId($menu)).slideDown("slow");
            }
        }

        const clickTab = (event) => {
            toggleContents($(event.delegateTarget));
        }

        //탭메뉴 클릭
        $('.data-menu').click(clickTab);
    }

    //버튼 클릭같은거
    const ui = _ui();
}
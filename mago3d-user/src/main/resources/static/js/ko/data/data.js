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

    const _run = function() {

        const MAIN_OBSERVE_TARGET = 'data-wrap-content';
        const tabConfig = {attributes: true, attributeFilter: ['style', 'class'], subtree: true, childList: false, attributeOldValue: true};
        const menuConfig = {attributes: true, attributeFilter: ['class'], subtree: true, childList: false, attributeOldValue: true};

        const menuObserver = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                console.info("menu : ")
                console.info(mutation.target);
            });
        });

        const tabObserver = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                if (mutation.target.id !== MAIN_OBSERVE_TARGET) return true;
                console.info("tab : ")
                console.info(mutation.target);

                // menuObserver.disconnect();
                // menuObserver.observe(document.getElementById(MAIN_OBSERVE_TARGET), menuConfig);

            });
        });
        menuObserver.observe(document.getElementById(MAIN_OBSERVE_TARGET), menuConfig);
        tabObserver.observe(document.getElementById(MAIN_OBSERVE_TARGET), tabConfig);

        /*
        const _data = function() {
            const elId = 'layer-content';
            const datas = {
                data2d : new Data2D(magoInstance),
                data3d : new Data2D(magoInstance)
            }
            return {
                getDatas : function(type) {
                    return datas[type];
                },
                setActive : function(type, active) {
                    this.getDatas(type).active = active;
                },
                stop : function() {
                    for(let key in datas) {
                        this.setActive(key, false);
                    }
                },
                observerTarget : elId
            }
        }
        const dataProvider = _data();
        let data = dataProvider[key];
         */
    }

    //버튼 클릭같은거
    _ui();

    //프로그램 실행
    _run();

}
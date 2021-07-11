const Data = function(magoInstance) {

    const _ui = function() {
        const ON_CLASS_NAME = 'actived';
        const cssName = {
            menuClass : {
                'tab' : '.data-menu'
            },
            targetClass : {
                'tab' : '.data-contents'
            },
            getMenuClassName : function(type) {
                return this.menuClass[type];
            },
            getTargetClassName : function(type) {
                return this.targetClass[type];
            }
        };
        const hide = (type) => {
            $(cssName.getTargetClassName(type)).hide();
            $(cssName.getMenuClassName(type)).removeClass(ON_CLASS_NAME);
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

        let data;
        const datas = {
            'layer-tab' : new Data2D(magoInstance),
            'data-tab' : new Data3D(magoInstance)
        }

        const init = function(targetId) {
            data = datas[targetId];
            if (data && data.load === false) {
                data.load = true;
            }
        }

        const dataObserverTarget = document.getElementById('data-menu');
        if (dataObserverTarget) {
            const dataObserverConfig = {attributes: true, attributeFilter: ['class']};
            const dataMenuObserver = new MutationObserver(function (mutations) {
                for (const mutation of mutations) {
                    const isVisible = mutation.target.classList.contains('actived');
                    if (isVisible) {
                        init(mutation.target.id);
                    }
                }
            });
            const dataTabObserver = new MutationObserver(function (mutations) {
                for (const mutation of mutations) {
                    const isVisible = mutation.target.classList.contains('on');
                    if (isVisible) {
                        const targetId = $('#data-wrap-content').find('.data-menu.actived').attr('id');
                        init(targetId);
                        for (const targetId in datas) {
                            dataMenuObserver.observe(document.getElementById(targetId), dataObserverConfig);
                        }
                    } else {
                        dataMenuObserver.disconnect();
                    }
                }
            });
            dataTabObserver.observe(dataObserverTarget, dataObserverConfig);
        }

    }

    //버튼 클릭같은거
    _ui();

    //프로그램 실행
    _run();
}
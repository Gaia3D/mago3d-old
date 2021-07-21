const Data2D = function(magoInstance) {
    this.magoInstance = magoInstance;
    this._load = false;
    this.layers = [];

    var that = this;
    function setElementEvent() {
        $('#layer-content-turn-on-all').click(function () {
            Data2D.allLayersOnOff(that, true);
        });
        $('#layer-content-turn-off-all').click(function () {
            Data2D.allLayersOnOff(that, false);
        });
        $('#layer-content-unfold-tree').click(function() {
            Data2D.unfoldTree();
        });
        $('#layer-content-fold-tree').click(function() {
            Data2D.foldTree();
        });
        $('#layer-content-save-user-layers').click(function() {
            Data2D.updateLayers();
        });
    }
    setElementEvent();
}

Object.defineProperties(Data2D.prototype, {
    load: {
        get: function () {
            return this._load;
        },
        set: function (load) {
            if (load) {
                this.getLayers();
            }
            this._load = load;
        }
    }
});

// 레이어 전체 켜기/끄기
Data2D.allLayersOnOff = function(data2d, visible) {
    for (const key in data2d.layers) {
        const layer = data2d.layers[key];
        const layerGroupId = layer.layerGroupId;
        const _this = {checked: visible};
        Data2D.layerGroupOnOff(layerGroupId, _this);
        $('.data-layer input').each(function() {
            $(this).prop('checked', _this.checked);
        })
    }
}
// 레이어 트리 전체 펼치기
Data2D.unfoldTree = function() {
    $('.data-layer span').each(function() {
       if ($(this).is(':first-child')) {
           const collapseSpan = $(this);
           collapseSpan.removeClass();
           collapseSpan.addClass("data-layer-ttl");
       }
    });
    $('.data-layerlist-bx').slideDown();
}
// 레이어 트리 전체 접기
Data2D.foldTree = function() {
    $('.data-layer span').each(function() {
        if ($(this).is(':first-child')) {
            const collapseSpan = $(this);
            collapseSpan.removeClass();
            collapseSpan.addClass("data-layer-ttld");
        }
    });
    $('.data-layerlist-bx').slideUp();
}
// 레이어 트리 접기/펼치기
Data2D.collapseTree = function (layerGroupId, _this, event) {
    const subTree = $(".data-layerlist-bx[data-parent='" + layerGroupId + "']");
    const collapseSpan = $(_this).find("span").first();
    if (subTree.is(':visible')) {
        subTree.slideUp();
        collapseSpan.removeClass();
        collapseSpan.addClass("data-layer-ttld");
    } else {
        subTree.slideDown();
        collapseSpan.removeClass();
        collapseSpan.addClass("data-layer-ttl");
    }
    return false;
}
// 레이어 그룹 ON/OFF
Data2D.layerGroupOnOff = function (layerGroupId, _this, event) {
    //console.debug("layerGroupOnOff : " + layerGroupId);
    if (_this.checked) {
        MAGO.map.addGroupLayer(Number(layerGroupId));
    } else {
        MAGO.map.removeGroupLayer(Number(layerGroupId));
    }
    $(".data-layerlist[data-parent='" + layerGroupId + "'] input").prop('checked', _this.checked);
    return false;
}
// 레이어 ON/OFF
Data2D.layerOnOff = function (layerKey, _this, event) {
    //console.debug("layerOnOff : " + layerKey);
    if (_this.checked) {
        MAGO.map.addLayer(layerKey);
    } else {
        MAGO.map.removeLayer(layerKey);
    }
    return false;
}
// 레이어 영역으로 이동
Data2D.move = function (layerId) {
    $.ajax({
        url: "/layers/" + layerId + "/envelope",
        type: "GET",
        headers: {"X-Requested-With": "XMLHttpRequest"},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (msg) {
            if (msg.statusCode <= 200) {
                const pointArray = [];
                const minX = msg.minPoint[0];
                const minY = msg.minPoint[1];
                const maxX = msg.maxPoint[0];
                const maxY = msg.maxPoint[1];
                pointArray[0] = Mago3D.ManagerUtils.geographicCoordToWorldPoint(minX, minY, 0);
                pointArray[1] = Mago3D.ManagerUtils.geographicCoordToWorldPoint(maxX, maxY, 0);
                MAGO3D_INSTANCE.getMagoManager().flyToBox(pointArray);
            } else {
                alert(JS_MESSAGE[msg.errorCode]);
                console.log("---- " + msg.message);
            }
        },
        error: function (request, status, error) {
            alert(JS_MESSAGE["ajax.error.message"]);
        }
    });
}
// 레이어 설정 저장
Data2D.updateLayers = function() {

    var layerList = [];
    $("input:checkbox[name='layer']").each(function(){
        if(this.checked) {
            layerList.push($(this).attr("data-layer-key"));
        }
    });
    $("#base-layers").val(layerList.join(","));

    $.ajax({
        url: "/user-policy/update-layers",
        type: "POST",
        headers: {"X-Requested-With": "XMLHttpRequest"},
        dataType: "json",
        data : $("#layer-form").serialize(),
        success: function(res){
            if(res.statusCode <= 200) {
                alert(JS_MESSAGE["update"]);
            } else {
                if (res.errorCode === "session.required") {
                    const isGoToSignInPage = confirm("로그인이 필요한 기능입니다. 로그인 페이지로 이동할까요?");
                    if (isGoToSignInPage) {
                        window.location = res.message;
                    }
                }
                //alert(JS_MESSAGE[res.errorCode]);
                //console.log("---- " + res.message);
            }
        },
        error: function(request, status, error) {
            console.log([request, status, error]);
            alert(JS_MESSAGE["ajax.error.message"]);
        }
    });
}
// 서버에서 레이어 정보 가져오기
Data2D.prototype.getLayers = function() {
    const _this = this;
    $.ajax({
        url: "/layers",
        type: "GET",
        headers: {"X-Requested-With": "XMLHttpRequest"},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(res){
            if(res.statusCode <= 200) {
                // html 생성
                _this.layers = res.layerGroupList;
                _this.createLayerTree();
            } else {
                alert(JS_MESSAGE[res.errorCode]);
                console.log("---- " + res.message);
            }
        },
        error: function(request, status, error) {
            alert(JS_MESSAGE["ajax.error.message"]);
        }
    });
}
// 레이어 트리 만들기
Data2D.prototype.createLayerTree = function() {
    if (!$("#layer-list-source").html()) return;
    const template = Handlebars.compile($("#layer-list-source").html());
    for (const key in this.layers) {
        const layer = this.layers[key];
        const contents = template(layer);
        let selector = "";
        if (layer.depth === 1) {
            selector = $("#layer-list-dhtml");
            selector.append(contents);
        } else {
            selector = $("[data-depth=" + layer.parent + "] > div");
            selector.append(contents);
        }
    }
    this.layerGroupDefaultOnOff();
}
// 레이어 그룹 기본 켜기/끄기 설정
Data2D.prototype.layerGroupDefaultOnOff = function() {
    for (const i in this.layers) {
        const layer = this.layers[i];
        const layerGroupId = layer.layerGroupId;
        const sublayers = layer.layerList;
        for (const j in sublayers) {
            const sublayer = sublayers[j];
            if (sublayer.defaultDisplay) {
                $(".data-layer[data-depth='" + layerGroupId + "'] input").prop('checked', true);
                break;
            }
        }
    }
}
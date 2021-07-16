const Data3D = function(magoInstance) {
    this.magoInstance = magoInstance;
    this._load = false;

    this.datas = [];
    this.dataGroups = [];

    this.pagingRootElement = $("#data-paging-dhtml");
    this.dataPagination = undefined;
    this.groupPagination = undefined;

    const that = this;
    function setElementEvent() {

        // 팝업 레이어 드래그 가능하도록 설정
        $('#data-group-search').draggable({containment: "window"});
        $('#data-search-filter').draggable({containment: "window"});

        $('#data-info-dialog-dhtml').draggable({containment: "window"});
        $('#data-group-dialog-dhtml').draggable({containment: "window"});

        /** 필터 ON/OFF **/

        // 데이터 그룹 검색 ON/OFF
        $('#search-data-group').click(function() {
            Data3D.toggleLayer('data-group-search');
            that.getDataGroups(1);
            return false;
        });
        // 데이터 검색 필터 ON/OFF
        $('#search-data-filter').click(function() {
            Data3D.toggleLayer('data-search-filter');
            return false;
        });


        /** 데이터 그룹 **/

        // 데이터 그룹 검색 버튼 클릭
        $('#search-data-group').click(function() {
            that.getDataGroups(1);
        });
        // 데이터 그룹 검색 엔터키
        $("#data-group-content input:text[name='searchValue']").keyup(function(e) {
            if (e.keyCode === 13) that.getDataGroups(1);
        });
        // 데이터 그룹 선택
        $("#data-group-content").on('click', '.select-data-group', function() {
            const groupId = $(this).data('groupId');
            $("#search-data-form input[name='dataGroupId']").val(groupId);
            Data3D.toggleLayer('data-group-search');
            that.getDatas(1);
        });


        /** 데이터 필터 **/

        // 데이터 필터 공유유형 선택
        $('#data-search-filter .sharing').click(function() {
           $(this).toggleClass('on');
           $(this).siblings().removeClass('on');
        });
        // 데이터 필터 데이터타입 선택
        $('#data-search-filter .data-type').click(function() {
            $(this).toggleClass('on');
            $(this).siblings().removeClass('on');
        });
        // 데이터 필터 적용
        $('#data-search-filter-apply').click(function() {
            const sharing = $('#data-search-filter .sharing.on').data('sharing');
            const dataType = $('#data-search-filter .data-type.on').data('type');
            $("#search-data-form input[name='sharing']").val(sharing);
            $("#search-data-form input[name='dataType']").val(dataType);
            Data3D.toggleLayer('data-search-filter');
            that.getDatas(1);
        });
        // 데이터 필터 초기화
        $('#data-search-filter-reset').click(function() {
            $('#data-search-filter .layer-list').removeClass('on');
        });


        /** 데이터 **/

        // 데이터 검색 버튼 클릭
        $('#search-data').click(function() {
            that.getDatas(1);
        });

        // 데이터 검색 엔터키
        $("#data-content input:text[name='searchValue']").keyup(function(e) {
            if (e.keyCode === 13) that.getDatas(1);
        });

        // 데이터 그룹/필터 초기화
        $("#search-data-reset").click(function() {
            /*$("#search-data-form input:text[name='searchValue']").val("");*/
            $("#search-data-form input[name='dataGroupId']").val(0);
            $("#search-data-form input[name='sharing']").val("");
            $("#search-data-form input[name='dataType']").val("");
            that.getDatas(1);
        });

    }
    setElementEvent();

}

Object.defineProperties(Data3D.prototype, {
    load : {
        get : function () {
            return this._load;
        },
        set : function (load) {
            if(load) {
                //this.getDataGroups(1);
                this.getDatas(1);
            } else {
                //this.clear();
            }
            this._load = load;
        }
    }
});

/**
 * HTML 팝업레이어 ON/OFF
 * @param id 토글해야할 html id
 */
Data3D.toggleLayer = function(id) {
    $('#' + id).toggle();
}

// 데이터 그룹 표시/비표시
Data3D.dataGroupOnOff = function (groupId, _this) {
    if (groupId === null || groupId === '') {
        alert(JS_MESSAGE["data.info.incorrect"]);
        return;
    }

    var option = true;
    if (!_this.checked) option = false;

    groupId = parseInt(groupId);
    this._dataGroupOnOffById(groupId, option);
}

Data3D._dataGroupOnOffById = function (groupId, option) {
    const nodeMap = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.getNodesMap(groupId);
    let isLoaded = true;
    if (!$.isEmptyObject(nodeMap)) {
        for (const key in nodeMap) {
            const node = nodeMap[key];
            if (!$.isEmptyObject(nodeMap)) {
                const nodeData = node.data;
                if (nodeData && nodeData.attributes && nodeData.attributes.isPhysical === true) {
                    const optionObject = { isVisible : option };
                    setNodeAttributeAPI(MAGO3D_INSTANCE, groupId, key, optionObject);
                }
            } else {
                isLoaded = false;
                break;
            }
        }
    } else {
        isLoaded = false;
    }

    if (!isLoaded) {
        alert(JS_MESSAGE["data.not.loaded"]);
        return;
    }

    const projectsMap = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.projectsMap;
    if (!$.isEmptyObject(projectsMap) && projectsMap[groupId] && projectsMap[groupId].attributes) {
        projectsMap[groupId].attributes.isVisible = option;
    }
}

// 데이터 표시/비표시
Data3D.dataOnOff = function(groupId, dataKey, tiling, _this) {

    if (groupId === null || groupId === '' || dataKey === null || dataKey === '') {
        alert(JS_MESSAGE["data.info.incorrect"]);
        return;
    }

    var option = true;
    if (!_this.checked) option = false;

    if (tiling) dataKey = "F4D_" + dataKey;

    groupId = parseInt(groupId);
    if (!isExistDataAPI(MAGO3D_INSTANCE, groupId, dataKey)) {
        alert(JS_MESSAGE["data.not.loaded"]);
        return;
    }
    var optionObject = { isVisible : option };
    setNodeAttributeAPI(MAGO3D_INSTANCE, groupId, dataKey, optionObject);

}

// 데이터 상세 팝업 레이어
Data3D.dataInfoPopupLayerOnOff = function(url) {
    const callback = function(msg) {
        if(msg.statusCode <= 200) {
            console.info(msg.dataInfo);
            const template = Handlebars.compile($("#data-info-source").html());
            $("#data-info-dialog-dhtml").html("").append(template(msg.dataInfo)).show();
        } else {
            alert(JS_MESSAGE[msg.errorCode]);
        }
    }
    this._loadDetail(url, callback);
}
// 데이터 그룹 상세 팝업 레이어
Data3D.dataGroupPopupLayerOnOff = function(url) {
    const callback = function(msg) {
        if(msg.statusCode <= 200) {
            const template = Handlebars.compile($("#data-group-info-source").html());
            $("#data-group-dialog-dhtml").html("").append(template(msg.dataGroup)).show();
        } else {
            alert(JS_MESSAGE[msg.errorCode]);
        }
    }
    this._loadDetail(url, callback);
}

Data3D._loadDetail = function(url, callback) {
    $.ajax({
        url: url,
        type: "GET",
        headers: {"X-Requested-With": "XMLHttpRequest"},
        dataType: "json",
        success: function(msg){
            callback(msg);
        },
        error:function(request,status,error){
            alert(JS_MESSAGE["ajax.error.message"]);
        }
    });
}

// 페이징 성공 콜백
Data3D.prototype.success = function(res) {
    if(res.statusCode <= 200) {

        // html 생성
        if (res.dataGroupList) {
            this.dataGroups = res.dataGroupList;
            this.dataGroupOnOff();
            this.createDataGroupContents(res);
            this.pagingRootElement = $("#data-group-paging-dhtml");
            if (this.groupPagination) {
                this.groupPagination.pagination = res.pagination;
                this.groupPagination.target = this;
            } else {
                this.groupPagination = Pagination.create(res.pagination, this);
            }
        }

        if (res.dataList) {
            this.datas = res.dataList;
            this.datasGroupOnOff();
            this.createDataContents(res);
            this.pagingRootElement = $("#data-paging-dhtml");
            if (this.dataPagination) {
                this.dataPagination.pagination = res.pagination;
                this.dataPagination.target = this;
            } else {
                this.dataPagination = Pagination.create(res.pagination, this);
            }
        }

    } else {
        alert(JS_MESSAGE[res.errorCode]);
        console.log("---- " + res.message);
    }
}

// 서버에서 데이터 그룹 정보 가져오기
Data3D.prototype.getDataGroups = function(pageNo) {
    const _this = this;

    let params = _this.getFormData($("#search-data-group-form"));
    params.pageNo = pageNo;
    params.searchOption = "1";
    const recursiveEncoded = $.param(params);
    //{ pageNo : pageNo, searchWord : "data_group_name", searchValue : searchDataGroupName, searchOption : "1"};

    $.ajax({
        url: "/data-groups",
        type: "GET",
        headers: {"X-Requested-With": "XMLHttpRequest"},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: recursiveEncoded,
        success: function(res) {
            _this.success(res);
        },
        error: function(request, status, error) {
            alert(JS_MESSAGE["ajax.error.message"]);
        }
    });
}

// 서버에서 데이터 정보 가져오기
Data3D.prototype.getDatas = function(pageNo) {
    const _this = this;

    let params = _this.getFormData($("#search-data-form"));
    params.pageNo = pageNo;
    params.searchOption = "1";
    const recursiveEncoded = $.param(params);
    //{ pageNo : pageNo, searchWord : "data_group_name", searchValue : searchDataGroupName, searchOption : "1"};

    $.ajax({
        url: "/datas",
        type: "GET",
        headers: {"X-Requested-With": "XMLHttpRequest"},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: recursiveEncoded,
        success: function(res) {
            _this.success(res);
        },
        error: function(request, status, error) {
            alert(JS_MESSAGE["ajax.error.message"]);
        }
    });
}

Data3D.prototype.getFormData = function($form) {
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};
    $.map(unindexed_array, function (n, i) {
        if (indexed_array[n['name']]) {
            indexed_array[n['name']] += ',' + n['value'];
        } else {
            indexed_array[n['name']] = n['value'];
        }
    });
    return indexed_array;
}

Data3D.prototype.dataGroupOnOff = function() {
    if (this.dataGroups.length <= 0) return;
    const projectsMap = this.magoInstance.getMagoManager().hierarchyManager.projectsMap;
    for (const key in this.dataGroups) {
        const dataGroup = this.dataGroups[key];
        const dataGroupId = parseInt(dataGroup.dataGroupId);
        let isVisible = true;
        if (!$.isEmptyObject(projectsMap)) {
            const projects = projectsMap[dataGroupId];
            if ($.isEmptyObject(projects)) {
                dataGroup.groupVisible = isVisible;
                continue;
            }
            const groupVisible = projects.attributes.isVisible;
            if (groupVisible !== undefined) {
                isVisible = groupVisible;
            }
        }
        dataGroup.groupVisible = isVisible;
    }
}

Data3D.prototype.datasGroupOnOff = function() {
    if (this.datas.length <= 0) return;
    const projectsMap = this.magoInstance.getMagoManager().hierarchyManager.projectsMap;
    for (const key in this.datas) {
        const data = this.datas[key];
        const dataGroupId = parseInt(data.dataGroupId);
        let isVisible = true;
        if (!$.isEmptyObject(projectsMap)) {
            const projects = projectsMap[dataGroupId];
            if ($.isEmptyObject(projects)) {
                data.groupVisible = isVisible;
                continue;
            }

            if (!projects.attributes) {
                projects.attributes = {};
                projects.attributes.objectType = "basicF4d";
                if (data.tiling) {
                    projects.attributes.fromSmartTile = true;
                }
            }

            const groupVisible = projects.attributes.isVisible;
            if (groupVisible !== undefined) {
                isVisible = groupVisible;
            }
        }
        data.groupVisible = isVisible;
    }
}

Data3D.prototype.createDataGroupContents = function(msg) {

    //핸들바 템플릿 컴파일
    const searchSummaryTemplate = Handlebars.compile($("#search-summary-source").html());
    const template = Handlebars.compile($("#data-group-list-source").html());
    const pagingTemplate = Handlebars.compile($("#pagination-source").html());

    //핸들바 템플릿에 데이터를 바인딩해서 HTML 생성
    $("#data-group-search-summary-dhtml").html("").append(searchSummaryTemplate(msg));
    $("#data-group-list-dhtml").html("").append(template(msg));
    $("#data-group-paging-dhtml").html("").append(pagingTemplate(msg));

}

Data3D.prototype.createDataContents = function(msg) {

    //핸들바 템플릿 컴파일
    const searchSummaryTemplate = Handlebars.compile($("#search-summary-source").html());
    const template = Handlebars.compile($("#data-list-source").html());
    const pagingTemplate = Handlebars.compile($("#pagination-source").html());

    //핸들바 템플릿에 데이터를 바인딩해서 HTML 생성
    $("#data-search-summary-dhtml").html("").append(searchSummaryTemplate(msg));
    $("#data-list-dhtml").html("").append(template(msg));
    $("#data-paging-dhtml").html("").append(pagingTemplate(msg));

}
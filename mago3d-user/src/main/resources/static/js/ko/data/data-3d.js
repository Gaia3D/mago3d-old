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

        // 데이터 그룹 검색 ON/OFF
        $('#search-data-group').click(function() {
            Data3D.dataGroupSearchOnOff();
            that.getDataGroups(1);
            return false;
        });

        // 데이터 검색 필터 ON/OFF
        $('#search-data-filter').click(function() {
            Data3D.dataSearchFilterOnOff();
            return false;
        });

        // 데이터 그룹 검색 버튼 클릭
        $('#search-data-group').click(function() {
            that.getDataGroups(1);
        });

        // 데이터 그룹 검색 엔터키
        $("#data-group-info-content input:text[name='searchValue']").keyup(function(e) {
            if (e.keyCode === 13) that.getDataGroups(1);
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

// 데이터 그룹 검색 ON/OFF
Data3D.dataGroupSearchOnOff = function() {
    $('#data-group-search').slideToggle();
}

// 데이터 검색 필터 ON/OFF
Data3D.dataSearchFilterOnOff = function() {
    $('#data-search-filter').slideToggle();
}

// 데이터 표시/비표시
Data3D.dataOnOff = function(groupId, dataKey, tiling, _this) {

    if(groupId === null || groupId === '' || dataKey === null || dataKey === '') {
        alert(JS_MESSAGE["data.info.incorrect"]);
        return;
    }

    var option = true;
    if (_this.checked) {
        option = false;
    }

    if (tiling) {
        dataKey = "F4D_" + dataKey;
    }
    //setNodeAttributeAPI(MAGO3D_INSTANCE, groupId, dataKey, optionObject);

    groupId = parseInt(groupId);
    //var nodeMap = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.getNodesMap(groupId);

    //isExistDataAPI(MAGO3D_INSTANCE, groupId, dataKey);
    //isDataReadyToRender(MAGO3D_INSTANCE, groupId, dataKey);

    if (!isExistDataAPI(MAGO3D_INSTANCE, groupId, dataKey)) {
        alert(JS_MESSAGE["data.not.loaded"]);
        return;
    }

    var optionObject = { isVisible : option };
    setNodeAttributeAPI(MAGO3D_INSTANCE, groupId, dataKey, optionObject);

    /*
    if ($(this).hasClass("show")) {
        $(this).removeClass("show");
        $(this).addClass("hide");
    } else {
        $(this).removeClass("hide");
        $(this).addClass("show");
    }
    */

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
            if (projects.attributes.isVisible) {
                isVisible = projects.attributes.isVisible;
            }
        }
        dataGroup.groupVisible = isVisible;
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
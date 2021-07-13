const Data3D = function(magoInstance) {
    this.magoInstance = magoInstance;
    this._load = false;
    this.datas = [];
    this.dataGroups = [];

    function setElementEvent() {

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
                this.getDataGroups(1);
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
        success: function(res){
            if(res.statusCode <= 200) {
                // html 생성
                _this.dataGroups = res.dataGroupList;
                //_this.createLayerTree();
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

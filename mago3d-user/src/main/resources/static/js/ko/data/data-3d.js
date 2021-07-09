const Data3D = function(magoInstance) {
    this.magoInstance = magoInstance;
    this._load = false;
    this.datas = [];
}

Object.defineProperties(Data3D.prototype, {
    load : {
        get : function () {
            return this._load;
        },
        set : function (load) {
            if(load) {
                alert('1');
                //this.initCamera();
                //this.run();
            } else {
                //this.clear();
            }
            this._load = load;
        }
    }
});

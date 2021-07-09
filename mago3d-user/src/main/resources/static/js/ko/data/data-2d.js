const Data2D = function(magoInstance) {
    this.magoInstance = magoInstance;
    this._load = false;
    this.layers = [];
}

Object.defineProperties(Data2D.prototype, {
    load : {
        get : function () {
            return this._load;
        },
        set : function (load) {
            if(load) {
                alert('2');
                //this.initCamera();
                //this.run();
            } else {
                //this.clear();
            }
            this._load = load;
        }
    }
});

const Data2D = function(magoInstance) {
    this.magoInstance = magoInstance;
    this._active = false;
    this.layers = [];
}

Object.defineProperties(Data2D.prototype, {
    active : {
        get : function () {
            return this._active;
        },
        set : function (active) {
            if(active) {
                this.run();
            } else {
                this.clear();
            }
            this._active = active;
        }
    }
});
// smart tiling data flyTo
function gotoFly(longitude, latitude, altitude) {
    if (longitude === null || longitude === '' || latitude === null || latitude === '' || altitude === null || altitude === '') {
        alert(JS_MESSAGE["location.invalid"]);
        return;
    }

    gotoFlyAPI(MAGO3D_INSTANCE, longitude, latitude, 500, 3);
    hereIamMarker(longitude, latitude, altitude);
}

function flyTo(dataGroupId, dataKey) {
    if (dataGroupId === null || dataGroupId === '' || dataKey === null || dataKey === '') {
        alert(JS_MESSAGE["location.invalid"]);
        return;
    }

    //  searchDataAPI
    searchDataAPI(MAGO3D_INSTANCE, dataGroupId, dataKey);

    var node = MAGO3D_INSTANCE.getMagoManager().hierarchyManager.getNodeByDataKey(dataGroupId, dataKey);
    var geographic = node.data.bbox.geographicCoord;
    hereIamMarker(geographic.longitude, geographic.latitude, geographic.altitude);
}

var hereIamTimeOut;

function hereIamMarker(longitude, latitude, altitude) {
    var magoManager = MAGO3D_INSTANCE.getMagoManager();
    if (!magoManager.speechBubble) {
        magoManager.speechBubble = new Mago3D.SpeechBubble();
    }
    var sb = magoManager.speechBubble;
    removeHearIam();
    if (hereIamTimeOut) {
        clearTimeout(hereIamTimeOut);
    }
    var commentTextOption = {
        pixel: 12,
        color: 'black',
        borderColor: 'white',
        text: JS_MESSAGE["here.it.is"]
    }

    var img = sb.getPng([80, 32], '#94D8F6', commentTextOption);

    var options = {
        positionWC: Mago3D.ManagerUtils.geographicCoordToWorldPoint(longitude, latitude, parseFloat(altitude) + 5),
        imageFilePath: img
    };

    var omId = new Date().getTime();
    var om = magoManager.objMarkerManager.newObjectMarker(options, magoManager);
    om.id = omId;
    om.hereIam = true;

    var effectOption = {
        effectType: "zMovement",
        durationSeconds: 9.9,
        zVelocity: 100,
        zMax: 30,
        zMin: 0
    };
    var effect = new Mago3D.Effect(effectOption);
    magoManager.effectsManager.addEffect(omId, effect);

    hereIamTimeOut = setTimeout(function () {
        removeHearIam();
    }, 10000);

    function removeHearIam() {
        magoManager.objMarkerManager.setMarkerByCondition(function (om) {
            return !om.hereIam;
        });
    }
}

function flyToGroup(longitude, latitude, altitude, duration) {
    if (longitude === null || longitude === '' || latitude === null || latitude === '' || altitude === null || altitude === '') {
        alert(JS_MESSAGE["location.invalid"]);
        return;
    }
    gotoFlyAPI(MAGO3D_INSTANCE, parseFloat(longitude), parseFloat(latitude), parseFloat(altitude), parseFloat(duration));
}
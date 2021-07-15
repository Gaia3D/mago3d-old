package gaia3d.domain.layer;

import gaia3d.domain.cache.CacheManager;
import gaia3d.domain.policy.GeoPolicy;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GeoserverLayer {

    public CRSEnvelope getLayerBoundingBox(String layerName) {

        GeoPolicy geoPolicy = CacheManager.getGeoPolicy();
        String geoserverDataUrl = geoPolicy.getGeoserverDataUrl();

        URL url = null;
        try {
            url = new URL(String.format("%s/wms?version=1.1.1&request=getcapabilities&service=wms", geoserverDataUrl));
        } catch (MalformedURLException e) {
            //will not happen
        }

        WebMapServer wms;
        CRSEnvelope env = null;

        try {
            wms = new WebMapServer(url);
            WMSCapabilities capabilities = wms.getCapabilities();
            Layer[] layers = WMSUtils.getNamedLayers(capabilities);

            Layer layer = getLayerByName(layers, layerName);
            if (layer == null) return null;
            env = layer.getLatLonBoundingBox();

        } catch (IOException e) {
            //There was an error communicating with the server
            //For example, the server is down
        } catch (ServiceException e) {
            //The server returned a ServiceException (unusual in this case)
        }

        return env;

    }

    private Layer getLayerByName(Layer[] layers, String layerName) {
        Layer layer = null;
        for (int i = 0; i < layers.length; i++) {
            layer = layers[i];
            if (layer.getName().equals(layerName)) {
                return layer;
            }
        }
        return layer;
    }

}

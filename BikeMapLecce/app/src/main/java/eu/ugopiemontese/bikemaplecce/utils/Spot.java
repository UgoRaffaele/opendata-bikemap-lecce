package eu.ugopiemontese.bikemaplecce.utils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Spot {

    private String title = "BikeSharing";
    private double lat = 0.00;
    private double lng = 0.00;
    private LatLng latLng = new LatLng(0, 0);

    public Spot fromGeoJSONFeature(JSONObject feature) {
        try {
            JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
            this.lat = coordinates.getDouble(1);
            this.lng = coordinates.getDouble(0);
            this.latLng = new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public LatLng getPosition() {
        return this.latLng;
    }

}

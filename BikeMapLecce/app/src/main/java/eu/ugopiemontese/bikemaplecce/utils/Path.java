package eu.ugopiemontese.bikemaplecce.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Path {

    private PolylineOptions lineOptions = new PolylineOptions();

    public Path fromGeoJSONFeature(JSONObject feature) {
        try {
            JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
            for (int i = 0; i < coordinates.length(); i++) {
                lineOptions.add(new LatLng(coordinates.getJSONArray(i).getDouble(1), coordinates.getJSONArray(i).getDouble(0)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Path fromGeoJSONFeature(JSONObject feature, LatLngBounds.Builder builder) {
        try {
            JSONArray coordinates = feature.getJSONObject("geometry").getJSONArray("coordinates");
            for (int i = 0; i < coordinates.length(); i++) {
                lineOptions.add(new LatLng(coordinates.getJSONArray(i).getDouble(1), coordinates.getJSONArray(i).getDouble(0)));
                builder.include(new LatLng(coordinates.getJSONArray(i).getDouble(1), coordinates.getJSONArray(i).getDouble(0)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public PolylineOptions getLineOptions() {
        return this.lineOptions;
    }

}

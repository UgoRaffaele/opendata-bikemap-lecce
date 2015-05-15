package eu.ugopiemontese.bikemaplecce.utils;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class Utilities {

    public static int min_padding = 200;
    public static int min_zoom = 12;

    public static Double lecce_lat = 40.352011;
    public static Double lecce_lng = 18.169139;
    public static LatLng lecce = new LatLng(lecce_lat, lecce_lng);
    public static float max_distance = 15000;

    public static String loadJSONFromAsset(Context ctx, String name) throws JSONException {

        String json = null;
        try {
            InputStream is = ctx.getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static double distance(LatLng a, LatLng b) {

        Location locationA = new Location("A");
        locationA.setLatitude(a.latitude);
        locationA.setLongitude(a.longitude);

        Location locationB = new Location("B");
        locationB.setLatitude(b.latitude);
        locationB.setLongitude(b.longitude);

        return locationA.distanceTo(locationB);

    }

}

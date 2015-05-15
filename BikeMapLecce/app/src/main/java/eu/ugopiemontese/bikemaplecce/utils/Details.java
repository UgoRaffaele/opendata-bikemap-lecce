package eu.ugopiemontese.bikemaplecce.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Details {

    private String title = "BikeSharing";
    private String address = "";
    private LatLng latLng = new LatLng(0, 0);

    public Details fromLatLng(Context ctx, double lat, double lng) {
        this.latLng = new LatLng(lat, lng);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(ctx, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            this.address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAddress() {
        return this.address;
    }

    public LatLng getPosition() {
        return this.latLng;
    }

}

package eu.ugopiemontese.bikemaplecce;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import eu.ugopiemontese.bikemaplecce.utils.Path;
import eu.ugopiemontese.bikemaplecce.utils.Spot;
import eu.ugopiemontese.bikemaplecce.utils.Utilities;

public class MapActivity extends ActionBarActivity implements GoogleMap.OnCameraChangeListener, GoogleMap.OnMapLoadedCallback, GoogleMap.OnInfoWindowClickListener, LocationListener {

    private GoogleMap mMap;
    private LatLngBounds mBounds;
    private ArrayList<Marker> markerArrayList;

    private LocationManager locationManager;
    private SharedPreferences mSharedPreferences;
    private SwitchCompat mUserLocation;
    private Marker mUserMarker;

    private Criteria mDefaultCriteria = new Criteria();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mDefaultCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mDefaultCriteria.setAltitudeRequired(false);
        mDefaultCriteria.setBearingRequired(false);
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        mUserLocation = (SwitchCompat) menu.findItem(R.id.location_switch).getActionView().findViewById(R.id.switchView);
        setUpUserLocation();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_credits:
                startActivity(new Intent(MapActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpUserLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void showLocationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);
        dialog.setTitle(getString(android.R.string.dialog_alert_title));
        dialog.setMessage(getString(R.string.user_location_not_enabled));
        dialog.setPositiveButton(getString(R.string.user_location_open_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(mIntent);
            }
        });
        dialog.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                //TODO Auto-generated method stub
            }
        });
        dialog.show();
    }

    private void setUpUserLocation() {
        if (mUserLocation != null && mSharedPreferences != null) {
            mUserLocation.setChecked(mSharedPreferences.getBoolean("switch_gps", false));
            if (mUserLocation.isChecked()) {
                if (isLocationServiceEnabled()) {
                    locationManager.requestLocationUpdates(locationManager.getBestProvider(mDefaultCriteria, true), 0, 0, this);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastKnownLocation != null)
                        setUpUserMaker(lastKnownLocation);
                } else {
                    showLocationDialog();
                }
            }
            mUserLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (isLocationServiceEnabled()) {
                            locationManager.requestLocationUpdates(locationManager.getBestProvider(mDefaultCriteria, true), 0, 0, MapActivity.this);
                            mSharedPreferences.edit().putBoolean("switch_gps", true).commit();
                            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (lastKnownLocation != null)
                                setUpUserMaker(lastKnownLocation);
                        } else {
                            mUserLocation.setChecked(false);
                            showLocationDialog();
                        }
                    } else {
                        if (mUserMarker != null)
                            mUserMarker.remove();
                        locationManager.removeUpdates(MapActivity.this);
                        mSharedPreferences.edit().putBoolean("switch_gps", false).commit();
                    }
                }
            });
        }
    }

    private void setUpUserMaker(Location location) {

        if (mUserMarker != null)
            mUserMarker.remove();

        mUserMarker = mMap.addMarker(
            new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.user_location_marker))
        );

    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        LatLngBounds.Builder mBuilder = new LatLngBounds.Builder();
        markerArrayList = new ArrayList<Marker>();

        try {
            JSONObject postazioni = new JSONObject(Utilities.loadJSONFromAsset(getApplicationContext(), "postazioni.json"));
            JSONArray features = postazioni.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                Spot mSpot = new Spot().fromGeoJSONFeature(features.getJSONObject(i));
                mBuilder.include(mSpot.getPosition());
                Marker mMarker = mMap.addMarker(
                    new MarkerOptions()
                        .position(mSpot.getPosition())
                        .title(mSpot.getTitle())
                        .anchor(0.5f, 0.9f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
                );
                markerArrayList.add(mMarker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject ztl = new JSONObject(Utilities.loadJSONFromAsset(getApplicationContext(), "ztl.json"));
            JSONArray features = ztl.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                Path mPath = new Path().fromGeoJSONFeature(features.getJSONObject(i), mBuilder);
                Polyline mPolyline = mMap.addPolyline(
                    mPath.getLineOptions()
                    .color(getResources().getColor(R.color.primaryAlt))
                    .zIndex(0)
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject piste = new JSONObject(Utilities.loadJSONFromAsset(getApplicationContext(), "piste.json"));
            JSONArray features = piste.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                Path mPath = new Path().fromGeoJSONFeature(features.getJSONObject(i), mBuilder);
                Polyline mPolyline = mMap.addPolyline(
                    mPath.getLineOptions()
                    .color(getResources().getColor(R.color.primaryDark))
                    .zIndex(1)
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBounds = mBuilder.build();
        mMap.setOnMapLoadedCallback(this);

    }

    @Override
    public void onMapLoaded() {

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mBounds, Utilities.min_padding);
        if (mMap != null) {
            mMap.animateCamera(cameraUpdate);
            mMap.setOnCameraChangeListener(this);
            mMap.setOnInfoWindowClickListener(this);
        }

    }

    @Override
    public void onCameraChange(CameraPosition position) {

        boolean distance = Utilities.distance(position.target, Utilities.lecce) > Utilities.max_distance;
        boolean min_zoom = position.zoom < Utilities.min_zoom;
        //if too much out of Lecce, restore center and zoom
        if (distance || min_zoom) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mBounds, Utilities.min_padding);
            if (mMap != null) {
                mMap.animateCamera(cameraUpdate);
            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(MapActivity.this, DetailsActivity.class);
        i.putExtra("lat", marker.getPosition().latitude);
        i.putExtra("lng", marker.getPosition().longitude);
        startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (markerArrayList != null) {
            for (int i = 0; i < markerArrayList.size(); i++) {
                Location mrkLocation = new Location("A");
                mrkLocation.setLatitude(markerArrayList.get(i).getPosition().latitude);
                mrkLocation.setLongitude(markerArrayList.get(i).getPosition().longitude);
                markerArrayList.get(i).setSnippet((int) location.distanceTo(mrkLocation) + " metri");
            }
        }

        setUpUserMaker(location);

        if (mUserMarker != null) {
            boolean distance = Utilities.distance(mUserMarker.getPosition(), Utilities.lecce) > Utilities.max_distance;
            if (!distance) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(mUserMarker.getPosition());
                mMap.animateCamera(cameraUpdate);
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public boolean isLocationServiceEnabled() {
        String provider = locationManager.getBestProvider(mDefaultCriteria, true);
        return (provider.length() > 0 && !LocationManager.PASSIVE_PROVIDER.equals(provider));
    }

}

package eu.ugopiemontese.bikemaplecce;

import android.os.Bundle;
import android.view.Menu;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;

import eu.ugopiemontese.bikemaplecce.utils.Details;

public class DetailsActivity extends ActionBarActivity {

    private double mLat, mLng;
    private Details mDetails;

    private ShareActionProvider mShareActionProvider;
    private StreetViewPanorama mStreetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        mLat = extras.getDouble("lat");
        mLng = extras.getDouble("lng");

        mDetails = new Details().fromLatLng(getApplicationContext(), mLat, mLng);
        if (savedInstanceState == null) {
            getSupportActionBar().setSubtitle(mDetails.getAddress());
            mStreetView = ((StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetView)).getStreetViewPanorama();
            mStreetView.setPosition(mDetails.getPosition());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_item_share));
        setShareIntent(mDetails);
        return true;
    }

    private void setShareIntent(Details details) {
        if (mShareActionProvider != null && details != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            shareIntent.putExtra(Intent.EXTRA_TITLE, details.getTitle());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, details.getTitle());
            String text = details.getAddress();
            text += "\n" + details.getPosition().latitude + "," + details.getPosition().longitude;
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);

            shareIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

}

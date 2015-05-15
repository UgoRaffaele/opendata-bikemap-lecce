package eu.ugopiemontese.bikemaplecce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.support.v7.app.ActionBarActivity;

public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TextView textLink = (TextView) findViewById(R.id.textLink);
		textLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.credits_data_link)));
				startActivity(browserIntent);
			}
			
		});
		
		TextView textDevelopedBy = (TextView) findViewById(R.id.textDevelopedBy);
		textDevelopedBy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.developed_by_link)));
				startActivity(browserIntent);
			}
			
		});
		
	}
	
}

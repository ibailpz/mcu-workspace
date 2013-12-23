package es.deusto.p1justpark.faces;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;

public class ParkingView extends Activity {

	private Parking parking;

	private static final int settingsIntent = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_info);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			parking = (Parking) extras.getSerializable("parking");
			setParking(parking);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parking_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this,
					es.deusto.p1justpark.settings.MySettingsActivity.class);
			startActivityForResult(intent, settingsIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void setParking(Parking parking) {
		TextView ed1 = (TextView) findViewById(R.id.parking_name);
		TextView ed2 = (TextView) findViewById(R.id.parking_places);
		TextView ed3 = (TextView) findViewById(R.id.parking_address);
		TextView ed4 = (TextView) findViewById(R.id.coords);

		ed1.setText(parking.getName());
		ed1.setTypeface(null, Typeface.BOLD);
		ed2.setText(parking.getPlaces());
		ed2.setTypeface(null, Typeface.BOLD);
		ed3.setText(parking.getAddress());
		ed3.setTypeface(null, Typeface.BOLD);
		ed4.setText(parking.getLat() + "," + parking.getLng());
		ed3.setTypeface(null, Typeface.BOLD);
	}
}

package es.deusto.p1justpark.ui;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.ParkingsDatasource;

public class ParkingView extends Activity {

	private Parking parking;

	public static final String PARKING_KEY = "PARKING_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_info);

		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayShowTitleEnabled(false);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			parking = (Parking) extras.getParcelable(ParkingView.PARKING_KEY);
			setParking(parking);
		}
		// actionBar.setTitle(parking.getName());
	}

	@Override
	public void onBackPressed() {
		updateParking();
		super.onBackPressed();
	}

	private void setParking(Parking parking) {
		TextView ed1 = (TextView) findViewById(R.id.parking_name);
		TextView ed2 = (TextView) findViewById(R.id.parking_places);
		TextView ed3 = (TextView) findViewById(R.id.parking_last_updated);
		TextView ed4 = (TextView) findViewById(R.id.parking_address);
		TextView ed5 = (TextView) findViewById(R.id.coords);
		CheckBox cb = (CheckBox) findViewById(R.id.cb_notification);
		ImageView iv = (ImageView) findViewById(R.id.map);

		ed1.setText(parking.getName());
		// ed1.setTypeface(null, Typeface.BOLD);
		ed2.setText(parking.getPlaces());
		// ed2.setTypeface(null, Typeface.BOLD);
		String date = SimpleDateFormat.getDateTimeInstance(
				SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).format(
				parking.getLastUpdatedTime());
		ed3.setText(String.format(
				getResources().getString(R.string.last_updated_note), date));
		ed4.setText(parking.getAddress());
		// ed3.setTypeface(null, Typeface.BOLD);
		ed5.setText(parking.getLat() + ", " + parking.getLng());
		// ed3.setTypeface(null, Typeface.BOLD);
		cb.setChecked(parking.isNotifications());
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"switch_notifications", false)) {
			cb.setEnabled(false);
		}
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?q="
								+ ParkingView.this.parking.getLat() + ","
								+ ParkingView.this.parking.getLng()));
				startActivity(intent);
			}
		});
	}

	private void updateParking() {
		CheckBox notification = (CheckBox) findViewById(R.id.cb_notification);
		parking.setNotifications(notification.isChecked());
		ParkingsDatasource.getInstance().updateParkingSettings(parking);
	}
}

package es.deusto.p1justpark.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
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

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// parking = (Parking)
			// extras.getSerializable(ParkingView.PARKING_KEY);
			parking = (Parking) extras.getParcelable(ParkingView.PARKING_KEY);
			setParking(parking);
		}

	}

	@Override
	public void onBackPressed() {
		// FIXM Remove and save to BD
		updateParking();
		// Intent resultIntent = new Intent();
		// resultIntent.putExtra(ParkingView.PARKING_KEY,
		// (Serializable) newParking);
		// setResult(RESULT_OK, resultIntent);
		// finish();
		super.onBackPressed();
	}

	private void setParking(Parking parking) {
		TextView ed1 = (TextView) findViewById(R.id.parking_name);
		TextView ed2 = (TextView) findViewById(R.id.parking_places);
		TextView ed3 = (TextView) findViewById(R.id.parking_address);
		TextView ed4 = (TextView) findViewById(R.id.coords);
		CheckBox cb = (CheckBox) findViewById(R.id.cb_notification);

		ed1.setText(parking.getName());
		ed1.setTypeface(null, Typeface.BOLD);
		ed2.setText(parking.getPlaces());
		ed2.setTypeface(null, Typeface.BOLD);
		ed3.setText(parking.getAddress());
		ed3.setTypeface(null, Typeface.BOLD);
		ed4.setText(parking.getLat() + "," + parking.getLng());
		ed3.setTypeface(null, Typeface.BOLD);
		cb.setChecked(parking.isNotifications());
	}

	private void updateParking() {
		CheckBox notification = (CheckBox) findViewById(R.id.cb_notification);
		parking.setNotifications(notification.isChecked());
		ParkingsDatasource.getInstance().updateParking(parking);
	}
}

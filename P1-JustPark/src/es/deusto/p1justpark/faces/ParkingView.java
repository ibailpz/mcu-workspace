package es.deusto.p1justpark.faces;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;

public class ParkingView extends Activity{
	
	private Parking parking;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_info);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			parking = (Parking) extras.getSerializable("parking");
			setParking(parking);
		}
		
	}
	
	private void setParking(Parking parking) {
		TextView ed1 = (TextView) findViewById(R.id.parking_name);
		TextView ed2 = (TextView) findViewById(R.id.parking_places);
		TextView ed3 = (TextView) findViewById(R.id.parking_address);
		
		ed1.setText(parking.getName());
		ed1.setTypeface(null, Typeface.BOLD);
		ed2.setText(parking.getPlaces());
		ed2.setTypeface(null, Typeface.BOLD);
		ed3.setText(parking.getAddress());
		ed3.setTypeface(null, Typeface.BOLD);
	}
}

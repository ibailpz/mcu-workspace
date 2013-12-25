package es.deusto.p1justpark.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;

public class ParkingsAdapter extends ArrayAdapter<Parking> {

	public ParkingsAdapter(Context context, List<Parking> objects) {
		super(context, R.layout.parking_row, R.id.parking_name, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView text1 = (TextView) view.findViewById(R.id.parking_name);
		TextView text2 = (TextView) view.findViewById(R.id.parking_places);
		text1.setText(getItem(position).getName());
		text2.setText(getItem(position).getPlaces());
		return view;
	}

}

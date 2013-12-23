package es.deusto.p1justpark.ui;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;

public class ParkingsFragment extends ListFragment {

	public static final String PARKINGS_ARRAY = "PARKINGS_ARRAY";
	private static final int viewParking = 2;

	private AdapterObserver observer;
	private ArrayList<Parking> arrParkings;
	private ArrayAdapter<Parking> adpParkings;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		observer = ((AdapterObserver) activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		arrParkings = getArguments().getParcelableArrayList(PARKINGS_ARRAY);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adpParkings = new ArrayAdapter<Parking>(getActivity(),
				R.layout.parking_row, R.id.parking_name, arrParkings) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view
						.findViewById(R.id.parking_name);
				TextView text2 = (TextView) view
						.findViewById(R.id.parking_places);
				text1.setText(adpParkings.getItem(position).getName());
				text2.setText(adpParkings.getItem(position).getPlaces());
				return view;
			}
		};
		setListAdapter(adpParkings);
		observer.onAdapterChanged(adpParkings, getListView());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		viewParking(position);
		if (getListView().getCheckedItemPosition() > -1) {
			getListView().setItemChecked(
					getListView().getCheckedItemPosition(), false);
		}
	}

	private void viewParking(int position) {
		Parking parking = arrParkings.get(position);
		Intent intent = new Intent(getActivity(), ParkingView.class);
		intent.putExtra(ParkingView.PARKING_KEY, (Serializable) parking);
		startActivityForResult(intent, viewParking);
	}

}

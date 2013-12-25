package es.deusto.p1justpark.ui;

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
import es.deusto.p1justpark.data.Parking;

public class ParkingsFragment extends ListFragment {

	public static final String PARKINGS_ARRAY = "PARKINGS_ARRAY";

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
		adpParkings = new ParkingsAdapter(getActivity(), arrParkings);
		setListAdapter(adpParkings);
		observer.onAdapterChanged(adpParkings);
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
		intent.putExtra(ParkingView.PARKING_KEY, parking);
		// startActivityForResult(intent, viewParking);
		startActivity(intent);
	}

}

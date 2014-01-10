package es.deusto.p1justpark.ui;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.DatabaseObserver;
import es.deusto.p1justpark.db.ParkingsDatasource;
import es.deusto.p1justpark.services.ParkingUpdateService;

public class ParkingsFragment extends ListFragment implements
		OnRefreshListener, DatabaseObserver {

	public static final String PARKINGS_ARRAY = "PARKINGS_ARRAY";

	private PullToRefreshLayout mPullToRefreshLayout;
	private ViewGroup viewGroup;
	private Runnable hide = new Runnable() {

		@Override
		public void run() {
			transformer.hideHeaderView();
			setupPull();
		}
	};

	private AdapterObserver observer;
	private ArrayList<Parking> arrParkings;
	private ArrayAdapter<Parking> adpParkings;
	private DefaultHeaderTransformer transformer = new DefaultHeaderTransformer();

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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ParkingsDatasource.addDatabaseObserver(this);
		viewGroup = (ViewGroup) view;
		setupPull();
	}

	private void setupPull() {
		mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
		ActionBarPullToRefresh
				.from(getActivity())
				.insertLayoutInto(viewGroup)
				.theseChildrenArePullable(getListView(),
						getListView().getEmptyView())
				.options(
						Options.create().headerTransformer(transformer).build())
				.listener(this).setup(mPullToRefreshLayout);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adpParkings = new ParkingsAdapter(getActivity(), arrParkings);
		setListAdapter(adpParkings);
		observer.onAdapterChanged(adpParkings);
		Parking parking = getActivity().getIntent().getParcelableExtra(
				ParkingsActivity.LAUNCH_PARKING_KEY);
		if (parking != null) {
			getActivity().getIntent().putExtra(
					ParkingsActivity.LAUNCH_PARKING_KEY, (Parcelable) null);
			viewParking(parking);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		viewParking(arrParkings.get(position));
		if (getListView().getCheckedItemPosition() > -1) {
			getListView().setItemChecked(
					getListView().getCheckedItemPosition(), false);
		}
	}

	private void viewParking(Parking parking) {
		Intent intent = new Intent(getActivity(), ParkingView.class);
		intent.putExtra(ParkingView.PARKING_KEY, parking);
		startActivity(intent);
	}

	@Override
	public void onRefreshStarted(View view) {
		getActivity().startService(
				new Intent(getActivity(), ParkingUpdateService.class));
	}

	@Override
	public void onUpdate() {
		getActivity().runOnUiThread(hide);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ParkingsDatasource.removeDatabaseObserver(this);
	}

}

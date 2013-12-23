package es.deusto.p1justpark.faces;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;

public class ParkingsList extends ListActivity implements
		ActionBar.OnNavigationListener {

	private ArrayList<Parking> arrParkings;
	private ArrayList<Parking> arrFavoriteParkings;
	private ArrayAdapter<Parking> adpParkings;

	private ActionMode mActionMode;

	private static final String STATE_TYPE_LIST = "all_or_favorites";
	private static final int settingsIntent = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		setActionBar(actionBar);

		createParkingsList();		

		adpParkings = new ArrayAdapter<Parking>(this, R.layout.parking_row,
				R.id.parking_name, arrParkings) {
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
		
		setCAB();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parking_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mnu_search_location) {

		} else if (id == R.id.mnu_view_map) {

		} else if (id == R.id.action_settings) {
			Intent intent = new Intent(this, es.deusto.p1justpark.settings.MySettingsActivity.class);
			startActivityForResult(intent, settingsIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			ShareActionProvider shareProv = (ShareActionProvider) menu
					.findItem(R.id.parking_share).getActionProvider();
			shareProv
					.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(
					Intent.EXTRA_TEXT,
					arrParkings.get(getListView().getCheckedItemPosition())
							.getName()
							+ " "
							+ arrParkings.get(
									getListView().getCheckedItemPosition())
									.getPlaces());
			shareProv.setShareIntent(intent);

			ParkingsList.this.getListView().setEnabled(false);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

			if (ParkingsList.this.getListView().getCheckedItemPosition() >= 0)
				ParkingsList.this.getListView().setItemChecked(
						ParkingsList.this.getListView()
								.getCheckedItemPosition(), false);
			ParkingsList.this.getListView().setEnabled(true);
			mActionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.parking_action, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			final int position = ParkingsList.this.getListView()
					.getCheckedItemPosition();

			switch (item.getItemId()) {
			case R.id.parking_favorite:
				if (arrFavoriteParkings == null) {
					arrFavoriteParkings = new ArrayList<Parking>();
				}
				arrFavoriteParkings.add(arrParkings.get(position));
				mode.finish();
				return true;
			case R.id.parking_navigate:
				// TODO Navigate to parking
				mode.finish();
				return true;
			case R.id.parking_share:
				return true;
			default:
				return false;
			}
		}
	};

	private void setCAB() {
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if (mActionMode != null) {
							return false;
						}
						getListView().setItemChecked(position, true);
						mActionMode = ParkingsList.this
								.startActionMode(mActionModeCallback);
						return true;
					}
				});
	}

	private void createParkingsList() {
		if (arrParkings == null) {
			arrParkings = new ArrayList<Parking>();
			arrParkings.add(new Parking(1, "Parking Plaza Euskadi",
					"Plaza Euskadi Bilbao", "100"));
			arrParkings.add(new Parking(2, "Parking Diputacion",
					"Gran Via 19, Bilbao", "50"));
		}

		if (arrFavoriteParkings == null) {
			arrFavoriteParkings = new ArrayList<Parking>();
			arrFavoriteParkings.add(new Parking(1, "Parking Plaza Euskadi",
					"Plaza Euskadi Bilbao", "100"));
		}
	}

	private void viewParking(int position) {
		Parking parking = arrParkings.get(position);
		Intent intent = new Intent(this, ParkingView.class);
		intent.putExtra("parking", parking);
		startActivity(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		viewParking(position);
		super.onListItemClick(l, v, position, id);
	}

	private void setActionBar(ActionBar actionBar) {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final String[] dropdownValues = { "All", "Favorites" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getActionBarThemedContextCompat(),
				android.R.layout.simple_spinner_item, android.R.id.text1,
				dropdownValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(adapter, this);

		onNavigationItemSelected(0, 0);
	}

	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_TYPE_LIST)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_TYPE_LIST));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_TYPE_LIST, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		switch (position) {
		case 0:
			// TODO Inflate adapter with arrParkings
			break;
		case 1:
			// TODO Inflate adapter with arrFavoriteParkings
			break;
		}
		return true;
	}

}

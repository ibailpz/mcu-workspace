package es.deusto.p1justpark.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import es.deusto.p1justpark.R;
import es.deusto.p1justpark.data.Parking;
import es.deusto.p1justpark.db.DatabaseObserver;
import es.deusto.p1justpark.db.ParkingsDatasource;

public class ParkingsActivity extends Activity implements
		ActionBar.OnNavigationListener, AdapterObserver, DatabaseObserver {

	private static final int settingsIntent = 1;
	private static final String STATE_TYPE_LIST = "all_or_favorites";

	private ArrayList<Parking> arrParkings;
	private ArrayList<Parking> arrFavoriteParkings;

	private Fragment list;
	private Fragment favs;
	private ArrayAdapter<Parking> currentAdapter;
	private ActionMode mActionMode;
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
					currentAdapter.getItem(
							((ListView) findViewById(android.R.id.list))
									.getCheckedItemPosition()).getName()
							+ " "
							+ currentAdapter
									.getItem(
											((ListView) findViewById(android.R.id.list))
													.getCheckedItemPosition())
									.getPlaces());
			shareProv.setShareIntent(intent);

			((ListView) findViewById(android.R.id.list)).setEnabled(false);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (((ListView) findViewById(android.R.id.list))
					.getCheckedItemPosition() >= 0)
				((ListView) findViewById(android.R.id.list)).setItemChecked(
						((ListView) findViewById(android.R.id.list))
								.getCheckedItemPosition(), false);
			((ListView) findViewById(android.R.id.list)).setEnabled(true);
			mActionMode = null;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.parking_action, menu);
			int position = ((ListView) findViewById(android.R.id.list))
					.getCheckedItemPosition();
			if (position >= 0
					&& !currentAdapter.getItem(position).isFavourite()) {
				menu.findItem(R.id.parking_favorite).setIcon(
						R.drawable.ic_action_not_favorite);
				menu.findItem(R.id.parking_favorite).setTitle(
						R.string.parking_add_favorite);
			}
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			final int position = ((ListView) findViewById(android.R.id.list))
					.getCheckedItemPosition();

			switch (item.getItemId()) {
			case R.id.parking_favorite:
				// FIXM Change favourite state
				Parking parking = currentAdapter.getItem(position);
				if (parking.isFavourite()) {
					parking.setFavourite(false);
					Toast.makeText(
							ParkingsActivity.this,
							String.format(
									getResources().getString(
											R.string.remove_favourite),
									parking.getName()), Toast.LENGTH_SHORT)
							.show();
				} else {
					parking.setFavourite(true);
					Toast.makeText(
							ParkingsActivity.this,
							String.format(
									getResources().getString(
											R.string.added_favourite),
									parking.getName()), Toast.LENGTH_SHORT)
							.show();
				}
				ParkingsDatasource.getInstance().updateParking(parking);
				mode.finish();
				return true;
			case R.id.parking_navigate:
				// FIXME Navigate to parking
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?q="
								+ currentAdapter.getItem(position).getLat()
								+ ","
								+ currentAdapter.getItem(position).getLng()));
				startActivity(intent);
				mode.finish();
				return true;
			case R.id.parking_share:
				return true;
			default:
				return false;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ParkingsDatasource.addDatabaseObserver(this);
		ParkingsDatasource.initDatasource(this);
		createParkingsList();
		loadParkings();
		setActionBar(getActionBar());
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
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
			Intent intent = new Intent(this,
					es.deusto.p1justpark.settings.SettingsActivity.class);
			startActivityForResult(intent, settingsIntent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void setActionBar(ActionBar actionBar) {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final String[] dropdownValues = { "All", "Favorites" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActionBar()
				.getThemedContext(), android.R.layout.simple_spinner_item,
				android.R.id.text1, dropdownValues);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(adapter, this);

		onNavigationItemSelected(0, 0);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Fragment fragment;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			// TOD Inflate adapter with arrParkings from BD
			if (list == null) {
				list = new ParkingsFragment();
				args.putParcelableArrayList(ParkingsFragment.PARKINGS_ARRAY,
						arrParkings);
				list.setArguments(args);
			} else {
				list.getArguments().putParcelableArrayList(
						ParkingsFragment.PARKINGS_ARRAY, arrParkings);
			}
			fragment = list;
			break;
		case 1:
			// TOD Inflate adapter with arrFavoriteParkings from BD
			args.putParcelableArrayList(ParkingsFragment.PARKINGS_ARRAY,
					arrFavoriteParkings);
			if (favs == null) {
				favs = new ParkingsFragment();
				args.putParcelableArrayList(ParkingsFragment.PARKINGS_ARRAY,
						arrFavoriteParkings);
				favs.setArguments(args);
			} else {
				favs.getArguments().putParcelableArrayList(
						ParkingsFragment.PARKINGS_ARRAY, arrFavoriteParkings);
			}
			fragment = favs;
			break;
		default:
			fragment = list;
		}
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
		return true;
	}

	private void setCAB() {
		((ListView) findViewById(android.R.id.list))
				.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		((ListView) findViewById(android.R.id.list))
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if (mActionMode != null) {
							return false;
						}
						((ListView) findViewById(android.R.id.list))
								.setItemChecked(position, true);
						mActionMode = ParkingsActivity.this
								.startActionMode(mActionModeCallback);
						return true;
					}
				});
	}

	private void loadParkings() {
		if (arrParkings == null) {
			arrParkings = new ArrayList<Parking>();
		}
		arrParkings.clear();
		arrParkings.addAll(ParkingsDatasource.getInstance().getAllParkings());
		if (arrFavoriteParkings == null) {
			arrFavoriteParkings = new ArrayList<Parking>();
		}
		arrFavoriteParkings.clear();
		arrFavoriteParkings.addAll(ParkingsDatasource.getInstance()
				.getFavouriteParkings());
	}

	@Override
	public void onAdapterChanged(ArrayAdapter<Parking> adapter) {
		currentAdapter = adapter;
		setCAB();
	}

	@Override
	public void onUpdate() {
		loadParkings();
		if (currentAdapter != null) {
			currentAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Log.i("Pause", "Saving data");
		// (new
		// ParkingManager(getApplicationContext())).saveParkings(arrParkings);
		// (new ParkingManager(getApplicationContext()))
		// .saveFavoriteParkings(arrFavoriteParkings);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ParkingsDatasource.getInstance().close();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized dropdown position
		if (savedInstanceState.containsKey(STATE_TYPE_LIST)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_TYPE_LIST));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position
		outState.putInt(STATE_TYPE_LIST, getActionBar()
				.getSelectedNavigationIndex());
	}

	private void createParkingsList() {
		if (ParkingsDatasource.getInstance().getAllParkings().isEmpty()) {
			ArrayList<Parking> list = new ArrayList<Parking>();
			list.add(new Parking(1, "Parking Plaza Euskadi",
					"Plaza Euskadi Bilbao", "100", 43.26723, -2.93839, false,
					false));
			list.add(new Parking(2, "Parking El Corte Ingles",
					"Gran Via 19, Bilbao", "50", 43.18474, -2.47936, false,
					true));
			for (Parking p : list) {
				ParkingsDatasource.getInstance().createParking(p);
			}
		}
	}
}
